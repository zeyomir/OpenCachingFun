package com.zeyomir.ocfun.configuration;

import android.app.Application;
import android.os.Environment;

import com.zeyomir.ocfun.storage.FileManager;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FileManagerModule {
    @Provides
    @Singleton
    FileManager provideFileManager(Application application) {
        File externalFilesDir = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new FileManager(externalFilesDir);
    }
}
