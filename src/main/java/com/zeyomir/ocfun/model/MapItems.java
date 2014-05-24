package com.zeyomir.ocfun.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import com.google.android.maps.*;
import com.zeyomir.ocfun.gui.SingleCache;

import java.util.ArrayList;

public class MapItems extends ItemizedOverlay {
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Context mContext;

    public MapItems(Drawable arg0) {
        super(boundCenter(arg0));
    }

    public MapItems(Drawable defaultMarker, Context context) {
        super(boundCenter(defaultMarker));
        mContext = context;
        populate(); //??
    }

    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    @Override
    protected boolean onTap(int index) {
        OverlayItem item = mOverlays.get(index);
        if (item.getSnippet().startsWith("B"))
            Toast.makeText(mContext, "Twoje położenie\n" + item.getSnippet(),
                    Toast.LENGTH_SHORT).show();
        else {
            Intent i = new Intent(mContext, SingleCache.class);
            i.putExtra("_id", Long.parseLong(item.getSnippet()));
            mContext.startActivity(i);
        }

        return true;
    }

    @Override
    public void draw(Canvas canvas, MapView map, boolean shadow) {
        super.draw(canvas, map, shadow);

        if (shadow == false) {
            OverlayItem item;
            OverlayItem myPos = null;
            GeoPoint point;
            Point ptScreenCoord;
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(15);
            paint.setARGB(255, 0, 0, 0);

            for (int i = 0; i < mOverlays.size(); i++) {
                item = mOverlays.get(i);
                if (item.getTitle().equals("")) {
                    myPos = item;
                    continue;
                }
                point = item.getPoint();
                ptScreenCoord = new Point();
                map.getProjection().toPixels(point, ptScreenCoord);

                canvas.drawText(item.getTitle(), ptScreenCoord.x,
                        ptScreenCoord.y + 35, paint);
            }
            if (myPos != null) {
                Projection projection = map.getProjection();
                Point pt = new Point();

                GeoPoint gp = myPos.getPoint();
                projection.toPixels(gp, pt);

                GeoPoint newGeos = new GeoPoint(gp.getLatitudeE6()
                        + changeMetersToMicrodegrees(Integer.parseInt(myPos.getSnippet().replaceAll("[a-zA-Z :łą\\.]+", "")) / 10),
                        //+ changeMetersToMicrodegrees(100),
                        gp.getLongitudeE6()
                );
                Point pt2 = new Point();
                projection.toPixels(newGeos, pt2);
                float circleRadius = Math.abs(pt2.y - pt.y);

                Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

                circlePaint.setColor(Color.BLUE);
                circlePaint.setAlpha(16);
                circlePaint.setStyle(Style.FILL_AND_STROKE);
                canvas.drawCircle((float) pt.x, (float) pt.y, circleRadius,
                        circlePaint);

                circlePaint.setColor(Color.BLUE);
                circlePaint.setAlpha(128);
                circlePaint.setStyle(Style.STROKE);
                canvas.drawCircle((float) pt.x, (float) pt.y, circleRadius,
                        circlePaint);
            }
        }
        super.draw(canvas, map, shadow);
    }

    private int changeMetersToMicrodegrees(int meters) {
        return meters * 9;
    }
}
