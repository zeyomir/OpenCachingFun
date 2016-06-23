package com.zeyomir.ocfun.configuration;

import android.app.Application;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeyomir.ocfun.BuildConfig;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.network.API;
import com.zeyomir.ocfun.network.OKAPI;
import com.zeyomir.ocfun.network.oauth.OAuthAuthenticator;
import com.zeyomir.ocfun.network.oauth.OkHttpOAuthConsumer;
import com.zeyomir.ocfun.network.oauth.SigningInterceptor;
import com.zeyomir.ocfun.network.response.CacheData;
import com.zeyomir.ocfun.network.response.LogData;
import com.zeyomir.ocfun.network.response.utils.CacheDataDeserializer;
import com.zeyomir.ocfun.network.response.utils.LogDataDeserializer;
import com.zeyomir.ocfun.storage.PreferencesManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class HttpModule {
    public static final String OAUTH_CALLBACK_SCHEME = "ocfun";
    public static final String OAUTH_CALLBACK_URL = "ocfun://callback";
    public static final String BASE_URL = "http://opencaching.pl/";
    public static final String REQUEST_TOKEN_URL = "http://opencaching.pl/okapi/services/oauth/request_token";
    public static final String ACCESS_TOKEN_URL = "http://opencaching.pl/okapi/services/oauth/access_token";
    public static final String AUTHORIZATION_URL = "http://opencaching.pl/okapi/services/oauth/authorize";

    @Provides
    @Singleton
    API provideAPI(OKAPI service, OAuthAuthenticator authenticator, EventBus eventBus) {
        return new API(service, authenticator, eventBus);
    }

    @Provides
    @Singleton
    OAuthProvider provideOAuthProvider() {
        return new DefaultOAuthProvider(
                REQUEST_TOKEN_URL,
                ACCESS_TOKEN_URL,
                AUTHORIZATION_URL);
    }

    @Provides
    @Singleton
    OAuthConsumer provideOAuthConsumer(Application context, PreferencesManager preferencesManager) {
        OkHttpOAuthConsumer oAuthConsumer = new OkHttpOAuthConsumer(context.getString(R.string.oauth_consumer_key), context.getString(R.string.oauth_consumer_secret));
        oAuthConsumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
        if (preferencesManager.userAuthentication.isAuthenticated())
            oAuthConsumer.setTokenWithSecret(preferencesManager.userAuthentication.getToken(), preferencesManager.userAuthentication.getTokenSecret());
        return oAuthConsumer;
    }

    @Provides
    @Singleton
    OAuthAuthenticator provideAuthenticator(Application application, OAuthProvider oAuthProvider, OAuthConsumer oAuthConsumer) {
        return new OAuthAuthenticator(application, oAuthProvider, oAuthConsumer, OAUTH_CALLBACK_URL);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(CacheData.class, new CacheDataDeserializer())
                .registerTypeAdapter(LogData.class, new LogDataDeserializer())
                .create();
    }

    @Provides
    @Singleton
    Cache provideCache(Application application) {
        return new Cache(application.getCacheDir(), 10 * 1024 * 1024);
    }

    @Provides
    @Singleton
    @Named("signing")
    Interceptor provideSigningInterceptor(final OAuthConsumer oAuthConsumer) {
        return new SigningInterceptor(oAuthConsumer);
    }

    @Provides
    @Singleton
    @Named("logging")
    Interceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache, @Named("signing") Interceptor signingInterceptor, @Named("logging") Interceptor loggingInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(signingInterceptor)
                .addInterceptor(loggingInterceptor);
        if (BuildConfig.DEBUG)
            builder.addNetworkInterceptor(new StethoInterceptor());
        return builder.build();
    }

    @Provides
    @Singleton
    OKAPI provideOKAPI(Gson gson, OkHttpClient client) {
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(gsonConverterFactory)
                .validateEagerly(true)
                .build();
        return restAdapter.create(OKAPI.class);
    }
}
