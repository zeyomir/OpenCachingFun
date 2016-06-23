package com.zeyomir.ocfun.eventbus.event;

public class AuthenticationStateChangedEvent extends Event {
    public final boolean authenticated;

    public AuthenticationStateChangedEvent(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
