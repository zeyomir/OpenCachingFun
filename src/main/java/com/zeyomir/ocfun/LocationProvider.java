package com.zeyomir.ocfun;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Date;
import java.util.List;

public class LocationProvider extends Application {
	private static final int MINUTE = 1000 * 60;

	private Location bestLocation;
	private LocationManager lm;
	private List<String> knownProviders;

	private LocationListener ll;

	private LocationUser currentClient;
	private boolean frequently;

	@Override
	public void onCreate() {
		super.onCreate();
		ll = new LocationListener() {
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}

			@Override
			public void onProviderEnabled(String arg0) {
			}

			@Override
			public void onProviderDisabled(String arg0) {
			}

			@Override
			public void onLocationChanged(Location arg0) {
				if (isBetter(arg0)) {
					bestLocation = arg0;
					notifyClient();
				}
			}
		};

		frequently = false;
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		knownProviders = lm.getAllProviders();

		resume();
	}

	private void standardLocationUpdates() {
		lm.removeUpdates(ll);
		if (knownProviders.contains(LocationManager.GPS_PROVIDER))
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 60 * 1000, 20, ll);
		if (knownProviders.contains(LocationManager.NETWORK_PROVIDER))
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2 * 60 * 1000, 200, ll);
	}

	private void frequentLocationUpdates() {
		lm.removeUpdates(ll);
		if (knownProviders.contains(LocationManager.GPS_PROVIDER))
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3 * 1000, 1, ll);
		else if (knownProviders.contains(LocationManager.NETWORK_PROVIDER))
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, 100, ll);
	}


	public void resume() {
		loadLast();
		if (frequently)
			frequentLocationUpdates();
		else
			standardLocationUpdates();
	}

	public void suspend() {
		lm.removeUpdates(ll);
	}

	private boolean isBetter(Location l) {
		if (l == null)
			return false;
		if (bestLocation == null)
			return true;
		long timeDelta = l.getTime() - bestLocation.getTime();
		boolean significantlyNewer = timeDelta > 30 * MINUTE;
		boolean significantlyOlder = timeDelta < -3 * MINUTE;
		boolean isNewer = timeDelta > 0;

		if (significantlyNewer)
			return true;
		else if (significantlyOlder)
			return false;

		int accuracyDelta = (int) (l.getAccuracy() - bestLocation.getAccuracy());
		boolean isMoreAccurate = accuracyDelta <= 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 20;

		if (isMoreAccurate)
			return true;
		else if (isNewer && !isSignificantlyLessAccurate)
			return true;

		return false;
	}

	private void loadLast() {
		Location temp = null;
		if (knownProviders.contains(LocationManager.NETWORK_PROVIDER)) {
			temp = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (isBetter(temp))
				bestLocation = temp;
		}
		if (knownProviders.contains(LocationManager.GPS_PROVIDER)) {
			temp = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (isBetter(temp))
				bestLocation = temp;
		}
	}

	public Location getLast() {
		loadLast();
		return bestLocation;
	}

	public void registerForLocationUpdates(LocationUser lu) {
		currentClient = lu;
		long time = new Date().getTime();

		if ((bestLocation != null) && ((time - bestLocation.getTime()) < 1000 * 60 * 3))
			currentClient.locationFound(bestLocation);
	}

	public void registerForFrequentlyLocationUpdates(LocationUser lu) {
		currentClient = lu;

		frequently = true;
		frequentLocationUpdates();

		long time = new Date().getTime();
		if ((bestLocation != null) && ((time - bestLocation.getTime()) < 1000 * 60 * 3))
			currentClient.locationFound(bestLocation);
	}

	public void unregister(LocationUser lu) {
		if (currentClient != null && currentClient.equals(lu)) {
			if (frequently) {
				frequently = false;
				standardLocationUpdates();
			}
			currentClient = null;
		}
	}

	private void notifyClient() {
		if (currentClient != null) {
			currentClient.locationFound(bestLocation);
		}
	}
}
