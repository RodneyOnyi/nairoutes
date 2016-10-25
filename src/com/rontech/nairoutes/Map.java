package com.rontech.nairoutes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Map extends FragmentActivity implements OnItemSelectedListener{
	GoogleMap map;
	ArrayList<LatLng> markerPoints;
	ArrayList<LatLng> point;
	Alert alert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// get action bar  
        ActionBar actionBar = getActionBar();
 
        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
		
		// Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select Route");
        categories.add("Clear Map");
        categories.add("Route 237");
        categories.add("Route 15");
        categories.add("Route 111");
        categories.add("Route 102");
        categories.add("Route 46");

        
        // Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
		
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// attaching data adapter to spinner
		spinner.setAdapter(dataAdapter);
		
		 // create class object
	     alert = new Alert (Map.this);
   	 
	     // check if GPS enabled		
	        if(alert.canGetLocation()){
	        	Toast.makeText(getApplicationContext(), "GPS Enabled",
						   Toast.LENGTH_SHORT).show();	
		}else{
			alert.showSettingsAlert();	
		}
		
		// Initializing 
		markerPoints = new ArrayList<LatLng>();
        point = new ArrayList<LatLng>();
		
		// Getting reference to SupportMapFragment of the activity_main
		SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
		
		// Getting Map for the SupportMapFragment
		map = fm.getMap();
		
		// Enable MyLocation Button in the Map
		map.setMyLocationEnabled(true);		
		
		CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(-1.284746, 36.825642)).zoom(14).build();
 
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		
		String passRoute = getIntent().getStringExtra("Route");
		fetchLocation(passRoute);	
			
		}
	
		
	   private void fetchLocation(String pRoute) {
		// TODO Auto-generated method stub
					
		   if (pRoute.equals("102")){
			   map.clear();
				markerPoints.clear();
				
				CameraPosition cameraPosition = new CameraPosition.Builder().target(
		                new LatLng(-1.284746, 36.825642)).zoom(14).build();
		 
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
				Toast.makeText(getApplicationContext(), "Please Wait, Loading...",
						   Toast.LENGTH_LONG).show();
				
				// Already 10 locations with 8 waypoints and 1 start location and 1 end location. 
				// Upto 8 waypoints are allowed in a query for non-business users
				MarkerOptions begin = new MarkerOptions()
				.position(new LatLng(-1.290851, 36.828274))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				map.addMarker(begin);
				
				MarkerOptions end = new MarkerOptions()
				.position(new LatLng(-1.281321, 36.692050))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				map.addMarker(end);
							
				// Adding new item to the ArrayList
				//markerPoints.add(point);		
				markerPoints.add(new LatLng(-1.290851, 36.828274));
				markerPoints.add(new LatLng(-1.281321, 36.692050));
				//way point
				markerPoints.add(new LatLng(-1.298059, 36.758097));
				
				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();
				
				// Setting the position of the marker
				//options.position(point);
				options.position(new LatLng(-1.290851, 36.828274));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				options.position(new LatLng(-1.281321, 36.692050));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				//options.position(new LatLng(-1.298059, 36.758097));
				//options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				
				/** 
				 * For the start location, the color of marker is GREEN and
				 * for the end location, the color of marker is RED and
				 * for the rest of markers, the color is AZURE
				 *		
				*/
				// Add new marker to the Google Map Android API V2
				map.addMarker(options);			
				
			
				// Checks, whether start and end locations are captured
				if(markerPoints.size() >= 2){					
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);
					
					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}		
			}else if(pRoute.equals("46")) {
				map.clear();
				markerPoints.clear();
				
				CameraPosition cameraPosition = new CameraPosition.Builder().target(
		                new LatLng(-1.284746, 36.825642)).zoom(14).build();
		 
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
				Toast.makeText(getApplicationContext(), "Please Wait, Loading...",
						   Toast.LENGTH_LONG).show();
				
				// Already 10 locations with 8 waypoints and 1 start location and 1 end location. 
				// Upto 8 waypoints are allowed in a query for non-business users
				MarkerOptions begin = new MarkerOptions()
				.position(new LatLng(-1.290851, 36.828274))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				map.addMarker(begin);
				
				MarkerOptions end = new MarkerOptions()
				.position(new LatLng(-1.284016, 36.742900))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				map.addMarker(end);
							
				// Adding new item to the ArrayList
				//markerPoints.add(point);		
				markerPoints.add(new LatLng(-1.290851, 36.828274));
				markerPoints.add(new LatLng(-1.284016, 36.742900));
				//way point
				markerPoints.add(new LatLng(-1.292801, 36.820577));
				markerPoints.add(new LatLng(-1.287127, 36.816200));
				markerPoints.add(new LatLng(-1.295117, 36.802542));
				markerPoints.add(new LatLng(-1.292790, 36.774550));

				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();
				
				// Setting the position of the marker
				//options.position(point);
				options.position(new LatLng(-1.290851, 36.828274));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				options.position(new LatLng(-1.284016, 36.742900));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				
				/** 
				 * For the start location, the color of marker is GREEN and
				 * for the end location, the color of marker is RED and
				 * for the rest of markers, the color is AZURE
				 *		
				*/
				// Add new marker to the Google Map Android API V2
				map.addMarker(options);			
				
			
				// Checks, whether start and end locations are captured
				if(markerPoints.size() >= 2){					
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);
					
					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}
			}else if(pRoute.equals("15")){
				map.clear();
				markerPoints.clear();
				
				CameraPosition cameraPosition = new CameraPosition.Builder().target(
		                new LatLng(-1.284746, 36.825642)).zoom(14).build();
		 
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
				Toast.makeText(getApplicationContext(), "Please Wait, Loading...",
						   Toast.LENGTH_LONG).show();
				
				// Already 10 locations with 8 waypoints and 1 start location and 1 end location. 
				// Upto 8 waypoints are allowed in a query for non-business users
				MarkerOptions begin = new MarkerOptions()
				.position(new LatLng(-1.286556, 36.829124))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				map.addMarker(begin);
				
				MarkerOptions end = new MarkerOptions()
				.position(new LatLng(-1.321974, 36.774318))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				map.addMarker(end);
							
				// Adding new item to the ArrayList
				//markerPoints.add(point);		
				markerPoints.add(new LatLng(-1.286556, 36.829124));
				markerPoints.add(new LatLng(-1.321964, 36.774318));
				//way point
				markerPoints.add(new LatLng(-1.327656, 36.789013));
				markerPoints.add(new LatLng(-1.323452, 36.779754));
						
				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();
				
				// Setting the position of the marker
				//options.position(point);
				options.position(new LatLng(-1.286556, 36.829124));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				options.position(new LatLng(-1.321974, 36.774318));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				/** 
				 * For the start location, the color of marker is GREEN and
				 * for the end location, the color of marker is RED and
				 * for the rest of markers, the color is AZURE
				 *		
				*/
				// Add new marker to the Google Map Android API V2
				map.addMarker(options);			
				
			
				// Checks, whether start and end locations are captured
				if(markerPoints.size() >= 2){					
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);
					
					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}
			} else if(pRoute.equals("237")){
				map.clear();
				markerPoints.clear();
				
				CameraPosition cameraPosition = new CameraPosition.Builder().target(
		                new LatLng(-1.284746, 36.825642)).zoom(14).build();
		 
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
				Toast.makeText(getApplicationContext(), "Please Wait, Loading...",
						   Toast.LENGTH_LONG).show();
				
				// Already 10 locations with 8 waypoints and 1 start location and 1 end location. 
				// Upto 8 waypoints are allowed in a query for non-business users
				MarkerOptions begin = new MarkerOptions()
				.position(new LatLng(-1.285022, 36.828908))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				map.addMarker(begin);
				
				MarkerOptions end = new MarkerOptions()
				.position(new LatLng(-1.036921, 37.076636))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				map.addMarker(end);
							
				// Adding new item to the ArrayList
				//markerPoints.add(point);		
				markerPoints.add(new LatLng(-1.285022, 36.828908));
				markerPoints.add(new LatLng(-1.036921, 37.076636));
				//way point
						
				
				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();
				
				// Setting the position of the marker
				//options.position(point);
				options.position(new LatLng(-1.285022, 36.828908));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				options.position(new LatLng(-1.036921, 37.076636));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				/** 
				 * For the start location, the color of marker is GREEN and
				 * for the end location, the color of marker is RED and
				 * for the rest of markers, the color is AZURE
				 *		
				*/
				// Add new marker to the Google Map Android API V2
				map.addMarker(options);			
				
			
				// Checks, whether start and end locations are captured
				if(markerPoints.size() >= 2){					
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);
					
					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}
			}else if(pRoute.equals("111")){
				map.clear();
				markerPoints.clear();
				
				CameraPosition cameraPosition = new CameraPosition.Builder().target(
		                new LatLng(-1.284746, 36.825642)).zoom(14).build();
		 
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
				Toast.makeText(getApplicationContext(), "Please Wait, Loading...",
						   Toast.LENGTH_LONG).show();
				
				// Already 10 locations with 8 waypoints and 1 start location and 1 end location. 
				// Upto 8 waypoints are allowed in a query for non-business users
				MarkerOptions begin = new MarkerOptions()
				.position(new LatLng(-1.290851, 36.828274))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				map.addMarker(begin);
				
				MarkerOptions end = new MarkerOptions()
				.position(new LatLng(-1.360382, 36.656318))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				map.addMarker(end);
							
				// Adding new item to the ArrayList
				//markerPoints.add(point);		
				markerPoints.add(new LatLng(-1.290851, 36.828274));
				markerPoints.add(new LatLng(-1.360382, 36.656318));
				//way point
				markerPoints.add(new LatLng(-1.299158, 36.761847));		
				
				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();
				
				// Setting the position of the marker
				//options.position(point);
				options.position(new LatLng(-1.290851, 36.828274));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				options.position(new LatLng(-1.360382, 36.656318));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				/** 
				 * For the start location, the color of marker is GREEN and
				 * for the end location, the color of marker is RED and
				 * for the rest of markers, the color is AZURE
				 *		
				*/
				// Add new marker to the Google Map Android API V2
				map.addMarker(options);			
				
			
				// Checks, whether start and end locations are captured
				if(markerPoints.size() >= 2){					
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);
					
					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}
			
			}
			else{
				Toast.makeText(getApplicationContext(), "Route Map Not Updated",
			   Toast.LENGTH_SHORT).show();
			}
	}
	   
	@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// On selecting a spinner item
			String item = parent.getItemAtPosition(position).toString();
			//String passRoute = getIntent().getStringExtra("Route");
			
			if (item=="Clear Map"){
				Toast.makeText(getApplicationContext(), "Map has been cleared",
						   Toast.LENGTH_LONG).show();	
				// Removes all the points from Google Map
				map.clear();
				
				// Removes all the points in the ArrayList
				markerPoints.clear();
			 
			}
			if (item =="Route 46"){
				map.clear();
				markerPoints.clear();
				
				CameraPosition cameraPosition = new CameraPosition.Builder().target(
		                new LatLng(-1.284746, 36.825642)).zoom(14).build();
		 
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
				// Already 10 locations with 8 waypoints and 1 start location and 1 end location. 
				// Upto 8 waypoints are allowed in a query for non-business users
				MarkerOptions begin = new MarkerOptions()
				.position(new LatLng(-1.290851, 36.828274))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				map.addMarker(begin);
				
				MarkerOptions end = new MarkerOptions()
				.position(new LatLng(-1.284016, 36.742900))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				map.addMarker(end);
							
				// Adding new item to the ArrayList
				//markerPoints.add(point);		
				markerPoints.add(new LatLng(-1.290851, 36.828274));
				markerPoints.add(new LatLng(-1.284016, 36.742900));
				//way point
				markerPoints.add(new LatLng(-1.292801, 36.820577));
				markerPoints.add(new LatLng(-1.287127, 36.816200));
				markerPoints.add(new LatLng(-1.295117, 36.802542));
				markerPoints.add(new LatLng(-1.292790, 36.774550));

				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();
				
				// Setting the position of the marker
				//options.position(point);
				options.position(new LatLng(-1.290851, 36.828274));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				options.position(new LatLng(-1.284016, 36.742900));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				

				/** 
				 * For the start location, the color of marker is GREEN and
				 * for the end location, the color of marker is RED and
				 * for the rest of markers, the color is AZURE
				 *		
				*/
				// Add new marker to the Google Map Android API V2
				map.addMarker(options);			
				
			
				// Checks, whether start and end locations are captured
				if(markerPoints.size() >= 2){					
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);
					
					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}
			}
			
			if (item =="Route 102"){
				map.clear();
				markerPoints.clear();
				
				CameraPosition cameraPosition = new CameraPosition.Builder().target(
		                new LatLng(-1.284746, 36.825642)).zoom(14).build();
		 
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
				// Already 10 locations with 8 waypoints and 1 start location and 1 end location. 
				// Upto 8 waypoints are allowed in a query for non-business users
				MarkerOptions begin = new MarkerOptions()
				.position(new LatLng(-1.290851, 36.828274))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				map.addMarker(begin);
				
				MarkerOptions end = new MarkerOptions()
				.position(new LatLng(-1.281321, 36.692050))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				map.addMarker(end);
							
				// Adding new item to the ArrayList
				//markerPoints.add(point);		
				markerPoints.add(new LatLng(-1.290851, 36.828274));
				markerPoints.add(new LatLng(-1.281321, 36.692050));
				//way point
				markerPoints.add(new LatLng(-1.298059, 36.758097));
				
				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();
				
				// Setting the position of the marker
				//options.position(point);
				options.position(new LatLng(-1.290851, 36.828274));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				options.position(new LatLng(-1.281321, 36.692050));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				//options.position(new LatLng(-1.298059, 36.758097));
				//options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				
				/** 
				 * For the start location, the color of marker is GREEN and
				 * for the end location, the color of marker is RED and
				 * for the rest of markers, the color is AZURE
				 *		
				*/
				// Add new marker to the Google Map Android API V2
				map.addMarker(options);			
				
			
				// Checks, whether start and end locations are captured
				if(markerPoints.size() >= 2){					
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);
					
					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}
			}
			if (item =="Route 111"){
				map.clear();
				markerPoints.clear();
				
				CameraPosition cameraPosition = new CameraPosition.Builder().target(
		                new LatLng(-1.284746, 36.825642)).zoom(14).build();
		 
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
				// Already 10 locations with 8 waypoints and 1 start location and 1 end location. 
				// Upto 8 waypoints are allowed in a query for non-business users
				MarkerOptions begin = new MarkerOptions()
				.position(new LatLng(-1.290851, 36.828274))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				map.addMarker(begin);
				
				MarkerOptions end = new MarkerOptions()
				.position(new LatLng(-1.360382, 36.656318))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				map.addMarker(end);
							
				// Adding new item to the ArrayList
				//markerPoints.add(point);		
				markerPoints.add(new LatLng(-1.290851, 36.828274));
				markerPoints.add(new LatLng(-1.360382, 36.656318));
				//way point
				markerPoints.add(new LatLng(-1.299158, 36.761847));		
				
				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();
				
				// Setting the position of the marker
				//options.position(point);
				options.position(new LatLng(-1.290851, 36.828274));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				options.position(new LatLng(-1.360382, 36.656318));
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				
				/** 
				 * For the start location, the color of marker is GREEN and
				 * for the end location, the color of marker is RED and
				 * for the rest of markers, the color is AZURE
				 *		
				*/
				// Add new marker to the Google Map Android API V2
				map.addMarker(options);			
				
			
				// Checks, whether start and end locations are captured
				if(markerPoints.size() >= 2){					
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);
					
					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}
			}
		

		if (item =="Route 237"){
			map.clear();
			markerPoints.clear();
			
			CameraPosition cameraPosition = new CameraPosition.Builder().target(
	                new LatLng(-1.284746, 36.825642)).zoom(14).build();
	 
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			
			// Already 10 locations with 8 waypoints and 1 start location and 1 end location. 
			// Upto 8 waypoints are allowed in a query for non-business users
			MarkerOptions begin = new MarkerOptions()
			.position(new LatLng(-1.285022, 36.828908))
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			
			map.addMarker(begin);
			
			MarkerOptions end = new MarkerOptions()
			.position(new LatLng(-1.036921, 37.076636))
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			
			map.addMarker(end);
						
			// Adding new item to the ArrayList
			//markerPoints.add(point);		
			markerPoints.add(new LatLng(-1.285022, 36.828908));
			markerPoints.add(new LatLng(-1.036921, 37.076636));
			//way point
					
			
			// Creating MarkerOptions
			MarkerOptions options = new MarkerOptions();
			
			// Setting the position of the marker
			//options.position(point);
			options.position(new LatLng(-1.285022, 36.828908));
			options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			
			options.position(new LatLng(-1.036921, 37.076636));
			options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			
			/** 
			 * For the start location, the color of marker is GREEN and
			 * for the end location, the color of marker is RED and
			 * for the rest of markers, the color is AZURE
			 *		
			*/
			// Add new marker to the Google Map Android API V2
			map.addMarker(options);			
			
		
			// Checks, whether start and end locations are captured
			if(markerPoints.size() >= 2){					
				LatLng origin = markerPoints.get(0);
				LatLng dest = markerPoints.get(1);
				
				// Getting URL to the Google Directions API
				String url = getDirectionsUrl(origin, dest);				
				
				DownloadTask downloadTask = new DownloadTask();
				
				// Start downloading json data from Google Directions API
				downloadTask.execute(url);
			}
		}
		
		if (item =="Route 15"){
			map.clear();
			markerPoints.clear();
			
			CameraPosition cameraPosition = new CameraPosition.Builder().target(
	                new LatLng(-1.284746, 36.825642)).zoom(14).build();
	 
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			
			// Already 10 locations with 8 waypoints and 1 start location and 1 end location. 
			// Upto 8 waypoints are allowed in a query for non-business users
			MarkerOptions begin = new MarkerOptions()
			.position(new LatLng(-1.286556, 36.829124))
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			
			map.addMarker(begin);
			
			MarkerOptions end = new MarkerOptions()
			.position(new LatLng(-1.321974, 36.774318))
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			
			map.addMarker(end);
						
			// Adding new item to the ArrayList
			//markerPoints.add(point);		
			markerPoints.add(new LatLng(-1.286556, 36.829124));
			markerPoints.add(new LatLng(-1.321964, 36.774318));
			//way point
			markerPoints.add(new LatLng(-1.327656, 36.789013));
			markerPoints.add(new LatLng(-1.323452, 36.779754));
					
			// Creating MarkerOptions
			MarkerOptions options = new MarkerOptions();
			
			// Setting the position of the marker
			//options.position(point);
			options.position(new LatLng(-1.286556, 36.829124));
			options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			
			options.position(new LatLng(-1.321974, 36.774318));
			options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			
			/** 
			 * For the start location, the color of marker is GREEN and
			 * for the end location, the color of marker is RED and
			 * for the rest of markers, the color is AZURE
			 *		
			*/
			// Add new marker to the Google Map Android API V2
			map.addMarker(options);			
			
		
			// Checks, whether start and end locations are captured
			if(markerPoints.size() >= 2){					
				LatLng origin = markerPoints.get(0);
				LatLng dest = markerPoints.get(1);
				
				// Getting URL to the Google Directions API
				String url = getDirectionsUrl(origin, dest);				
				
				DownloadTask downloadTask = new DownloadTask();
				
				// Start downloading json data from Google Directions API
				downloadTask.execute(url);
			}
		}
	}
	   
	   
	public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	private String getDirectionsUrl(LatLng origin,LatLng dest){
					
		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		
		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;		
					
		// Sensor enabled
		String sensor = "sensor=false";			
				
		// Waypoints
		String waypoints = "";
		for(int i=2;i<markerPoints.size();i++){
			LatLng point  = (LatLng) markerPoints.get(i);
			if(i==2)
				waypoints = "waypoints=";
			waypoints += point.latitude + "," + point.longitude + "|";
		}
		
					
		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;
					
		// Output format
		String output = "json";
		
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		
		
		return url;
	}
	
	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url 
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url 
                urlConnection.connect();

                // Reading data from url 
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }

	
	
	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{			
				
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
				
			// For storing data from web service
			String data = "";
					
			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);			
			
			ParserTask parserTask = new ParserTask();
			
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
				
		}		
	}
	
	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	// Parsing the data in non-ui thread    	
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			
			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;			           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	DirectionsJSONParser parser = new DirectionsJSONParser();
            	
            	// Starts parsing data
            	routes = parser.parse(jObject);    
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}
		
		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			
			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);
				
				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);					
					
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);	
					
					points.add(position);						
				}
				
				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);				
			}
			
			// Drawing polyline in the Google Map for the i-th route
			map.addPolyline(lineOptions);							
		}			
    }   
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, ViewRoutes.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
