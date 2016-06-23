package com.zeyomir.ocfun.network.response;

import com.google.gson.annotations.Expose;

public class LogSubmissionResponse {
    @Expose
    public boolean success;
    @Expose
    public String message;
    @Expose
    public String log_uuid;
}
