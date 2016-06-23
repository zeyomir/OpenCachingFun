package com.zeyomir.ocfun.eventbus.event;

public class ShowCacheDetailsEvent extends Event{
    public final String code;
    public final String name;

    public ShowCacheDetailsEvent(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
