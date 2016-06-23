package com.zeyomir.ocfun.eventbus.event;

import android.location.Location;

public class LocationUpdateEvent extends Event {
    public final Location location;

    public LocationUpdateEvent(Location lastLocation) {
        this.location = lastLocation;
    }
}
