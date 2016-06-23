package com.zeyomir.ocfun.eventbus.event;

public class LeftToSyncEvent extends Event {
    public final int i;

    public LeftToSyncEvent(int left) {
        this.i = left;
    }
}
