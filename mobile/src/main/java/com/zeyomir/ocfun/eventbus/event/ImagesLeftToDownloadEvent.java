package com.zeyomir.ocfun.eventbus.event;

public class ImagesLeftToDownloadEvent extends Event {
    public final int imagesToDownload;

    public ImagesLeftToDownloadEvent(int imagesToDownload) {
        this.imagesToDownload = imagesToDownload;
    }
}
