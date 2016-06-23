package com.zeyomir.ocfun.eventbus.event;

public class CachesLeftToDownloadEvent extends Event {
    public final int cachesToDownload;

    public CachesLeftToDownloadEvent(int cachesToDownload) {
        this.cachesToDownload = cachesToDownload;
    }
}
