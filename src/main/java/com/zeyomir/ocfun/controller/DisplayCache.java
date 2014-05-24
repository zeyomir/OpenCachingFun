package com.zeyomir.ocfun.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.zeyomir.ocfun.dao.CacheDAO;
import com.zeyomir.ocfun.dao.ImageDAO;
import com.zeyomir.ocfun.dao.LogDAO;
import com.zeyomir.ocfun.gui.Compass;
import com.zeyomir.ocfun.gui.Images;
import com.zeyomir.ocfun.gui.Logs;
import com.zeyomir.ocfun.gui.Maps;
import com.zeyomir.ocfun.model.Cache;

public class DisplayCache {
    public static Cache getCache(Intent i) {
        Bundle b = i.getExtras();
        long id = b.getLong(CacheDAO.idColumn);
        return CacheDAO.get(id);
    }

    public static String rot13(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'm')
                c += 13;
            else if (c >= 'n' && c <= 'z')
                c -= 13;
            else if (c >= 'A' && c <= 'M')
                c += 13;
            else if (c >= 'A' && c <= 'Z')
                c -= 13;
            sb.append(c);
        }
        return sb.toString();
    }

    public static Intent createImagesIntent(Context c, long cacheId) {
        Intent i = new Intent(c, Images.class);
        i.putExtra(ImageDAO.cacheIdColumn, cacheId);
        return i;
    }

    public static Intent createLogsIntent(Context c, long cacheId) {
        Intent i = new Intent(c, Logs.class);
        i.putExtra(LogDAO.cacheIdColumn, cacheId);
        return i;
    }

    public static Intent createCompassIntent(Context c, long cacheId) {
        Intent i = new Intent(c, Compass.class);
        i.putExtra(CacheDAO.idColumn, cacheId);
        return i;
    }

    public static Intent createMapIntent(Context c, String coords) {
        Intent i = new Intent(c, Maps.class);
        String[] temp = coords.split("\\|");
        i.putExtra("lat", temp[0]);
        i.putExtra("lon", temp[1]);
        return i;
    }
}
