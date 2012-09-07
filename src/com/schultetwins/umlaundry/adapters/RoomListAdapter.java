package com.schultetwins.umlaundry.adapters;

import com.schultetwins.umlaundry.UMLaundryDataAccessor.Room;
import com.schultetwins.umlaundry.views.RoomRowView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class RoomListAdapter extends ObjectListAdapter<Room> {

	public RoomListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RoomRowView view = null;
		
		if (convertView == null) {
			// Create a new view
			view = new RoomRowView(context);
		} else {
			view = (RoomRowView)convertView;
		}
		view.setBuildingNameText(getItem(position).name);
		return view;
	}

}
