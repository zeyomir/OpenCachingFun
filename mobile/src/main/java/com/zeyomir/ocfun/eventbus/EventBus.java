package com.zeyomir.ocfun.eventbus;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.zeyomir.ocfun.eventbus.command.api.ApiCommand;
import com.zeyomir.ocfun.eventbus.command.db.DbCommand;
import com.zeyomir.ocfun.eventbus.command.file.FileTransferCommand;
import com.zeyomir.ocfun.eventbus.command.sensors.SensorsCommand;
import com.zeyomir.ocfun.eventbus.event.Event;

import timber.log.Timber;

public class EventBus {

    private final Bus bus;
    private final Handler mainHandler;
    private final Handler apiHandler;
    private final Handler dbHandler;
    private final Handler fileTransferHandler;

    public EventBus(Bus bus, Handler mainHandler, Handler dbHandler, Handler apiHandler, Handler fileTransferHandler) {
        this.bus = bus;
        this.mainHandler = mainHandler;
        this.dbHandler = dbHandler;
        this.apiHandler = apiHandler;
        this.fileTransferHandler = fileTransferHandler;
    }

    public void post(final ApiCommand command) {
        apiHandler.post(new Runnable() {
            @Override
            public void run() {
                Timber.v("Dispatching ApiCommand %s", command.getClass().getSimpleName());
                bus.post(command);
            }
        });
    }

    public void post(final DbCommand command) {
        dbHandler.post(new Runnable() {
            @Override
            public void run() {
                Timber.v("Dispatching DbCommand %s", command.getClass().getSimpleName());
                bus.post(command);
            }
        });
    }

    public void post(final FileTransferCommand command) {
        fileTransferHandler.post(new Runnable() {
            @Override
            public void run() {
                Timber.v("Dispatching FileTransferCommand %s", command.getClass().getSimpleName());
                bus.post(command);
            }
        });
    }

    public void post(final SensorsCommand command) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Timber.v("Dispatching SensorCommand %s", command.getClass().getSimpleName());
                bus.post(command);
            }
        });
    }

    public void post(final Event event) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Timber.v("Dispatching Event %s", event.getClass().getSimpleName());
                bus.post(event);
            }
        });
    }

    public void register(Object o) {
        Timber.v("%s joined", o.getClass().getSimpleName());
        bus.register(o);
    }

    public void unregister(Object o) {
        Timber.v("%s left", o.getClass().getSimpleName());
        bus.unregister(o);
    }
}
