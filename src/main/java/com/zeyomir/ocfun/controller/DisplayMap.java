package com.zeyomir.ocfun.controller;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.dao.CacheDAO;
import com.zeyomir.ocfun.dao.InternalResourceMapper;
import com.zeyomir.ocfun.model.MapItems;

public class DisplayMap {

    public static MapItems getOverlays(Context context) {
        CacheDAO dao = new CacheDAO();
        dao.open();
        Cursor c = dao.list("");
        MapItems itemizedoverlay = new MapItems(context.getResources()
                .getDrawable(R.drawable.moving), context);

        int id;
        int type;
        String name;
        String[] coords;
        double lat;
        double lon;
        GeoPoint p;
        Drawable icon;

        OverlayItem item;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            id = c.getInt(0);
            type = c.getInt(1);
            name = c.getString(2);
            Log.i("MAP-added", id + ", " + name + ", " + type);
            coords = c.getString(3).split("\\|");
            lat = Double.parseDouble(coords[0]);
            lon = Double.parseDouble(coords[1]);

            p = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
            item = new OverlayItem(p, name, "" + id);
            icon = context.getResources().getDrawable(
                    InternalResourceMapper.map(type));
            icon.setBounds(0 - icon.getIntrinsicWidth() / 2,
                    0 - icon.getIntrinsicHeight() / 2,
                    icon.getIntrinsicWidth() / 2, icon.getIntrinsicHeight() / 2);

            item.setMarker(icon);

            itemizedoverlay.addOverlay(item);
            c.moveToNext();
        }
        dao.close();
        return itemizedoverlay;
    }
}
