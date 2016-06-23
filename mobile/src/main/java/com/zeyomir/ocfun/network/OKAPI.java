package com.zeyomir.ocfun.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.zeyomir.ocfun.network.response.CacheData;
import com.zeyomir.ocfun.network.response.CachesSearchResponse;
import com.zeyomir.ocfun.network.response.LogSubmissionResponse;
import com.zeyomir.ocfun.network.response.PersonalNoteSubmissionResponse;
import com.zeyomir.ocfun.network.response.UserDetailsResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OKAPI {
    @GET("okapi/services/caches/geocache?log_fields=uuid|date|user|type|comment|images&langpref=pl")
    Call<CacheData> cacheDetails(@NonNull @Query("cache_code") String code, @Query("fields") String fields);

    @GET("okapi/services/caches/geocache?fields=location&langpref=pl")
    Call<JsonObject> cacheLocation(@NonNull @Query("cache_code") String code);

    @GET("okapi/services/caches/search/nearest?limit=500")
    Call<CachesSearchResponse> cachesByLocation(@NonNull @Query("center") String center, @Query("radius") float radius, @Nullable @Query("found_status") String foundStatus, @Nullable @Query("exclude_my_own") Boolean excluydeMyOwn, @Nullable @Query("ignored_status") String ignoredStatus);

    @GET("okapi/services/caches/search/all?limit=500")
    Call<CachesSearchResponse> cachesByName(@NonNull @Query("name") String name, @Nullable @Query("found_status") String foundStatus, @Nullable @Query("exclude_my_own") Boolean excluydeMyOwn, @Nullable @Query("ignored_status") String ignoredStatus);

    @GET("okapi/services/caches/search/all?watched_only=true&limit=500")
    Call<CachesSearchResponse> watchedCaches();

    @GET("okapi/services/users/user")
    Call<UserDetailsResponse> userDetails(@NonNull @Query("fields") String fields);

    @GET("images/uploads/{name}")
    Call<ResponseBody> image(@NonNull @Path("name") String name);

    @POST("okapi/services/logs/submit?langpref=pl&comment_format=plaintext")
    Call<LogSubmissionResponse> submitLogEntry(@NonNull @Query("cache_code") String cacheCode, @NonNull @Query("logtype") String logtype, @NonNull @Query("comment") String comment, @NonNull @Query("when") String when, @Nullable @Query("password") String password, @Nullable @Query("rating") Integer rating, @Nullable @Query("recommend") Boolean recommend, @Nullable @Query("needs_maintenance") Boolean needsMaintenance);

    @POST("okapi/services/caches/save_personal_notes")
    Call<PersonalNoteSubmissionResponse> saveNote(@NonNull @Query("cache_code") String cacheCode, @NonNull @Query("new_value") String new_value, @Nullable @Query("old_value") String old_value);
}
