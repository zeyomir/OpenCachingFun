package com.zeyomir.ocfun.eventbus.event;

public class ShowAddLogEvent extends Event {
    public final String code;

    public ShowAddLogEvent(String code) {
        this.code = code;
    }
}
