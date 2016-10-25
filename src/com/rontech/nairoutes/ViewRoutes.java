package com.rontech.nairoutes;

import com.rontech.nairoutes.adater.RouteDbAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewRoutes extends Activity {
	
	private RouteDbAdapter dbHelper;
	private SimpleCursorAdapter dataAdapter;
	
	 
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_route);
	 
	  dbHelper = new RouteDbAdapter(this);
	  dbHelper.open();
	 
	  //Clean all data
	  dbHelper.deleteAllRoutes();
	  //Add some data
	  dbHelper.insertSomeRoutes();
	 
	  //Generate ListView from SQLite Database
	  displayListView();
	 
	 }
	 
	 private void displayListView() {
	 
	 
	  Cursor cursor = dbHelper.fetchAllRoutes();
	 
	  // The desired columns to be bound
	  String[] columns = new String[] {
	    RouteDbAdapter.KEY_ROUTE,
	    RouteDbAdapter.KEY_STAGE,
	    RouteDbAdapter.KEY_STOP,
	    RouteDbAdapter.KEY_FARE
	  };
	 
	  // the XML defined views which the data will be bound to
	  int[] to = new int[] {
	    R.id.route,
	    R.id.stage,
	    R.id.stop,
	    R.id.fare,
	  };
	 
	  // create the adapter using the cursor pointing to the desired data
	  //as well as the layout information
	  dataAdapter = new SimpleCursorAdapter(
	    this, R.layout.route_info,
	    cursor,
	    columns,
	    to,
	    0);
	 
	  ListView listView = (ListView) findViewById(R.id.listViewRoute);
	  // Assign adapter to ListView
	  listView.setAdapter(dataAdapter);
	 
	 
	  listView.setOnItemClickListener(new OnItemClickListener() {
	   @Override
	   public void onItemClick(AdapterView<?> listView, View view,
	     int position, long id) {
	   // Get the cursor, positioned to the corresponding row in the result set
	   Cursor cursor = (Cursor) listView.getItemAtPosition(position);
	 
	   // Get the state's capital from this row in the database.
	   String route =
	    cursor.getString(cursor.getColumnIndexOrThrow("route"));
	   Toast.makeText(getApplicationContext(),
	   ("Route:"+route), Toast.LENGTH_SHORT).show();  
	   	   
	    Intent map =new Intent(getApplicationContext(), Map.class);
	    map.putExtra("Route", route);

		startActivity(map);
	 
	   }
	  });
	 
	  EditText myFilter = (EditText) findViewById(R.id.myFilter);
	  myFilter.addTextChangedListener(new TextWatcher() {
	 
	   public void afterTextChanged(Editable s) {
	   }
	 
	   public void beforeTextChanged(CharSequence s, int start,
	     int count, int after) {
	   }
	 
	   public void onTextChanged(CharSequence s, int start,
	     int before, int count) {
	    dataAdapter.getFilter().filter(s.toString());
	   }
	  });
	   
	  dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
	         public Cursor runQuery(CharSequence constraint) {
	             return dbHelper.fetchRoutesByName(constraint.toString());
	         }
	     });
	 
	 }

}
