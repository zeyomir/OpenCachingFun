package com.zeyomir.ocfun.utils;

public class AzimuthSanitizer {
    public static int sanitize(int azimuth){
        if (azimuth > 360)
            return azimuth - 360;
        if (azimuth < 0)
            return azimuth + 360;
        return azimuth;
    }
}
