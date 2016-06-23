package com.zeyomir.ocfun.eventbus.event;

import com.zeyomir.ocfun.network.response.CachesSearchResponse;

public class ReceivedCachesListEvent extends Event {
    public final CachesSearchResponse searchResult;

    public ReceivedCachesListEvent(CachesSearchResponse searchResult) {
        this.searchResult = searchResult;
    }
}
