package com.zeyomir.ocfun.configuration;

import android.app.Application;

import com.zeyomir.ocfun.storage.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PreferencesModule {
    @Provides
    @Singleton
    PreferencesManager providePreferencesManager(Application application){
        return new PreferencesManager(application);
    }
}
