package com.zeyomir.ocfun.network;

import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.eventbus.event.GeneralNetworkErrorEvent;
import com.zeyomir.ocfun.eventbus.event.GeneralServerErrorEvent;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public abstract class DefaultCallback<T> implements Callback<T> {
    private final EventBus bus;

    DefaultCallback(EventBus bus) {
        this.bus = bus;
    }

    public abstract void onSuccess(Call<T> call, Response<T> response);

    protected void customOnNotSuccess(Call call, Response response) {
    }

    protected void customOnFailure(Call<T> call, Throwable t) {
    }

    private void onNotSuccess(Call call, Response response) {
        try {
            Timber.e("HTTP --> %s\n%s\nHTTP %d <--- %s", call.request().url(), call.request().body(), response.code(), response.errorBody().string());
        } catch (IOException e) {
            Timber.e(e, "HTTP --> %s\n%s", call.request().url(), call.request().body());
        } finally {
            bus.post(new GeneralServerErrorEvent());
        }
        customOnNotSuccess(call, response);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(call, response);
        } else {
            onNotSuccess(call, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof IOException) {
            bus.post(new GeneralNetworkErrorEvent());
            Timber.d(t, "HTTP --> %s\n%s", call.request().url(), call.request().body());
        } else {
            bus.post(new GeneralServerErrorEvent());
            Timber.e(t, "HTTP --> %s\n%s", call.request().url(), call.request().body());
        }
        customOnFailure(call, t);
    }
}
