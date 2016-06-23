package com.zeyomir.ocfun.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.eventbus.command.sensors.GetLastKnownLocationCommand;
import com.zeyomir.ocfun.eventbus.command.sensors.SensorsCommand;
import com.zeyomir.ocfun.eventbus.command.sensors.SetLocationUpdatesCommand;
import com.zeyomir.ocfun.eventbus.event.LastLocationReceivedEvent;
import com.zeyomir.ocfun.eventbus.event.LocationUpdateEvent;

public class LocationHandler {

    private final Handler handler;
    private final Context context;
    private final EventBus bus;
    private boolean playServicesPresent = false;
    private boolean determined = false;
    private SensorsCommand lastCommand;

    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            determined = true;
            playServicesPresent = true;
        }

        @Override
        public void onConnectionSuspended(int i) {
            determined = true;
            playServicesPresent = false;
        }
    };
    private final GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            determined = true;
            playServicesPresent = false;
        }
    };
    private final GoogleApiClient googleApiClient;
    private LocationListener locationUpdateListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            bus.post(new LocationUpdateEvent(location));
        }
    };
    private Runnable repeater = new Runnable() {
        @Override
        public void run() {
            if (lastCommand == null)
                return;

            if (!determined) {
                handler.postDelayed(repeater, 1000);
                return;
            }

            handler.removeCallbacks(repeater);
            bus.post(lastCommand);
            lastCommand = null;
        }
    };

    public LocationHandler(Context context, EventBus bus) {
        this.context = context;
        this.bus = bus;
        handler = new Handler(context.getMainLooper());
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    public void dispose() {
        googleApiClient.disconnect();
    }

    @Subscribe
    public void getLastKnownLocation(GetLastKnownLocationCommand command) {
        if (!determined) {
            handler.removeCallbacks(repeater);
            lastCommand = command;
            handler.postDelayed(repeater, 1000);
            return;
        }
        Location lastLocation = null;
        if (playServicesPresent && hasGpsPermission())
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        bus.post(new LastLocationReceivedEvent(lastLocation));
    }

    @Subscribe
    public void setLocationUpdates(SetLocationUpdatesCommand command) {
        if (!determined) {
            handler.removeCallbacks(repeater);
            lastCommand = command;
            handler.postDelayed(repeater, 1000);
            return;
        }
        if (!playServicesPresent)
            return;
        if (command.requestType.equals(SetLocationUpdatesCommand.RequestType.STOP)) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationUpdateListener);
            return;
        }
        LocationRequest locationRequest = LocationRequest
                .create()
                .setMaxWaitTime(4000)
                .setInterval(2000)
                .setFastestInterval(1000)
                .setSmallestDisplacement(1)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (command.requestType.equals(SetLocationUpdatesCommand.RequestType.SINGLE)) {
            locationRequest.setExpirationDuration(12000);
            locationRequest.setNumUpdates(1);
        }

        if (hasGpsPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationUpdateListener);
        }
    }

    private boolean hasGpsPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
