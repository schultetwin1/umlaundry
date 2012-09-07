package com.schultetwins.umlaundry.activities;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.schultetwins.umlaundry.R;
import com.schultetwins.umlaundry.UMLaundryDataAccessor;
import com.schultetwins.umlaundry.UMLaundryDataAccessor.Machine;
import com.schultetwins.umlaundry.adapters.MachineListAdapter;
import com.schultetwins.umlaundry.services.UMLaundryStatusService;

public class UofMLaundryMachinesActivity extends UofMLaundryBaseActivity{
	private static final String TAG = "UofMMachineLaundryActivity";	
	
    // Listview for buildings
    private MachineListAdapter machinesListAdapter;
    
    // Selected building
    private int selectedBuildingCode;
    
    // Selected room
    private int selectedRoomCode;
    
    // Handler to update every min
    private Handler handler;
    private Runnable runnable;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        selectedBuildingCode = intent.getIntExtra(UofMLaundryBuildingsActivity.BUILDING_CODE, -1);
        selectedRoomCode = intent.getIntExtra(UofMLaundryRoomsActivity.ROOM_CODE, -1);
        
        // Assign the list view
        final ListView buildingsListView = (ListView)findViewById(R.id.buildings_list);
        
        // Create adapter
        this.machinesListAdapter = new MachineListAdapter(this);
        
        // Bind adapter to adapter view
        buildingsListView.setAdapter(machinesListAdapter);
        
        // Set listener for click
        buildingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(UofMLaundryMachinesActivity.this, UMLaundryStatusService.class);
				Bundle machineData = new Bundle();
				machineData.putInt(UofMLaundryBuildingsActivity.BUILDING_CODE, UofMLaundryMachinesActivity.this.selectedBuildingCode);
				machineData.putInt(UofMLaundryRoomsActivity.ROOM_CODE, UofMLaundryMachinesActivity.this.selectedRoomCode);
				machineData.putInt(UofMLaundryMachinesActivity.MACHINE_CODE, position);
				intent.putExtras(machineData);
				startService(intent);
			}
        });
        
        List<Machine> machines = new ArrayList<Machine>();
        machinesListAdapter.setItems(machines);
        machinesListAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	this.handler = new Handler();
        this.runnable = new Runnable() {

            public void run() {

                //call the function
                loadPage(true);
                Log.i(TAG, "Reloaded Page");
                //also call the same runnable
                handler.postDelayed(this, 60000 /* 1 min */);
            }
        };
        // Called once to get started 
        this.handler.postDelayed(runnable, 60000 /* 1 min */);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	this.handler.removeCallbacks(runnable);
    }
    
    // Uses AsyncTask subclass to download the XML feed from stackoverflow.com.
    // This avoids UI lock up. To prevent network operations from
    // causing a delay that results in a poor user experience, always perform
    // network operations on a separate thread from the UI.
    protected void loadPage(boolean force) {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
            new DownloadJSONTask().execute(selectedBuildingCode, selectedRoomCode);
        } else {
            showErrorPage("No Data");
        }
        machinesListAdapter.notifyDataSetChanged();
    }
    
    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadJSONTask extends AsyncTask<Integer, Void, List<Machine>> {
 

        @Override
        protected List<Machine> doInBackground(Integer... params) {
        	UMLaundryDataAccessor dataAccessor = new UMLaundryDataAccessor();
            try {
                return dataAccessor.getMachines(params[0], params[1]);
            } catch (IOException e) {
            	Log.d(TAG, "IO ERROR", e);
                return new ArrayList<Machine>();
            }
        }

        @Override
        protected void onPostExecute(List<Machine> result) {
        	machinesListAdapter.setItems(result);
        }
    }

}