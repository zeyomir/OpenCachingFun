package com.zeyomir.ocfun.storage.model;

import android.support.annotation.DrawableRes;

import com.zeyomir.ocfun.R;

public enum CacheType {
    TRADITIONAL(R.drawable.traditional),
    MULTI(R.drawable.multi),
    QUIZ(R.drawable.quiz),
    VIRTUAL(R.drawable.virtual),
    EVENT(R.drawable.event),
    UNKNOWN(R.drawable.unknown);

    @DrawableRes
    private final int drawableId;

    CacheType(int drawableId) {
        this.drawableId = drawableId;
    }

    @DrawableRes
    public int getDrawableId() {
        return drawableId;
    }


    public static CacheType fromText(String type) {
        switch (type) {
            case Constants.TRADITIONAL_TEXT:
                return TRADITIONAL;
            case Constants.MULTI_TEXT:
                return MULTI;
            case Constants.QUIZ_TEXT:
                return QUIZ;
            case Constants.VIRTUAL_TEXT:
                return VIRTUAL;
            case Constants.EVENT_TEXT:
                return EVENT;
            default:
                return UNKNOWN;
        }
    }

    private static class Constants {
        private static final String TRADITIONAL_TEXT = "Traditional";
        private static final String MULTI_TEXT = "Multi";
        private static final String QUIZ_TEXT = "Quiz";
        private static final String VIRTUAL_TEXT = "Virtual";
        private static final String EVENT_TEXT = "Event";
    }
}
