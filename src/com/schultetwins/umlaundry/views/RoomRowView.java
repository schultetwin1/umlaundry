package com.schultetwins.umlaundry.views;

import com.schultetwins.umlaundry.R;
import com.schultetwins.umlaundry.R.id;
import com.schultetwins.umlaundry.R.layout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RoomRowView extends LinearLayout {
	
	private TextView roomNameTextView;

	public RoomRowView(Context context) {
		super(context);
		
		// Inflate the XML resource
		LayoutInflater roomsInflater = ((Activity)context).getLayoutInflater();
		roomsInflater.inflate(R.layout.roomview, this);
		
		// Capture the text views
		this.roomNameTextView = (TextView) findViewById(R.id.room_name);
	}
	
	public void setBuildingNameText(String name) {
		this.roomNameTextView.setText(name);
	}
	
}
