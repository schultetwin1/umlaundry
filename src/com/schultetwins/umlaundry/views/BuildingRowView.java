package com.schultetwins.umlaundry.views;

import com.schultetwins.umlaundry.R;
import com.schultetwins.umlaundry.R.id;
import com.schultetwins.umlaundry.R.layout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BuildingRowView extends LinearLayout {
	
	private TextView buildingNameTextView;

	public BuildingRowView(Context context) {
		super(context);
		
		// Inflate the XML resource
		LayoutInflater buildingsInflater = ((Activity)context).getLayoutInflater();
		buildingsInflater.inflate(R.layout.buildingview, this);
		
		// Capture the text views
		this.buildingNameTextView = (TextView) findViewById(R.id.building_name);
	}
	
	public void setBuildingNameText(String name) {
		this.buildingNameTextView.setText(name);
	}
	
}
