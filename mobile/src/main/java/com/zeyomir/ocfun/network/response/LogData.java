package com.zeyomir.ocfun.network.response;

import com.google.gson.annotations.Expose;

import java.util.List;

public class LogData {
    @Expose
    public String uuid;
    @Expose
    public String date;
    @Expose
    public String type;
    @Expose
    public String comment;
    @Expose
    public String username;
    @Expose
    public List<ImageData> images;
}



