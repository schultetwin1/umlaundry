package com.schultetwins.umlaundry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.util.Log;

public class UMLaundryDataAccessor {
	public static final String BUILDINGSURL = "http://housing.umich.edu/laundry-locator/locations/RAND_INT";
	public static final String ROOMSURL     = "http://housing.umich.edu/laundry-locator/rooms/BUILDING_CODE/RAND_INT";
	public static final String MACHINESURL  = "http://housing.umich.edu/laundry-locator/report/BUILDING_CODE/ROOM_CODE/STATUS_CODE/RAND_INT";
	private static final String TAG = "UMLaundryDataAccessor";
	
	private Context context;
	private boolean force = false;
	
	public UMLaundryDataAccessor () {
		this.context = null;
	}
	
	public UMLaundryDataAccessor(Context context) {
		this.context = context;
	}
	
	public UMLaundryDataAccessor(Context context, boolean force) {
		this.context = context;
		this.force = force;
	}
	
	public List<Building> getBuildings() throws IOException, JSONException {
		JSONObject buildingsJSON = null;
		// Check for cached results and load automatically
		File file = context.getFileStreamPath("buildings.txt");
		if (file.exists() && !force) {
			// Check to see if its not older than a day
			Date lastModDate = new Date(file.lastModified());
			if ((new Date()).getTime() - lastModDate.getTime() < 1000 * 60 * 60 * 24) {
				try {
					FileInputStream fis = context.openFileInput("buildings.txt");
					buildingsJSON = new JSONObject(convertStreamToString(fis));
					return parseBuildingJSON(buildingsJSON);
				} catch (IOException e) {
					// fail silently
				} catch (JSONException e) {
					// fail silently
				}
			}
		}
		String url = BUILDINGSURL.replace("RAND_INT", System.currentTimeMillis() + "");
		InputStream stream = downloadUrl(url);
		buildingsJSON = new JSONObject(convertStreamToString(stream));
		// Cache results
		try {
			FileOutputStream fos = context.openFileOutput("buildings.txt", Context.MODE_PRIVATE);
			fos.write(buildingsJSON.toString().getBytes());
			fos.close();
		} catch (IOException e) {
			// Silently fails FOR NOW!
			//@TODO
		}

		return parseBuildingJSON(buildingsJSON);
	}
	
	public List<Room> getRooms(int building_code) throws IOException, JSONException{
		JSONObject roomsJSON = null;
		// Check for cached results and load automatically
		File file = context.getFileStreamPath("rooms"+building_code+".txt");
		if (file.exists() && !force) {
			// Check to see if its not older than a day
			Date lastModDate = new Date(file.lastModified());
			if ((new Date()).getTime() - lastModDate.getTime() < 1000 * 60 * 60 * 24) {
				try {
					FileInputStream fis = context.openFileInput("rooms"+building_code+".txt");
					roomsJSON = new JSONObject(convertStreamToString(fis));
					return parseRoomJSON(roomsJSON);
				} catch (IOException e) {
					// fail silently
					// but continue on to download from Web
				} catch (JSONException e) {
					// fail silently
					// but continue on to download from Web
				}
			}
		}
		String url = ROOMSURL.replace("RAND_INT", System.currentTimeMillis() + "");
		url = url.replace("BUILDING_CODE", building_code+"");
		InputStream stream = downloadUrl(url);
		roomsJSON = new JSONObject(convertStreamToString(stream));
		// Cache results
		try {
			FileOutputStream fos = context.openFileOutput("rooms"+building_code+".txt", Context.MODE_PRIVATE);
			fos.write(roomsJSON.toString().getBytes());
			fos.close();
		} catch (IOException e) {
			// Silently fails FOR NOW!
			//@TODO
		}
		return parseRoomJSON(roomsJSON);
	}
	
	// Should not be cached
	public List<Machine> getMachines(int building_code, int machine_id) throws IOException {
		String url = MACHINESURL.replace("RAND_INT", System.currentTimeMillis() + "");
		url = url.replace("BUILDING_CODE", building_code+"");
		url = url.replace("ROOM_CODE", machine_id+"");
		url = url.replace("STATUS_CODE", 0+"");
		Document doc = Jsoup.connect(url).get();
		return parseMachinesHTML(doc);
	}
	
	private List<Building> parseBuildingJSON(JSONObject buildingsJSON) throws JSONException {
		List<Building> buildings = new ArrayList<Building>();
		JSONArray buildingsArray = buildingsJSON.getJSONArray("locations");
		for (int i=0; i<buildingsArray.length(); i++) {
			if (!buildingsArray.isNull(i)) {
				String name = buildingsArray.getJSONObject(i).getJSONObject("loc").getString("building");
				int code = buildingsArray.getJSONObject(i).getJSONObject("loc").getInt("code");
				buildings.add(new Building(name, code));
			}
		}
		return buildings;
	}
	
	private List<Room> parseRoomJSON(JSONObject roomsJSON) throws JSONException {
		List<Room> rooms = new ArrayList<Room>();
		JSONArray roomsArray = roomsJSON.getJSONArray("rooms");
		for (int i=0; i < roomsArray.length(); i++) {
			if (!roomsArray.isNull(i)) {
				String name = roomsArray.getJSONObject(i).getJSONObject("room").getString("name");
				int code = roomsArray.getJSONObject(i).getJSONObject("room").getInt("code");
				rooms.add(new Room(name, code));
			}
		}
		return rooms;
	}
	
	private List<Machine> parseMachinesHTML(Document machinesHMTL) {
		List<Machine> machines = new ArrayList<Machine>();
		Element machinesTable = machinesHMTL.select("table[class=mach_disp]").first();
		Iterator<Element> machineRows = machinesTable.select("tr").iterator();
		// First row is a header for the table
		machineRows.next();
		
		boolean areOffline = true;

		while (machineRows.hasNext()) {
			Iterator<Element> machineRow = machineRows.next().select("td").iterator();
			int id = Integer.parseInt(machineRow.next().text());
			String type = machineRow.next().text();
			String status = machineRow.next().text();
			String time_str = machineRow.next().text();
			int time = Integer.parseInt(time_str.replace("m", ""));
			if (!(areOffline && status.equals("In Use") && time == 0)) {
				areOffline = false;
			}
			
			machines.add(new Machine(id, type, status, time));
		}
		if (areOffline) {
			for (int i = 0; i < machines.size(); i++) {
				machines.get(i).setStatus("Offline");
			}
		}
		
		return machines;
	}
	
	private String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			 
			char[] buffer = new char[1024];
			 
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				} 
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
	
	// Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException, JSONException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
	
	// This class represents a single building in the JSON feed.
	// It includes the data members "name" and buildingCode."
	public static class Building {
		public final String name;
		public final int code;

		public Building(String name, int code) {
			this.name = name;
		    this.code = code;
		}
	}
	
	public static class Room {
		public final String name;
		public final int code;
		
		public Room(String name, int code) {
			this.name = name;
			this.code = code;
		}
	}
	
	final public class Machine {
		public final int ID;
		public final String type;
		private String status;
		private int timeRemaining;
		
		public Machine(int id, String type, String status, int timeRemaining) {
			this.ID = id;
			this.type = type;
			this.status = status;
			this.timeRemaining = timeRemaining;
		}
		
		public void setStatus(String status) {
			this.status = status;
		}
		
		public void setTimeRemaining(int time) {
			this.timeRemaining = time;
		}
		
		public String getStatus() {
			return this.status;
		}
		
		public int getTimeRemaining() {
			return this.timeRemaining;
		}
	}
}
