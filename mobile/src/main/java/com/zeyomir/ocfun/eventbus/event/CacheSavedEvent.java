package com.zeyomir.ocfun.eventbus.event;

public class CacheSavedEvent extends Event {
    public final String code;

    public CacheSavedEvent(String code) {
        this.code = code;
    }
}
