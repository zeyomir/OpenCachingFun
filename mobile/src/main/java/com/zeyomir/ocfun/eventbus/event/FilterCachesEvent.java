package com.zeyomir.ocfun.eventbus.event;

public class FilterCachesEvent extends Event {
    public final String filter;

    public FilterCachesEvent(String filter) {
        this.filter = filter;
    }
}
