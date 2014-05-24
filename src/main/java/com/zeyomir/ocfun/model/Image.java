package com.zeyomir.ocfun.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Image {
    public final long id;
    public final long cacheId;
    public final String name;
    public final String path;

    public Image(long id, long cacheId, String name, String path) {
        this.id = id;
        this.cacheId = cacheId;
        this.name = name;
        this.path = path;
    }

    public Image(long cacheId, String cacheCode, JSONObject data) throws JSONException {
        this.id = 0;
        this.cacheId = cacheId;
        String name = data.getString("caption").trim();
        if (name.equals(""))
            name = "Zdjęcie";
        if (data.getBoolean("is_spoiler"))
            name = "[SPOILER!] " + name;
        this.name = name;
        String[] temp = data.getString("url").split("/");
        this.path = cacheCode + "/" + temp[temp.length - 1];
    }
}
