package com.schultetwins.umlaundry.adapters;

import com.schultetwins.umlaundry.UMLaundryDataAccessor;
import com.schultetwins.umlaundry.UMLaundryDataAccessor.Machine;
import com.schultetwins.umlaundry.views.MachineRowView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class MachineListAdapter extends ObjectListAdapter<Machine> {

	public MachineListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MachineRowView view = null;
		
		if (convertView == null) {
			// Create a new view
			view = new MachineRowView(context);
		} else {
			view = (MachineRowView)convertView;
		}
		Machine machine = this.getItem(position);
		view.setMachineText(machine);
		return view;
	}

}
