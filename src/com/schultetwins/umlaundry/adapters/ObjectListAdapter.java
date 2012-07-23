package com.schultetwins.umlaundry.adapters;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

abstract public class ObjectListAdapter<T> extends BaseAdapter {
	
	protected Context context;
	private List<T> objects = Collections.emptyList(); 
	
	public ObjectListAdapter(Context context) {
		this.context = context;
	}

	final public int getCount() {
		return objects.size();
	}

	final public T getItem(int position) {	
		return objects.get(position);
	}

	final public long getItemId(int position) {
		return objects.get(position).hashCode();
	}
	
	public void setItems(List<T> objects) {
		this.objects = objects;
		notifyDataSetChanged();
	}

	abstract public View getView(int position, View convertView, ViewGroup parent);

}
