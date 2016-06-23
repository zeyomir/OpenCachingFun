package com.zeyomir.ocfun.storage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.zeyomir.ocfun.network.response.UserDetailsResponse;
import com.zeyomir.ocfun.storage.model.CacheModel;

public class PreferencesManager {
    private final SharedPreferences getter;
    private final SharedPreferences.Editor setter;
    private final String FALSE_STRING = String.valueOf(false);
    private final String TRUE_STRING = String.valueOf(true);

    public final User user;
    public final Settings settings;
    public final UserAuthentication userAuthentication;

    public PreferencesManager(Application application) {
        Context context = application;
        getter = context.getSharedPreferences("OCFUN_preferences", Context.MODE_PRIVATE);
        setter = getter.edit();
        user = new User();
        settings = new Settings();
        userAuthentication = new UserAuthentication();
    }

    private void set(String key, String value) {
        setter.putString(key, value);
        setter.apply();
    }

    private void set(String[] keys, String[] values) {
        if (keys.length != values.length) {
            throw new RuntimeException("Keys and values count does not match!");
        }
        for (int i = 0; i < keys.length; i++)
            setter.putString(keys[i], values[i]);
        setter.apply();
    }

    private String get(String key, String defaultVaule) {
        return getter.getString(key, defaultVaule);
    }

    public class User {
        private static final String USER_LEARNED_DRAWER = "user.learnedDrawer";
        private static final String USER_USERNAME = "user.username";
        private static final String USER_CACHES_FOUND = "user.cachesFound";
        private static final String USER_CACHES_NOT_FOUND = "user.cachesNotFound";
        private static final String USER_CACHES_HIDDEN = "user.cachesHidden";
        private static final String USER_RECOMMENDATIONS_GIVEN = "user.recommendationsGiven";
        private static final String USER_LOCATION_LATITUDE = "user.location.latitude";
        private static final String USER_LOCATION_LONGITUDE = "user.location.longitude";

        public boolean learnedDrawer() {
            return Boolean.parseBoolean(get(USER_LEARNED_DRAWER, FALSE_STRING));
        }

        public void learnedDrawer(boolean learned) {
            set(USER_LEARNED_DRAWER, String.valueOf(learned));
        }

        public void saveDetails(UserDetailsResponse userDetails) {
            set(new String[]{USER_USERNAME, USER_CACHES_FOUND, USER_CACHES_NOT_FOUND, USER_CACHES_HIDDEN, USER_RECOMMENDATIONS_GIVEN}, new String[]{userDetails.username, String.valueOf(userDetails.caches_found), String.valueOf(userDetails.caches_notfound), String.valueOf(userDetails.caches_hidden), String.valueOf(userDetails.rcmds_given)});
        }

        public String getUsername() {
            return get(USER_USERNAME, null);
        }

        public int getCachesFound() {
            return Integer.parseInt(get(USER_CACHES_FOUND, null));
        }

        public int getCachesNotFound() {
            return Integer.parseInt(get(USER_CACHES_NOT_FOUND, null));
        }

        public int getCachesHidden() {
            return Integer.parseInt(get(USER_CACHES_HIDDEN, null));
        }

        public int getRecommendationsGiven() {
            return Integer.parseInt(get(USER_RECOMMENDATIONS_GIVEN, null));
        }

        public void clear() {
            set(new String[]{USER_USERNAME, USER_CACHES_FOUND, USER_CACHES_NOT_FOUND, USER_CACHES_HIDDEN, USER_RECOMMENDATIONS_GIVEN}, new String[]{"", "0", "0", "0", "0"});
        }
    }

    public class UserAuthentication {
        private static final String USER_IS_AUTHENTICATED = "user.authenticated";
        private static final String USER_OAUTH_TOKEN = "user.token";
        private static final String USER_OAUTH_TOKEN_SECRET = "user.tokenSecret";


        public boolean isAuthenticated() {
            return Boolean.parseBoolean(get(USER_IS_AUTHENTICATED, FALSE_STRING));
        }

        public String getTokenSecret() {
            return get(USER_OAUTH_TOKEN_SECRET, null);
        }

        public String getToken() {
            return get(USER_OAUTH_TOKEN, null);
        }

        public void authenticated(String token, String tokenSecret) {
            if (token == null || tokenSecret == null) {
                set(new String[]{USER_OAUTH_TOKEN, USER_OAUTH_TOKEN_SECRET, USER_IS_AUTHENTICATED}, new String[]{null, null, FALSE_STRING});
                user.clear();
                settings.setSkipOwn(false);
                settings.setSkipFound(false);
            } else {
                set(new String[]{USER_OAUTH_TOKEN, USER_OAUTH_TOKEN_SECRET, USER_IS_AUTHENTICATED}, new String[]{token, tokenSecret, TRUE_STRING});
            }
        }
    }

    public class Settings {
        private static final String SETTINGS_IMAGES_ONDEMAND = "settings.imagesOnDemand";
        private static final String SETTINGS_SKIP_OWN = "settings.skipOwn";
        private static final String SETTINGS_SKIP_FOUND = "settings.skipFound";
        private static final String SETTINGS_CACHES_ORDER = "settings.cachesOrder";

        public void setSkipOwn(boolean skipOwn) {
            set(SETTINGS_SKIP_OWN, String.valueOf(skipOwn));
        }

        public boolean getSkipOwn() {
            return Boolean.parseBoolean(get(SETTINGS_SKIP_OWN, FALSE_STRING));
        }

        public void setSkipFound(boolean skipFound) {
            set(SETTINGS_SKIP_FOUND, String.valueOf(skipFound));
        }

        public boolean getSkipFound() {
            return Boolean.parseBoolean(get(SETTINGS_SKIP_FOUND, FALSE_STRING));
        }

        public void setImagesOnDemand(boolean onDemand) {
            set(SETTINGS_IMAGES_ONDEMAND, String.valueOf(onDemand));
        }

        public boolean getImagesOnDemand() {
            return Boolean.parseBoolean(get(SETTINGS_IMAGES_ONDEMAND, FALSE_STRING));
        }

        public String getCachedOrderMethod() {
            return get(SETTINGS_CACHES_ORDER, CacheModel.ORDER_BY_NAME);
        }

        public void setCachesOrderMethod(String orderBy) {
            set(SETTINGS_CACHES_ORDER, orderBy);
        }
    }
}
