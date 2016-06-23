package com.zeyomir.ocfun.eventbus.command.api;

import java.util.Locale;

public class GetCachesByLocationCommand extends GetCachesCommand {
   public final String location;
   public final float distance;
    public final String foundStatus;
    public final Boolean skipOwn;
    public final String ignoredStatus;

    public GetCachesByLocationCommand(float latitude, float longitude, float distance, boolean skipFound, boolean skipOwn, boolean skipIgnored) {
        this.distance = distance;
        this.foundStatus = getFoundStatus(skipFound);
        this.skipOwn = getSkipOwn(skipOwn);
        this.ignoredStatus = getIgnoredStatus(skipIgnored);
        location = String.format(Locale.US, "%f|%f", latitude, longitude);
    }
}
