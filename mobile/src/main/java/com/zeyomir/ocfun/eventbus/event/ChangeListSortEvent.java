package com.zeyomir.ocfun.eventbus.event;

public class ChangeListSortEvent extends Event {
    public final String sortBy;

    public ChangeListSortEvent(String sortBy) {
        this.sortBy = sortBy;
    }
}
