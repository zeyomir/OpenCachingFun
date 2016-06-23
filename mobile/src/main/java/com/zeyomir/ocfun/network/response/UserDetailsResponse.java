package com.zeyomir.ocfun.network.response;

import com.google.gson.annotations.Expose;

public class UserDetailsResponse {
    @Expose
    public String username;
    @Expose
    public int caches_found;
    @Expose
    public int caches_notfound;
    @Expose
    public int caches_hidden;
    @Expose
    public int rcmds_given;
}
