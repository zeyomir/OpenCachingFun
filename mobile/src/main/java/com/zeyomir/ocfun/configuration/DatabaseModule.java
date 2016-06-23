package com.zeyomir.ocfun.configuration;

import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.storage.DataBaseManager;
import com.zeyomir.ocfun.storage.FileManager;
import com.zeyomir.ocfun.storage.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    @Provides
    @Singleton
    DataBaseManager provideDatabaseManager(EventBus bus, FileManager fileManager, PreferencesManager preferencesManager) {
        return new DataBaseManager(bus, fileManager, preferencesManager);
    }
}
