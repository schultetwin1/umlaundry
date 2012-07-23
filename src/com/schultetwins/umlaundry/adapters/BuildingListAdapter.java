package com.schultetwins.umlaundry.adapters;


import com.schultetwins.umlaundry.UMLaundryDataAccessor;
import com.schultetwins.umlaundry.UMLaundryDataAccessor.Building;
import com.schultetwins.umlaundry.views.BuildingRowView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class BuildingListAdapter extends ObjectListAdapter<Building> {

	public BuildingListAdapter(Context context) {
		super(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BuildingRowView view = null;
		
		if (convertView == null) {
			// Create a new view
			view = new BuildingRowView(context);
		} else {
			view = (BuildingRowView)convertView;
		}
		view.setBuildingNameText(getItem(position).name);
		return view;
	}

}
