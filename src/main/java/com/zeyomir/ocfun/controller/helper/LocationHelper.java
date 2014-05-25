package com.zeyomir.ocfun.controller.helper;

import android.location.Location;

public class LocationHelper {

    public static String getDistance(Location from, Location to) {
        Float f = from.distanceTo(to);
        String d;
        if (f >= 1000) {
            f /= 1000;
            d = (f.toString()).substring(0,
                    f.toString().indexOf('.') + 2)
                    + " km";
        } else {
            d = Math.round(f) + " m";
        }
        return d;
    }

    public static String getAzimuth(Location from, Location to) {
        int azimuth = Math.round(from.bearingTo(to));
        if (azimuth < 0) {
            azimuth += 360;
        }
        return decodeAzimuth(azimuth);

    }

    private static String decodeAzimuth(int azimuth) {
        if ((azimuth <= 12) || (azimuth > 349)) {
            return "N";
        } else if (azimuth > 12 && azimuth <= 34) {
            return "NNE";
        } else if (azimuth > 34 && azimuth <= 57) {
            return "NE";
        } else if (azimuth > 57 && azimuth <= 79) {
            return "ENE";
        } else if (azimuth > 79 && azimuth <= 102) {
            return "E";
        } else if (azimuth > 102 && azimuth <= 124) {
            return "ESE";
        } else if (azimuth > 124 && azimuth <= 147) {
            return "SE";
        } else if (azimuth > 147 && azimuth <= 169) {
            return "SSE";
        } else if (azimuth > 169 && azimuth <= 192) {
            return "S";
        } else if (azimuth > 192 && azimuth <= 214) {
            return "SSW";
        } else if (azimuth > 214 && azimuth <= 237) {
            return "SW";
        } else if (azimuth > 237 && azimuth <= 259) {
            return "WSW";
        } else if (azimuth > 259 && azimuth <= 282) {
            return "W";
        } else if (azimuth > 282 && azimuth <= 304) {
            return "WNW";
        } else if (azimuth > 304 && azimuth <= 327) {
            return "NW";
        } else if (azimuth > 327 && azimuth <= 349) {
            return "NNW";
        } else {
            return "";
        }
    }
}
