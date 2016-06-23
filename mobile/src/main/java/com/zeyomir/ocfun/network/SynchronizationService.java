package com.zeyomir.ocfun.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.configuration.OCFunApplication;
import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.eventbus.command.api.SubmitLogCommand;
import com.zeyomir.ocfun.eventbus.command.api.SubmitPersonalNoteCommand;
import com.zeyomir.ocfun.eventbus.event.GeneralNetworkErrorEvent;
import com.zeyomir.ocfun.eventbus.event.LeftToSyncEvent;
import com.zeyomir.ocfun.eventbus.event.LogSubmittedEvent;
import com.zeyomir.ocfun.eventbus.event.PersonalNoteSubmittedEvent;
import com.zeyomir.ocfun.storage.PreferencesManager;
import com.zeyomir.ocfun.storage.model.CacheModel;
import com.zeyomir.ocfun.storage.model.LogModel;
import com.zeyomir.ocfun.storage.model.PersonalNoteModel;
import com.zeyomir.ocfun.storage.model.UserLogModel;

import java.util.Date;

import javax.inject.Inject;

public class SynchronizationService extends Service {
    private static final String USER_NAME_KEY = "userName";

    @Inject
    EventBus bus;

    private boolean syncInProgress = false;
    private String userName;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((OCFunApplication)getApplication()).getInjector().inject(this);

        bus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean tempSyncInProgress = syncInProgress;
        syncInProgress = true;
        if (!tempSyncInProgress) {
            this.userName = intent.getStringExtra(USER_NAME_KEY);
            UserLogModel.clearErrors();
            saveNextNote();
        }

        return START_STICKY;
    }

    private void saveNextNote() {
        int notesToSync = PersonalNoteModel.count();
        int logsToSync = UserLogModel.count();
        bus.post(new LeftToSyncEvent(notesToSync + logsToSync));
        if (notesToSync > 0) {
            PersonalNoteModel first = PersonalNoteModel.getFirst();
            bus.post(new SubmitPersonalNoteCommand(first.cacheCode, first.value, first.oldValue));
        } else if (logsToSync > 0) {
            UserLogModel first = UserLogModel.getFirst();
            bus.post(new SubmitLogCommand(first.getId(), first.cacheCode, first.logtype, first.comment, new Date(first.date), first.password, first.rating, first.recommend, first.needsMaintenance));
        } else {
            syncInProgress = false;
            stopSelf();
        }
    }

    @Subscribe
    public void personalNoteSubmitted(PersonalNoteSubmittedEvent event) {
        CacheModel.setPersonalNote(event.cacheCode, event.personalNoteSubmissionResponse.saved_value);
        PersonalNoteModel.remove(event.cacheCode);
        saveNextNote();
    }

    @Subscribe
    public void userLogSubmitted(LogSubmittedEvent event) {
        if (event.logSubmissionResponse.success) {
            UserLogModel.delete(UserLogModel.class, event.command.logModelId);

            LogModel logModel = new LogModel();
            logModel.uuid = event.logSubmissionResponse.log_uuid;
            logModel.code = event.command.cacheCode;
            logModel.date = event.command.when;
            logModel.type = event.command.logtype;
            logModel.comment = event.command.comment;
            logModel.username = userName;
            logModel.save();
        } else {
            UserLogModel.setError(event.command.cacheCode, event.logSubmissionResponse.message);
        }
        saveNextNote();
    }

    @Subscribe
    public void noNetwork(GeneralNetworkErrorEvent event){
        stopSelf();
    }

    public static Intent getIntent(Context context, String userName){
        Intent intent = new Intent(context, SynchronizationService.class);
        intent.putExtra(USER_NAME_KEY, userName);
        return intent;
    }
}
