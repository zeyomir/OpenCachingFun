package com.zeyomir.ocfun.network.oauth;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;

public class OkHttpOAuthConsumer extends AbstractOAuthConsumer {

    public OkHttpOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        if (!(request instanceof okhttp3.Request)) {
            throw new IllegalArgumentException("This consumer expects requests of type " + okhttp3.Request.class.getCanonicalName());
        }
        return new HttpRequestAdapter((okhttp3.Request) request);
    }
}
