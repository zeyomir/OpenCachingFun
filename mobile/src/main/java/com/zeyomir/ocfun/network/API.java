package com.zeyomir.ocfun.network;

import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.eventbus.command.api.FinishOAuthCommand;
import com.zeyomir.ocfun.eventbus.command.api.GetCacheDetailsCommand;
import com.zeyomir.ocfun.eventbus.command.api.GetCacheLocationCommand;
import com.zeyomir.ocfun.eventbus.command.api.GetCachesByLocationCommand;
import com.zeyomir.ocfun.eventbus.command.api.GetCachesByNameCommand;
import com.zeyomir.ocfun.eventbus.command.api.GetUserDetailsCommand;
import com.zeyomir.ocfun.eventbus.command.api.GetWatchedCachesCommand;
import com.zeyomir.ocfun.eventbus.command.api.StartOAuthCommand;
import com.zeyomir.ocfun.eventbus.command.api.SubmitLogCommand;
import com.zeyomir.ocfun.eventbus.command.api.SubmitPersonalNoteCommand;
import com.zeyomir.ocfun.eventbus.command.db.ClearAuthorizationCredentialsCommand;
import com.zeyomir.ocfun.eventbus.command.db.SaveAuthorizationCredentialsCommand;
import com.zeyomir.ocfun.eventbus.command.db.SaveUserDetailsCommand;
import com.zeyomir.ocfun.eventbus.command.file.DownloadImageCommand;
import com.zeyomir.ocfun.eventbus.event.AuthenticationStateChangedEvent;
import com.zeyomir.ocfun.eventbus.event.GeneralNetworkErrorEvent;
import com.zeyomir.ocfun.eventbus.event.GeneralServerErrorEvent;
import com.zeyomir.ocfun.eventbus.event.LogSubmittedEvent;
import com.zeyomir.ocfun.eventbus.event.PersonalNoteSubmittedEvent;
import com.zeyomir.ocfun.eventbus.event.PhotoDownloadedEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedCacheDataEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedCacheLocationEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedCachesListEvent;
import com.zeyomir.ocfun.network.oauth.OAuthAuthenticator;
import com.zeyomir.ocfun.network.response.CacheData;
import com.zeyomir.ocfun.network.response.CachesSearchResponse;
import com.zeyomir.ocfun.network.response.LogSubmissionResponse;
import com.zeyomir.ocfun.network.response.PersonalNoteSubmissionResponse;
import com.zeyomir.ocfun.network.response.UserDetailsResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class API {
    private static final String CACHE_FIELDS = "code|name|url|location|type|owner|size2|difficulty|terrain|req_passwd|description|hint2|images|latest_logs|last_found|founds|notfounds|attrnames|trackables|alt_wpts|short_description";
    private static final String CACHE_LVL3_FIELDS = CACHE_FIELDS + "|my_notes|is_found";
    private static final String USER_FIELDS = "username|caches_found|caches_notfound|caches_hidden|rcmds_given";
    private final OKAPI service;
    private final OAuthAuthenticator authenticator;
    private final EventBus bus;
    private boolean authenticated;

    public API(OKAPI service, OAuthAuthenticator authenticator, EventBus eventBus) {
        this.service = service;
        this.authenticator = authenticator;
        this.bus = eventBus;
    }

    @Subscribe
    public void getCacheDetails(GetCacheDetailsCommand command) {
        Callback<CacheData> callback = new DefaultCallback<CacheData>(bus) {
            @Override
            public void onSuccess(Call<CacheData> call, Response<CacheData> response) {
                bus.post(new ReceivedCacheDataEvent(response.body()));
            }
        };
        Call<CacheData> call = service.cacheDetails(command.cacheCode, authenticated ? CACHE_LVL3_FIELDS : CACHE_FIELDS);
        call.enqueue(callback);
    }

    @Subscribe
    public void getCacheLocation(final GetCacheLocationCommand command) {
        Callback<JsonObject> callback = new DefaultCallback<JsonObject>(bus) {
            @Override
            public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                String location = response.body().get("location").getAsString();
                bus.post(new ReceivedCacheLocationEvent(command.cacheCode, location));
            }
        };
        Call<JsonObject> call = service.cacheLocation(command.cacheCode);
        call.enqueue(callback);
    }

    @Subscribe
    public void getCachesByName(GetCachesByNameCommand command) {
        Callback<CachesSearchResponse> callback = new DefaultCallback<CachesSearchResponse>(bus) {
            @Override
            public void onSuccess(Call<CachesSearchResponse> call, Response<CachesSearchResponse> response) {
                bus.post(new ReceivedCachesListEvent(response.body()));
            }
        };
        Call<CachesSearchResponse> call = service.cachesByName(command.name, command.foundStatus, command.skipOwn, command.ignoredStatus);
        call.enqueue(callback);
    }

    @Subscribe
    public void getCachesByLocation(GetCachesByLocationCommand command) {
        Callback<CachesSearchResponse> callback = new DefaultCallback<CachesSearchResponse>(bus) {
            @Override
            public void onSuccess(Call<CachesSearchResponse> call, Response<CachesSearchResponse> response) {
                bus.post(new ReceivedCachesListEvent(response.body()));
            }
        };
        Call<CachesSearchResponse> call = service.cachesByLocation(command.location, command.distance, command.foundStatus, command.skipOwn, command.ignoredStatus);
        call.enqueue(callback);
    }

    @Subscribe
    public void getWatchedCaches(GetWatchedCachesCommand command) {
        Callback<CachesSearchResponse> callback = new DefaultCallback<CachesSearchResponse>(bus) {
            @Override
            public void onSuccess(Call<CachesSearchResponse> call, Response<CachesSearchResponse> response) {
                bus.post(new ReceivedCachesListEvent(response.body()));
            }
        };
        Call<CachesSearchResponse> call = service.watchedCaches();
        call.enqueue(callback);
    }

    @Subscribe
    public void downloadImage(final DownloadImageCommand command) {
        Callback<ResponseBody> callback = new DefaultCallback<ResponseBody>(bus) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    byte[] bytes = response.body().bytes();
                    File imageFile = command.cacheFile;
                    if (imageFile.exists()) {
                        bus.post(new PhotoDownloadedEvent(command.cacheCode, command.name, true));
                        return;
                    }

                    FileOutputStream output = new FileOutputStream(imageFile);
                    output.write(bytes);
                    output.flush();
                    output.close();
                    bus.post(new PhotoDownloadedEvent(command.cacheCode, command.name, true));
                } catch (IOException e) {
                    bus.post(new PhotoDownloadedEvent(command.cacheCode, command.name, false));
                }
            }
        };
        Call<ResponseBody> call = service.image(command.name);
        call.enqueue(callback);
    }

    @Subscribe
    public void getUserDetails(GetUserDetailsCommand command) {
        Callback<UserDetailsResponse> callback = new DefaultCallback<UserDetailsResponse>(bus) {
            @Override
            public void onSuccess(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                bus.post(new SaveUserDetailsCommand(response.body()));
            }
        };
        Call<UserDetailsResponse> call = service.userDetails(USER_FIELDS);
        call.enqueue(callback);
    }

    @Subscribe
    public void submitLogEntry(final SubmitLogCommand command) {
        Callback<LogSubmissionResponse> callback = new DefaultCallback<LogSubmissionResponse>(bus) {
            @Override
            public void onSuccess(Call<LogSubmissionResponse> call, Response<LogSubmissionResponse> response) {
                bus.post(new LogSubmittedEvent(command, response.body()));
            }
        };
        Call<LogSubmissionResponse> call = service.submitLogEntry(command.cacheCode, command.logtype.getText(), command.comment, command.when, command.password, command.rating, command.recommend, command.needsMaintenance);
        call.enqueue(callback);
    }

    @Subscribe
    public void savePersonalNote(final SubmitPersonalNoteCommand command) {
        Callback<PersonalNoteSubmissionResponse> callback = new DefaultCallback<PersonalNoteSubmissionResponse>(bus) {
            @Override
            public void onSuccess(Call<PersonalNoteSubmissionResponse> call, Response<PersonalNoteSubmissionResponse> response) {
                bus.post(new PersonalNoteSubmittedEvent(command.cacheCode, response.body()));
            }
        };
        Call<PersonalNoteSubmissionResponse> call = service.saveNote(command.cacheCode, command.newValue, command.oldValue);
        call.enqueue(callback);
    }

    @Subscribe
    public void startOAuth(StartOAuthCommand command) {
        bus.post(new ClearAuthorizationCredentialsCommand());
        authenticator.startOAuth();
    }

    @Subscribe
    public void finishOAuth(FinishOAuthCommand command) {
        OAuthAuthenticator.OAuthCredentials oAuthCredentials = authenticator.retrieveCredentials(command.verifier);
        bus.post(new SaveAuthorizationCredentialsCommand(oAuthCredentials.token, oAuthCredentials.tokenSecret));
    }

    @Subscribe
    public void authorizationStateChanged(AuthenticationStateChangedEvent event) {
        authenticated = event.authenticated;
    }
}
