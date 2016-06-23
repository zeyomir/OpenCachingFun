package com.zeyomir.ocfun.network.response.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.zeyomir.ocfun.network.response.AlternateWaypointData;
import com.zeyomir.ocfun.network.response.CacheData;
import com.zeyomir.ocfun.network.response.ContainerSize;
import com.zeyomir.ocfun.network.response.ImageData;
import com.zeyomir.ocfun.network.response.LogData;
import com.zeyomir.ocfun.storage.model.CacheModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CacheDataDeserializer implements JsonDeserializer<CacheData> {
    @Override
    public CacheData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final CacheData cacheData = new CacheData();
        cacheData.code = jsonObject.get("code").getAsString();
        cacheData.name = jsonObject.get("name").getAsString();
        cacheData.difficulty = jsonObject.get("difficulty").getAsInt();
        cacheData.terrain = jsonObject.get("terrain").getAsInt();
        cacheData.requiresPassword = jsonObject.get("req_passwd").getAsBoolean();
        cacheData.description = jsonObject.get("description").getAsString();
        cacheData.shortDescription = jsonObject.get("short_description").getAsString();
        cacheData.hint = jsonObject.get("hint2").getAsString();
        cacheData.type = jsonObject.get("type").getAsString();
        cacheData.location = jsonObject.get("location").getAsString();
        cacheData.owner = jsonObject.getAsJsonObject("owner").get("username").getAsString();

        cacheData.founds = jsonObject.get("founds").getAsInt();
        cacheData.notFounds = jsonObject.get("notfounds").getAsInt();
        cacheData.size = context.deserialize(jsonObject.get("size2"), ContainerSize.class);

        JsonElement last_found = jsonObject.get("last_found");
        if (last_found == null || last_found.isJsonNull()) {
            cacheData.lastFound = null;
        } else {
            cacheData.lastFound = last_found.getAsString().split("T")[0].replace('-', '.');
        }

        JsonElement notes = jsonObject.get("my_notes");
        if (notes == null || notes.isJsonNull())
            cacheData.notes = "";
        else
            cacheData.notes = notes.getAsString();

        JsonElement attributes = jsonObject.get("attrnames");
        if (attributes == null || attributes.isJsonNull())
            cacheData.attributes = "";
        else {
            StringBuilder builder = new StringBuilder();
            JsonArray attributesJsonArray = attributes.getAsJsonArray();
            for (JsonElement element : attributesJsonArray)
                builder.append(CacheModel.TOKENIZER).append(element.getAsString());
            cacheData.attributes = builder.length() > 0 ? builder.deleteCharAt(0).toString() : "";
        }

        JsonElement isFound = jsonObject.get("is_found");
        if (isFound == null || isFound.isJsonNull())
            cacheData.isFound = false;
        else
            cacheData.isFound = isFound.getAsBoolean();

        cacheData.images = context.deserialize(jsonObject.getAsJsonArray("images"), new TypeToken<ArrayList<ImageData>>() {
        }.getType());
        cacheData.latestLogs = context.deserialize(jsonObject.getAsJsonArray("latest_logs"), new TypeToken<ArrayList<LogData>>() {
        }.getType());
        cacheData.alternateWaypoints = context.deserialize(jsonObject.getAsJsonArray("alt_wpts"), new TypeToken<ArrayList<AlternateWaypointData>>() {
        }.getType());

        StringBuilder builder = new StringBuilder();
        for (JsonElement object : jsonObject.getAsJsonArray("trackables"))
            builder.append(CacheModel.TOKENIZER).append(object.getAsJsonObject().get("name").getAsString());

        cacheData.trackables = builder.length() > 0 ? builder.deleteCharAt(0).toString() : "";

        return cacheData;
    }
}
