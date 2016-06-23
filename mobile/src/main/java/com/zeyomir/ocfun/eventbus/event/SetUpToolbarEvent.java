package com.zeyomir.ocfun.eventbus.event;

import android.support.annotation.MenuRes;

public class SetUpToolbarEvent extends Event {
    public final String title;
    public final String subtitle;
    @MenuRes
    public final int menu;
    public final boolean hasMenu;

    public SetUpToolbarEvent(String title, String subtitle, @MenuRes int menu, boolean hasMenu) {
        this.title = title;
        this.subtitle = subtitle;
        this.menu = menu;
        this.hasMenu = hasMenu;
    }
}
