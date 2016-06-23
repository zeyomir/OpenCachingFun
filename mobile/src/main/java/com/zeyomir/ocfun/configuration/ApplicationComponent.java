package com.zeyomir.ocfun.configuration;

import com.zeyomir.ocfun.network.DownloadingService;
import com.zeyomir.ocfun.network.SynchronizationService;
import com.zeyomir.ocfun.view.activity.BaseActivity;
import com.zeyomir.ocfun.view.activity.MediaActivity;
import com.zeyomir.ocfun.view.fragment.BaseFragment;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {ApplicationContextModule.class, EventBusModule.class, PreferencesModule.class, FileManagerModule.class, HttpModule.class, DatabaseModule.class})
public interface ApplicationComponent {
    void inject(OCFunApplication application);

    void inject(BaseActivity activity);

    void inject(BaseFragment fragment);

    void inject(DownloadingService downloadingService);

    void inject(SynchronizationService synchronizationService);

    void inject(MediaActivity mediaActivity);
}
