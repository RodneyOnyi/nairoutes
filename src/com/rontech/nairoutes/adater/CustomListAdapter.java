package com.rontech.nairoutes.adater;

import com.rontech.nairoutes.R;
import com.rontech.nairoutes.model.Route;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class CustomListAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<Route> routeItems;

	public CustomListAdapter(Activity activity, List<Route> routeItems) {
		this.activity = activity;
		this.routeItems = routeItems;
	}

	@Override
	public int getCount() {
		return routeItems.size();
	}

	@Override
	public Object getItem(int location) {
		return routeItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.list_row, null);

		TextView route = (TextView) convertView.findViewById(R.id.route);
		TextView stage = (TextView) convertView.findViewById(R.id.stage);
		TextView bus_stop = (TextView) convertView.findViewById(R.id.bus_stop);
		TextView fare = (TextView) convertView.findViewById(R.id.fare);

		// getting movie data for the row
		Route m = routeItems.get(position);

		// Route
		route.setText("Route: " + String.valueOf(m.getRoute()));
		
		// Stage
		stage.setText("Stage: " + String.valueOf(m.getStage()));
		
		// Bus Stop
		bus_stop.setText("Stops: " + String.valueOf(m.getBus_Stop()));
				
		// release year
		fare.setText(String.valueOf(m.getFare()));

		return convertView;
	}
	
}