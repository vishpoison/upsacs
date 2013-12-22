package com.upsacs.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {
	Spinner spinLocations;
	Button btnSearch;
	DBclass db;
	Location location;
	String currLoc, DEFAULT_LOCATION = "Lucknow";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.home_screen);
			spinLocations = (Spinner) findViewById(R.id.spinLocations);
			btnSearch = (Button) findViewById(R.id.btnSearch);
			db = DBclass.getDBAdapterInstance(MainActivity.this);
			db.createDataBase();

			currLoc = getLocation();

			fillData();

			/*
			 * ArrayList<CDOLoc> arr = new ArrayList<CDOLoc>(); arr =
			 * db.getLocationId_Name();
			 * 
			 * ArrayList<HashMap<String, String>> mylist = new
			 * ArrayList<HashMap<String, String>>();
			 * 
			 * for (int i = 0; i < arr.size(); i++) { CDOLoc cdo = new CDOLoc();
			 * cdo = arr.get(i); HashMap<String, String> map = new
			 * HashMap<String, String>(); map.put("Id", cdo.getId());
			 * map.put("value", cdo.getName()); mylist.add(map);
			 * 
			 * }
			 * 
			 * SimpleAdapter adap = new SimpleAdapter(MainActivity.this, mylist,
			 * R.layout.loc_row, new String[] { "id", "value" }, new int[] {
			 * R.id.tvId, R.id.tvValue }); spinLocations.setAdapter(adap);
			 * 
			 * btnSearch.setOnClickListener(new View.OnClickListener() {
			 * 
			 * @SuppressWarnings("unchecked")
			 * 
			 * @Override public void onClick(View arg0) { try { String lat = "",
			 * longitude = ""; HashMap<String, String> map = new HashMap<String,
			 * String>(); map = (HashMap<String, String>) spinLocations
			 * .getSelectedItem();
			 * 
			 * if (map.get("Id").equalsIgnoreCase("0")) {
			 * 
			 * lat = "" + location.getLatitude(); longitude = "" +
			 * location.getLongitude();
			 * 
			 * } else { lat = db.getLatLongFromID(map.get("Id")) .getLatitude();
			 * longitude = db.getLatLongFromID(map.get("Id")) .getLongitude();
			 * 
			 * }
			 * 
			 * Intent intent = new Intent(MainActivity.this, MapActivity.class);
			 * intent.putExtra("lat", lat); intent.putExtra("longitude",
			 * longitude); startActivity(intent); } catch (Exception e) {
			 * e.printStackTrace(); }
			 * 
			 * } });
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getLocation() {
		LocationManager mngr = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String locationDefault = "Lucknow";
		String best = mngr.getBestProvider(criteria, true);
		location = mngr.getLastKnownLocation(best);
		Log.d("best provider", best);
		Geocoder coder = new Geocoder(MainActivity.this);
		List<Address> addresses = null;
		try {
			addresses = coder.getFromLocation(location.getLatitude(),
					location.getLongitude(), 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (addresses != null) {

			locationDefault = addresses.get(0).getSubAdminArea();

		}

		return locationDefault;
	}

	private void fillData() {
		List<String> districts = db.getDistricts();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				MainActivity.this, android.R.layout.simple_spinner_item,
				districts);
		spinLocations.setAdapter(adapter);

		spinLocations.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String district = (String) arg0.getAdapter().getItem(arg2);
				List<CDOLoc> centers = db
						.getLocationId_Name_ForDistrict(district);
				fillListView(centers);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		if (districts.contains(currLoc))
			spinLocations.setSelection(districts.indexOf(currLoc));
		else
			spinLocations.setSelection(districts.indexOf(DEFAULT_LOCATION));

	}

	protected void fillListView(List<CDOLoc> centers) {
		ListView lView = (ListView) findViewById(R.id.listCentres);
		ListAdapter adap = new ArrayAdapter<CDOLoc>(MainActivity.this,
				android.R.layout.simple_list_item_1, centers);
		lView.setAdapter(adap);
		lView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CDOLoc location = (CDOLoc) arg0.getAdapter().getItem(arg2);
				openMap(location);

			}
		});

	}

	protected void openMap(CDOLoc selectedLocation) {
		Intent intent = new Intent(MainActivity.this, MapActivity.class);
		intent.putExtra("lat", selectedLocation.getLatitude());
		intent.putExtra("longitude", selectedLocation.getLongitude());
		intent.putExtra("name", selectedLocation.toString());
		startActivity(intent);

	}

}
