package com.zeyomir.ocfun.configuration;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.zeyomir.ocfun.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class EventBusModule {
    @Provides
    @Singleton
    EventBus providesEventBus() {
        Bus bus = new Bus(ThreadEnforcer.ANY);
        Handler mainHandler = new Handler(Looper.getMainLooper());

        HandlerThread backgroundThread1 = new HandlerThread("apiThread");
        backgroundThread1.start();
        Looper looper1 = backgroundThread1.getLooper();
        Handler apiHandler = new Handler(looper1);

        HandlerThread backgroundThread2 = new HandlerThread("dbThread");
        backgroundThread2.start();
        Looper looper2 = backgroundThread2.getLooper();
        Handler dbHandler = new Handler(looper2);

        HandlerThread backgroundThread3 = new HandlerThread("fileTransferThread");
        backgroundThread3.start();
        Looper looper3 = backgroundThread3.getLooper();
        Handler fileTransferHandler = new Handler(looper3);

        return new EventBus(bus, mainHandler, dbHandler, apiHandler, fileTransferHandler);
    }
}
