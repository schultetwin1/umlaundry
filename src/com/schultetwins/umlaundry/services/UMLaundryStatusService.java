package com.schultetwins.umlaundry.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.schultetwins.umlaundry.UMLaundryDataAccessor;
import com.schultetwins.umlaundry.UMLaundryDataAccessor.Machine;
import com.schultetwins.umlaundry.activities.UofMLaundryBuildingsActivity;
import com.schultetwins.umlaundry.activities.UofMLaundryMachinesActivity;
import com.schultetwins.umlaundry.activities.UofMLaundryRoomsActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

public class UMLaundryStatusService extends Service {

	private static final String TAG = "UMLaundryStatusService";
	private Looper serviceLooper;
	private ServiceHandler serviceHandler;
	
	// Handler to receive messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			UMLaundryDataAccessor dataAccessor = new UMLaundryDataAccessor();
			try {
				List<Machine> machines = dataAccessor.getMachines(
						msg.getData().getInt(UofMLaundryBuildingsActivity.BUILDING_CODE),
						msg.getData().getInt(UofMLaundryRoomsActivity.ROOM_CODE)
				);
				Machine machine = machines.get(msg.getData().getInt(UofMLaundryMachinesActivity.MACHINE_CODE));
				String status = machine.getStatus();
				Toast.makeText(UMLaundryStatusService.this, "service done" + status, Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stopSelf(msg.arg1);
		}
	}
	
	@Override
	public void onCreate() {
		HandlerThread thread = new HandlerThread("UMLaundryStatus", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		
		serviceLooper = thread.getLooper();
		serviceHandler = new ServiceHandler(serviceLooper);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting "  + startId, Toast.LENGTH_SHORT).show();
		
		Message msg = serviceHandler.obtainMessage();
		msg.arg1 = startId;
		Log.i(TAG, "About to Check");
		Log.i(TAG, intent.getExtras().get(UofMLaundryBuildingsActivity.BUILDING_CODE)+"");
		msg.setData(intent.getExtras());
		serviceHandler.sendMessage(msg);
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show(); 
	}

}
