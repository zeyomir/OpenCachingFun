package com.zeyomir.ocfun.eventbus.event;

public class PhotoDownloadedEvent extends Event {
    public final String cacheCode;
    public final String name;
    public final boolean success;

    public PhotoDownloadedEvent(String cacheCode, String name, boolean success) {
        this.cacheCode = cacheCode;
        this.name = name;
        this.success = success;
    }
}
