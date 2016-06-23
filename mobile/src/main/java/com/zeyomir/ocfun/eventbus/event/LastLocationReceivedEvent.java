package com.zeyomir.ocfun.eventbus.event;

import android.location.Location;

public class LastLocationReceivedEvent extends Event {
    public final Location lastLocation;

    public LastLocationReceivedEvent(Location lastLocation) {
        this.lastLocation = lastLocation;
    }
}
