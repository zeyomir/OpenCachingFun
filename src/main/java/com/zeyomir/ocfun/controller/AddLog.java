package com.zeyomir.ocfun.controller;

import android.content.ContentValues;
import android.content.Context;
import com.zeyomir.ocfun.dao.CacheDAO;
import com.zeyomir.ocfun.dao.InternalResourceMapper;
import com.zeyomir.ocfun.dao.MyLogDAO;
import com.zeyomir.ocfun.model.MyLogbookEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AddLog {
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public AddLog(Context context) {
        this.context = context;
    }

    public void add(Map<String, String> data) {
        long cacheId = Long.parseLong(data.get("cacheId"));
        String cacheCode = data.get("cacheCode");
        String cacheName = data.get("cacheName");
        String user = "OCFun";
        String date = dateFormat.format(new Date());
        String message = data.get("message");
        int type = Integer.parseInt(data.get("type"));
        String password = data.get("password");
        int rating = Integer.parseInt(data.get("rating"));
        boolean recommendation = Boolean.parseBoolean(data.get("recommendation"));
        boolean needsMaintenance = Boolean.parseBoolean(data.get("needsMaintenance"));
        String errorMessage = null;

        MyLogbookEntry myLogbookEntry = new MyLogbookEntry(cacheId, cacheCode, cacheName, user, date, message, type, password, rating, recommendation, needsMaintenance, errorMessage);
        MyLogDAO.save(myLogbookEntry);
        if (type == InternalResourceMapper.found.id()) {
            ContentValues contentValues = new ContentValues();
            CacheDAO.setFoundStatus(contentValues, true);
            CacheDAO.setLastFoundDate(contentValues, myLogbookEntry.date.split(" ")[0]);
            CacheDAO.update(contentValues, cacheId);
        }
    }
}
