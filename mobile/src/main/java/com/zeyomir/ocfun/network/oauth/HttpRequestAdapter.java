package com.zeyomir.ocfun.network.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.http.HttpRequest;
import okhttp3.Request;
import okio.Buffer;
import timber.log.Timber;

public class HttpRequestAdapter implements HttpRequest {
    private Request request;

    public HttpRequestAdapter(Request request) {
        this.request = request;
    }

    @Override
    public Map<String, String> getAllHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        for (String name : request.headers().names()) {
            String headerValue = request.header(name);
            headers.put(name, headerValue);
        }
        return headers;
    }

    @Override
    public String getContentType() {
        if (request.body() == null || request.body().contentType() == null) {
            return null;
        }
        return request.body().contentType().toString();
    }

    @Override
    public String getHeader(String key) {
        return request.header(key);
    }

    @Override
    public InputStream getMessagePayload() throws IOException {
        if (request.body() == null) {
            return null;
        }
        Buffer buf = new Buffer();
        request.body().writeTo(buf);
        return buf.inputStream();
    }

    @Override
    public String getMethod() {
        return request.method();
    }

    @Override
    public String getRequestUrl() {
        return request.url().uri().toString();
    }

    @Override
    public void setHeader(String key, String value) {
        request = request.newBuilder().addHeader(key, value).build();
    }

    @Override
    public void setRequestUrl(String url) {
        request = request.newBuilder().url(url).build();
    }

    @Override
    public Object unwrap() {
        return request;
    }
}
