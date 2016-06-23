package com.zeyomir.ocfun.view;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.event.CachesLeftToDownloadEvent;
import com.zeyomir.ocfun.eventbus.event.GeneralNetworkErrorEvent;
import com.zeyomir.ocfun.eventbus.event.GeneralServerErrorEvent;
import com.zeyomir.ocfun.eventbus.event.ImagesLeftToDownloadEvent;
import com.zeyomir.ocfun.eventbus.event.LeftToSyncEvent;

public class NotificationManagerWrapper {
    private static final int CACHES_NOTIFICATION_ID = 1;
    private static final int IMAGES_NOTIFICATION_ID = 2;
    private static final int SYNCING_NOTIFICATION_ID = 3;
    private static final int NO_INTERNET_NOTIFICATION_ID = 4;
    private static final int SERVER_ERROR_NOTIFICATION_ID = 5;
    private final Context context;
    private int downloadedImages = 0, downloadedCaches = 0, synced = 0;
    private final NotificationManager notificationManager;
    private final NotificationCompat.Builder syncingNotificationBuilder;
    private final NotificationCompat.Builder cachesNotificationBuilder;
    private final NotificationCompat.Builder imagesNotificationBuilder;


    public NotificationManagerWrapper(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        cachesNotificationBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setOngoing(true)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notification_caches_title));
        imagesNotificationBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setOngoing(true)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notification_images_title));
        syncingNotificationBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setOngoing(true)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notification_syncing_title));
    }

    @Subscribe
    public void updateCachesNotification(CachesLeftToDownloadEvent event) {
        if (event.cachesToDownload <= 0) {
            downloadedCaches = 0;
            hideNotification(CACHES_NOTIFICATION_ID);
        } else {
            downloadedCaches++;
            showNotification(CACHES_NOTIFICATION_ID, event.cachesToDownload);
        }
    }

    @Subscribe
    public void updateImagesNotification(ImagesLeftToDownloadEvent event) {
        if (event.imagesToDownload <= 0) {
            downloadedImages = 0;
            hideNotification(IMAGES_NOTIFICATION_ID);
        } else {
            downloadedImages++;
            showNotification(IMAGES_NOTIFICATION_ID, event.imagesToDownload);
        }
    }

    @Subscribe
    public void updateSyncingNotification(LeftToSyncEvent event) {
        if (event.i <= 0) {
            synced = 0;
            hideNotification(SYNCING_NOTIFICATION_ID);
        } else {
            synced++;
            showNotification(SYNCING_NOTIFICATION_ID, event.i);
        }
    }

    @Subscribe
    public void noNetwork(GeneralNetworkErrorEvent event){
        hideNotification(SYNCING_NOTIFICATION_ID);
        hideNotification(IMAGES_NOTIFICATION_ID);
        hideNotification(CACHES_NOTIFICATION_ID);
        NotificationCompat.Builder noNetworkNotification = new NotificationCompat.Builder(context).setContentText(context.getString(R.string.notification_no_network))
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setOngoing(false)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_notification);
        notificationManager.notify(NO_INTERNET_NOTIFICATION_ID, noNetworkNotification.build());
    }

    @Subscribe
    public void serverError(GeneralServerErrorEvent event){
        NotificationCompat.Builder serverError = new NotificationCompat.Builder(context).setContentText(context.getString(R.string.notification_server_error))
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setOngoing(false)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_notification);
        notificationManager.notify(SERVER_ERROR_NOTIFICATION_ID, serverError.build());
    }

    private void showNotification(int notificationId, int toDownload) {
        switch (notificationId) {
            case CACHES_NOTIFICATION_ID:
                cachesNotificationBuilder
                        .setContentText(String.format(context.getString(R.string.notification_downloading_text), downloadedCaches, downloadedCaches + toDownload))
                        .setProgress(downloadedCaches + toDownload, downloadedCaches, false);
                notificationManager.notify(notificationId, cachesNotificationBuilder.build());
                break;
            case IMAGES_NOTIFICATION_ID:
                imagesNotificationBuilder
                        .setContentText(String.format(context.getString(R.string.notification_downloading_text), downloadedImages, downloadedImages + toDownload))
                        .setProgress(downloadedImages + toDownload, downloadedImages, false);
                notificationManager.notify(notificationId, imagesNotificationBuilder.build());
                break;
            case SYNCING_NOTIFICATION_ID:
                syncingNotificationBuilder
                        .setContentText(String.format(context.getString(R.string.notification_syncing_text), synced, synced + toDownload))
                        .setProgress(synced + toDownload, synced, false);
                notificationManager.notify(notificationId, syncingNotificationBuilder.build());
        }
    }

    private void hideNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }
}
