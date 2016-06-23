package com.zeyomir.ocfun.network.response;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CachesSearchResponse {
    @Expose
    public List<String> results;
    @Expose
    public boolean more;
}
