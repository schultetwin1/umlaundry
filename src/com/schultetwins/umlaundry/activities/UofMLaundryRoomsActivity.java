package com.schultetwins.umlaundry.activities;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.schultetwins.umlaundry.R;
import com.schultetwins.umlaundry.UMLaundryDataAccessor;
import com.schultetwins.umlaundry.UMLaundryDataAccessor.Room;
import com.schultetwins.umlaundry.adapters.RoomListAdapter;

public class UofMLaundryRoomsActivity extends UofMLaundryBaseActivity {
	private static final String TAG = "UofMLaundryActivity";
	
    // Listview for buildings
    private RoomListAdapter roomsListAdapter;
    
    // Selected room
    private int selectedBuildingCode;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        selectedBuildingCode = intent.getIntExtra(UofMLaundryBuildingsActivity.BUILDING_CODE, -1);
        wifiConnected = intent.getBooleanExtra(WIFI_STATUS, false);
        mobileConnected = intent.getBooleanExtra(MOBILE_STATUS, false); 
        // Assign the list view
        final ListView roomsListView = (ListView)findViewById(R.id.buildings_list);
        
        // Create adapter
        this.roomsListAdapter = new RoomListAdapter(this);
        
        // Bind adapter to adapter view
        roomsListView.setAdapter(roomsListAdapter);
        
        // Set listener for click
        roomsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(UofMLaundryRoomsActivity.this, UofMLaundryMachinesActivity.class);
				intent.putExtra(UofMLaundryBuildingsActivity.BUILDING_CODE, selectedBuildingCode);
				intent.putExtra(ROOM_CODE, roomsListAdapter.getItem(position).code);
				startActivity(intent);
			}
        });
        
        List<Room> rooms = new ArrayList<Room>();
        rooms.add(new Room("Loading...", 1));
        
        roomsListAdapter.setItems(rooms);
        roomsListAdapter.notifyDataSetChanged();
    }
    
    // Uses AsyncTask subclass to download the XML feed from stackoverflow.com.
    // This avoids UI lock up. To prevent network operations from
    // causing a delay that results in a poor user experience, always perform
    // network operations on a separate thread from the UI.
    protected void loadPage(boolean force) {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
            new DownloadJSONTask(force).execute(selectedBuildingCode);
        } else {
            showErrorPage("No Data");
        }
        roomsListAdapter.notifyDataSetChanged();
    }
    
    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadJSONTask extends AsyncTask<Integer, Void, List<Room>> {
    	private boolean force = false;
    	
    	public DownloadJSONTask(boolean force) {
    		this.force = force;
    	}
 

        @Override
        protected List<Room> doInBackground(Integer...params) {
        	UMLaundryDataAccessor dataAccessor = new UMLaundryDataAccessor(UofMLaundryRoomsActivity.this, force);
            try {
                return dataAccessor.getRooms(params[0]);
            } catch (IOException e) {
            	Log.d(TAG, "IO ERROR", e);
                return new ArrayList<Room>();
            } catch (JSONException e) {
            	Log.d(TAG, "JSON PARSING ERROR", e);
            	return new ArrayList<Room>();
            }
        }

        @Override
        protected void onPostExecute(List<Room> result) {
        	roomsListAdapter.setItems(result);
            
        }
    }
}