package com.zeyomir.ocfun.network.response.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.zeyomir.ocfun.network.response.ImageData;
import com.zeyomir.ocfun.network.response.LogData;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LogDataDeserializer implements JsonDeserializer<LogData> {

    @Override
    public LogData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final LogData logData = new LogData();
        logData.uuid = jsonObject.get("uuid").getAsString();
        logData.type = jsonObject.get("type").getAsString();
        logData.date = jsonObject.get("date").getAsString();
        logData.comment = jsonObject.get("comment").getAsString();
        logData.username = jsonObject.getAsJsonObject("user").get("username").getAsString();
        logData.images = context.deserialize(jsonObject.getAsJsonArray("images"), new TypeToken<ArrayList<ImageData>>() {}.getType());
        return logData;
    }
}
