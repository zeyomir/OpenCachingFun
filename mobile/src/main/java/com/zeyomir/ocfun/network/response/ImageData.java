package com.zeyomir.ocfun.network.response;

import com.google.gson.annotations.Expose;

public class ImageData {
    @Expose
    public String caption;
    @Expose
    public boolean is_spoiler;
    @Expose
    public String url;
    @Expose
    public String thumb_url ;
}
