package com.zeyomir.ocfun.configuration;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.BuildConfig;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.eventbus.event.ScheduldedToDownloadEvent;
import com.zeyomir.ocfun.network.API;
import com.zeyomir.ocfun.network.DownloadingService;
import com.zeyomir.ocfun.storage.DataBaseManager;
import com.zeyomir.ocfun.storage.model.CacheModel;
import com.zeyomir.ocfun.storage.model.CacheToDownloadModel;
import com.zeyomir.ocfun.storage.model.ImageModel;
import com.zeyomir.ocfun.storage.model.LogModel;
import com.zeyomir.ocfun.storage.model.PersonalNoteModel;
import com.zeyomir.ocfun.storage.model.UserLogModel;
import com.zeyomir.ocfun.storage.model.WaypointModel;
import com.zeyomir.ocfun.utils.CrashReportingTree;
import com.zeyomir.ocfun.utils.LocationHandler;
import com.zeyomir.ocfun.view.NotificationManagerWrapper;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;


public class OCFunApplication extends Application {
    @Inject
    API api;
    @Inject
    EventBus bus;
    @Inject
    DataBaseManager dataBaseManager;

    private NotificationManagerWrapper notificationManager;
    private LocationHandler locationHandler;

    private ApplicationComponent applicationComponent;
    private RefWatcher refWatcher;

    @Override
    public final void onCreate() {
        super.onCreate();
        inject();
        init();
    }

    private void inject() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationContextModule(new ApplicationContextModule(this))
                .fileManagerModule(new FileManagerModule())
                .preferencesModule(new PreferencesModule())
                .eventBusModule(new EventBusModule())
                .databaseModule(new DatabaseModule())
                .httpModule(new HttpModule())
                .build();
        applicationComponent.inject(this);
    }

    protected void init() {
        bus.register(this);
        prepareLogging();
        prepareDataBase();
        prepareNetworking();
        prepareNotifications();
        prepareLocationHandler();
    }

    private void prepareLogging() {
        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Answers answers = new Answers();
        Fabric.with(this, new Crashlytics.Builder().answers(answers).core(crashlyticsCore).build(), answers);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            refWatcher = LeakCanary.install(this);
            Stetho.initializeWithDefaults(this);
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private void prepareDataBase() {
        bus.register(dataBaseManager);
        Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        configurationBuilder.addModelClasses(CacheModel.class, ImageModel.class, LogModel.class, WaypointModel.class, CacheToDownloadModel.class, PersonalNoteModel.class, UserLogModel.class);
        ActiveAndroid.initialize(configurationBuilder.create(), BuildConfig.DEBUG);
    }

    private void prepareNetworking() {
        bus.register(api);
    }

    private void prepareNotifications() {
        notificationManager = new NotificationManagerWrapper(this);
        bus.register(notificationManager);
    }

    private void prepareLocationHandler() {
        locationHandler = new LocationHandler(this, bus);
        bus.register(locationHandler);
    }

    @Subscribe
    public void cachesScheduledToDownload(ScheduldedToDownloadEvent event) {
        Intent intent = DownloadingService.getIntent(this, true);
        startService(intent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        dispose();
    }

    private void dispose() {
        bus.unregister(this);
        bus.unregister(dataBaseManager);
        ActiveAndroid.dispose();
        bus.unregister(api);
        bus.unregister(notificationManager);
        bus.unregister(locationHandler);
        locationHandler.dispose();
    }

    public ApplicationComponent getInjector() {
        return applicationComponent;
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }
}
