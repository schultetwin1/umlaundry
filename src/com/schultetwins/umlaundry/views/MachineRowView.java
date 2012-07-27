package com.schultetwins.umlaundry.views;

import com.schultetwins.umlaundry.R;
import com.schultetwins.umlaundry.UMLaundryDataAccessor;
import com.schultetwins.umlaundry.R.id;
import com.schultetwins.umlaundry.R.layout;
import com.schultetwins.umlaundry.UMLaundryDataAccessor.Machine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MachineRowView extends RelativeLayout {
	
	private final String TAG = "MachineRowView";
	
	private TextView machineStatusTextView;
	private TextView machineNumberTextView;
	private TextView machineTypeTextView;

	public MachineRowView(Context context) {
		super(context);
		
		// Inflate the XML resource
		LayoutInflater buildingsInflater = ((Activity)context).getLayoutInflater();
		buildingsInflater.inflate(R.layout.machineview, this);
		
		// Capture the text views
		this.machineStatusTextView = (TextView) findViewById(R.id.machine_status);
		this.machineNumberTextView = (TextView) findViewById(R.id.machine_number);
		this.machineTypeTextView = (TextView) findViewById(R.id.machine_type);
		
	}
	
	public void setMachineText(Machine machine) {
		this.machineNumberTextView.setText(machine.ID+"");
		this.machineTypeTextView.setText(machine.type);
		this.machineStatusTextView.setText(machine.getStatus());
		this.setClickable(true);
		if (machine.getStatus().equals("In Use")) {
			this.machineStatusTextView.setTextColor(Color.RED);
			this.machineStatusTextView.setText(machine.getTimeRemaining() + ":00");
			// this.setClickable(true);
		} else if (machine.getStatus().equals("Available")) {
			this.machineStatusTextView.setTextColor(Color.GREEN);
		} else if (machine.getStatus().equals("Offline")) {
			this.machineStatusTextView.setTextColor(Color.GRAY);
		} else {
			Log.i(TAG, "Unknown status" + machine.getStatus());
			this.machineStatusTextView.setTextColor(Color.WHITE);
		}
	}
	
}
