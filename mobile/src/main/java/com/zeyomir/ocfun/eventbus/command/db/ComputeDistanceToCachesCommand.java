package com.zeyomir.ocfun.eventbus.command.db;

import android.location.Location;

public class ComputeDistanceToCachesCommand extends DbCommand{
    public final Location location;

    public ComputeDistanceToCachesCommand(Location location) {
        this.location = location;
    }
}
