package com.zeyomir.ocfun.eventbus.event;

import com.zeyomir.ocfun.network.response.CacheData;

public class ReceivedCacheDataEvent extends Event {
    public final CacheData cacheData;

    public ReceivedCacheDataEvent(CacheData cacheDataDeserialized) {
        cacheData = cacheDataDeserialized;
    }
}
