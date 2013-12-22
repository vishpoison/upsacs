package com.upsacs.app;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends FragmentActivity {// Google Map
	GoogleMap supportMap = null;
	private LatLng DUBLGGN;
	ProgressDialog pDialog;
	List<LatLng> polyz;
	JSONArray array;
	ArrayList<LatLng> arrLocations;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			MapsInitializer.initialize(MapActivity.this);

			initilizeMap();
			// double latitude = 20.00;
			// double longitude = 30.00;
			//
			// // create marker
			// MarkerOptions marker = new MarkerOptions().position(
			// new LatLng(latitude, longitude)).title("Hello Maps ");
			//
			// // adding marker
			// googleMap.addMarker(marker);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initilizeMap() {

		Intent intent = getIntent();
		Double lat = Double.parseDouble((String) intent.getExtras().getString(
				"lat"));
		Double longitude = Double.parseDouble((String) intent.getExtras()
				.getString("longitude"));
		String name = intent.getExtras().getString("name");
		// Loading map
		arrLocations = new ArrayList<LatLng>();
		DUBLGGN = new LatLng(lat, longitude);
		arrLocations.add(DUBLGGN);

		if (supportMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			supportMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (supportMap != null) {
				for (int i = 0; i < arrLocations.size(); i++) {
					MarkerOptions mo = new MarkerOptions()
							.position(arrLocations.get(i));
					mo.title(name);
					supportMap.addMarker(mo);
				}
			}

			supportMap
					.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBLGGN, 8));

			new GetDirection().execute();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	class GetDirection extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MapActivity.this);
			pDialog.setMessage("Loading route. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			try {

				// final JSONObject json = new JSONObject(result);
				// JSONArray routeArray = json.getJSONArray("routes");
				// JSONObject routes = routeArray.getJSONObject(0);
				// JSONObject overviewPolylines = routes
				// .getJSONObject("overview_polyline");
				// String encodedString = overviewPolylines.getString("points");

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		}

		protected void onPostExecute(String file_url) {

			for (int z = 0; z < arrLocations.size() - 1; z++) {
				LatLng src = arrLocations.get(z);
				LatLng dest = arrLocations.get(z + 1);
				supportMap.addPolyline(new PolylineOptions()
						.add(new LatLng(src.latitude, src.longitude),
								new LatLng(dest.latitude, dest.longitude))
						.width(5).color(Color.BLUE).geodesic(true));
			}
			pDialog.dismiss();

		}
	}

	/* Method to decode polyline points */
	/*
	 * private List<LatLng> decodePoly(String encoded) {
	 * 
	 * List<LatLng> poly = new ArrayList<LatLng>(); int index = 0, len =
	 * encoded.length(); int lat = 0, lng = 0;
	 * 
	 * while (index < len) { int b, shift = 0, result = 0; do { b =
	 * encoded.charAt(index++) - 63; result |= (b & 0x1f) << shift; shift += 5;
	 * } while (b >= 0x20); int dlat = ((result & 1) != 0 ? ~(result >> 1) :
	 * (result >> 1)); lat += dlat;
	 * 
	 * shift = 0; result = 0; do { b = encoded.charAt(index++) - 63; result |=
	 * (b & 0x1f) << shift; shift += 5; } while (b >= 0x20); int dlng = ((result
	 * & 1) != 0 ? ~(result >> 1) : (result >> 1)); lng += dlng;
	 * 
	 * LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
	 * poly.add(p); }
	 * 
	 * return poly; }
	 */
}

// Extra Code

// FragmentManager fm = getSupportFragmentManager();
// map = (SupportMapFragment) fm.findFragmentById(R.id.map);
// if (map == null) {
// map = SupportMapFragment.newInstance();
// fm.beginTransaction().replace(R.id.map, map).commit();
// }
//
// if (map != null) {
// MarkerOptions mo = new MarkerOptions().position( new LatLng( latitude,
// longitude ) );
// fm.addMarker( mo );
// }

// --------------------------------//

// if (googleMap == null) {
// try {
// googleMap = ((MapFragment) getFragmentManager()
// .findFragmentById(R.id.map)).getMap();
// Polyline line = googleMap.addPolyline(new PolylineOptions()
// .add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
// .width(5).color(Color.RED));
//
// // FragmentManager fm = MainActivity.this.getFragmentManager();
// // MapFragment frag = (MapFragment)
// // fm.findFragmentById(R.id.map);
// // googleMap = ((MapFragment) frag).getMap();
// // System.out.println();
//
// } catch (Exception e) {
// e.printStackTrace();
// }
//
// // check if map is created successfully or not
// if (googleMap == null) {
// Toast.makeText(getApplicationContext(),
// "Sorry! unable to create maps", Toast.LENGTH_SHORT)
// .show();
// }
// }

