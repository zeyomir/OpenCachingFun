package com.zeyomir.ocfun.eventbus.event;

public class ReceivedCacheLocationEvent extends Event {
    public final String cacheCode;
    public final String location;

    public ReceivedCacheLocationEvent(String cacheCode, String location) {
        this.cacheCode = cacheCode;
        this.location = location;
    }
}
