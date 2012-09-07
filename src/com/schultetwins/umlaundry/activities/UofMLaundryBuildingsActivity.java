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
import com.schultetwins.umlaundry.UMLaundryDataAccessor.Building;
import com.schultetwins.umlaundry.adapters.BuildingListAdapter;

public class UofMLaundryBuildingsActivity extends UofMLaundryBaseActivity {
	private static final String TAG = "UofMLaundryActivity";

    // Listview for buildings
    private BuildingListAdapter buildingsListAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Assign the list view
        final ListView buildingsListView = (ListView)findViewById(R.id.buildings_list);
        
        // Create adapter
        this.buildingsListAdapter = new BuildingListAdapter(this);
        
        // Bind adapter to adapter view
        buildingsListView.setAdapter(buildingsListAdapter);
        
        // Set listener for click
        buildingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(UofMLaundryBuildingsActivity.this, UofMLaundryRoomsActivity.class);
				intent.putExtra(BUILDING_CODE, buildingsListAdapter.getItem(position).code);
				intent.putExtra(WIFI_STATUS, wifiConnected);
				intent.putExtra(MOBILE_STATUS, mobileConnected);
				startActivity(intent);
			}

        	
        });

    }
    
    // Uses AsyncTask subclass to download the XML feed from stackoverflow.com.
    // This avoids UI lock up. To prevent network operations from
    // causing a delay that results in a poor user experience, always perform
    // network operations on a separate thread from the UI.
    protected void loadPage(boolean force) {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
            new DownloadJSONTask(force).execute();
        } else {
            showErrorPage("Not Connected");
        }
        buildingsListAdapter.notifyDataSetChanged();
    }
    
    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadJSONTask extends AsyncTask<Integer, Void, List<Building>> {
    	
    	private boolean force = false;
    	
    	public DownloadJSONTask(boolean force) {
    		this.force = force;
    	}
    	
        @Override
        protected List<Building> doInBackground(Integer... params) {
        	UMLaundryDataAccessor dataAccessor = new UMLaundryDataAccessor(UofMLaundryBuildingsActivity.this, force);
            try {
                return dataAccessor.getBuildings();
            } catch (IOException e) {
            	Log.d(TAG, "IO ERROR", e);
                return new ArrayList<Building>();
            } catch (JSONException e) {
            	Log.d(TAG, "JSON PARSING ERROR", e);
            	return new ArrayList<Building>();
            }
        }

        @Override
        protected void onPostExecute(List<Building> result) {
        	buildingsListAdapter.setItems(result);
            
        }
    }
    
}