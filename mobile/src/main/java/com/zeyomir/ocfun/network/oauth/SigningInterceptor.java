package com.zeyomir.ocfun.network.oauth;

import java.io.IOException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class SigningInterceptor implements Interceptor {
    private final OAuthConsumer oAuthConsumer;

    public SigningInterceptor(OAuthConsumer oAuthConsumer) {
        this.oAuthConsumer = oAuthConsumer;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request requestToSend = chain.request();
        try {
            HttpRequestAdapter signedAdapter = (HttpRequestAdapter) oAuthConsumer.sign(chain.request());
            requestToSend = (Request) signedAdapter.unwrap();
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
            Timber.e(e, "%s\n%s", requestToSend.url(), requestToSend.body().toString());
        }
        Response response = chain.proceed(requestToSend);
        return response;
    }
}
