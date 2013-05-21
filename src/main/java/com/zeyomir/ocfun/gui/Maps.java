package com.zeyomir.ocfun.gui;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.maps.*;
import com.zeyomir.ocfun.LocationProvider;
import com.zeyomir.ocfun.LocationUser;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.DisplayMap;
import com.zeyomir.ocfun.model.MapItems;

import java.util.List;

public class Maps extends MapActivity implements LocationUser {

	private MapItems myPos;

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.map);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		MapController mapControl = mapView.getController();
		mapControl.setZoom(16);
		Bundle extras = getIntent().getExtras();
		double lat;
		double lon;
		String _temp = extras.getString("lat");
		if (_temp != null && !_temp.equals("")) {
			Log.i("MAP", _temp);
			lat = Double.parseDouble(extras.getString("lat"));
			lon = Double.parseDouble(extras.getString("lon"));
			mapControl.animateTo(new GeoPoint((int) (lat * 1E6),
					(int) (lon * 1E6)));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocationProvider lp = ((LocationProvider) getApplicationContext());
		lp.registerForFrequentlyLocationUpdates(this);
		List<Overlay> mapOverlays = ((MapView) findViewById(R.id.mapview))
				.getOverlays();
		mapOverlays.add(DisplayMap.getOverlays(this));

		Location l = lp.getLast();
		if (l != null)
			addMyPos(l);
	}

	private void addMyPos(Location l) {
		MapView map = ((MapView) findViewById(R.id.mapview));

		if (myPos != null) {
			map.getOverlays().remove(myPos);
		}
		myPos = new MapItems(this.getResources().getDrawable(R.drawable.blue_dot), this);
		GeoPoint point = new GeoPoint((int) (l.getLatitude() * 1E6), (int) (l.getLongitude() * 1E6));
		OverlayItem oi = new OverlayItem(point, "", "Błąd: " + l.getAccuracy());
		myPos.addOverlay(oi);
		map.invalidate();
		map.getOverlays().add(myPos);
	}

	@Override
	protected void onPause() {
		super.onPause();
		((LocationProvider) getApplicationContext()).unregister(this);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void locationFound(Location l) {
		addMyPos(l);
	}
}
