package com.zeyomir.ocfun.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.configuration.OCFunApplication;
import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.eventbus.command.api.GetCacheDetailsCommand;
import com.zeyomir.ocfun.eventbus.command.db.SaveCacheCommand;
import com.zeyomir.ocfun.eventbus.command.file.DownloadImageCommand;
import com.zeyomir.ocfun.eventbus.event.CacheSavedEvent;
import com.zeyomir.ocfun.eventbus.event.CachesLeftToDownloadEvent;
import com.zeyomir.ocfun.eventbus.event.CachesListChangedEvent;
import com.zeyomir.ocfun.eventbus.event.GeneralNetworkErrorEvent;
import com.zeyomir.ocfun.eventbus.event.ImagesLeftToDownloadEvent;
import com.zeyomir.ocfun.eventbus.event.PhotoDownloadedEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedCacheDataEvent;
import com.zeyomir.ocfun.storage.FileManager;
import com.zeyomir.ocfun.storage.model.CacheToDownloadModel;
import com.zeyomir.ocfun.storage.model.ImageModel;

import java.io.File;

import javax.inject.Inject;

public class DownloadingService extends Service {
    private static final String SHOULD_DOWNLOAD_IMAGES_KEY = "shouldDownloadImages";
    @Inject
    EventBus bus;
    @Inject
    FileManager fileManager;

    private boolean downloadingCaches = false;
    private boolean downloadingImages = false;
    private boolean shouldDownloadImages = false;

    @Override
    public void onCreate() {
        super.onCreate();
        ((OCFunApplication) getApplication()).getInjector().inject(this);
        bus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        boolean tempDownloadingCaches = downloadingCaches;
        downloadingCaches = true;
        if (!tempDownloadingCaches) {
            shouldDownloadImages = intent.getBooleanExtra(SHOULD_DOWNLOAD_IMAGES_KEY, false);
            downloadNextCache();
        }

        return START_STICKY;
    }

    private void downloadNextCache() {
        if (!downloadingImages) {
            downloadingImages = true;
            downloadNextImage();
        }
        int cachesToDownload = CacheToDownloadModel.count();
        bus.post(new CachesLeftToDownloadEvent(cachesToDownload));
        if (cachesToDownload > 0) {
            CacheToDownloadModel cacheToDownload = CacheToDownloadModel.getFirst();
            bus.post(new GetCacheDetailsCommand(cacheToDownload.code));
        } else {
            downloadingCaches = false;
            tryToStopSelf();
        }
    }

    @Subscribe
    public void cacheReceived(ReceivedCacheDataEvent event) {
        bus.post(new SaveCacheCommand(event.cacheData));
    }

    @Subscribe
    public void cacheSaved(CacheSavedEvent event) {
        CacheToDownloadModel.remove(event.code);
        downloadNextCache();
        bus.post(new CachesListChangedEvent());
    }

    private void downloadNextImage() {
        int imagesToDownload = ImageModel.countNotDownloaded();
        if (shouldDownloadImages && imagesToDownload > 0) {
            bus.post(new ImagesLeftToDownloadEvent(imagesToDownload));
            ImageModel imageToDownload = ImageModel.getFirstNotDownloaded();

            File imageFile = fileManager.getImageFile(imageToDownload.code, imageToDownload.name);
            if (imageFile.exists()) {
                bus.post(new PhotoDownloadedEvent(imageToDownload.code, imageToDownload.name, true));
            } else {
                bus.post(new DownloadImageCommand(imageToDownload.name, imageToDownload.code, imageFile));
            }
        } else {
            bus.post(new ImagesLeftToDownloadEvent(0));
            downloadingImages = false;
            tryToStopSelf();
        }
    }

    @Subscribe
    public void imageSaved(PhotoDownloadedEvent event) {
        if (event.success) {
            ImageModel.markDownloaded(event.name);
        } else {
            fileManager.getImageFile(event.cacheCode, event.name).delete();
        }
        downloadNextImage();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void tryToStopSelf() {
        if (!downloadingCaches && !downloadingImages)
            stopSelf();
    }

    @Subscribe
    public void noNetwork(GeneralNetworkErrorEvent event) {
        stopSelf();
    }

    public static Intent getIntent(Context context, boolean shouldDownloadImages) {
        Intent intent = new Intent(context, DownloadingService.class);
        intent.putExtra(SHOULD_DOWNLOAD_IMAGES_KEY, shouldDownloadImages);
        return intent;
    }
}
