package com.zeyomir.ocfun.dao;

import com.zeyomir.ocfun.R;

public enum InternalResourceMapper {
    note(2),
    comment(3),
    found(4),
    notfound(5),
    event(6),
    moving(7),
    multi(8),
    owncache(9),
    quiz(10),
    traditional(11),
    unknown(12),
    virtual(13),
    webcam(14),
    custom(15);

    private final int id;

    InternalResourceMapper(int id) {
        this.id = id;
    }

    public static int map(int myResId) {
        switch (myResId) {
            case 2:
                return R.drawable.note;
            case 3:
                return R.drawable.comment;
            case 4:
                return R.drawable.found;
            case 5:
                return R.drawable.notfound;
            case 6:
                return R.drawable.event;
            case 7:
                return R.drawable.moving;
            case 8:
                return R.drawable.multi;
            case 9:
                return R.drawable.owncache;
            case 10:
                return R.drawable.quiz;
            case 11:
                return R.drawable.traditional;
            case 12:
                return R.drawable.unknown;
            case 13:
                return R.drawable.virtual;
            case 14:
                return R.drawable.webcam;
            case 15:
                return R.drawable.webcam;
            default:
                return 0;
        }
    }

    public int id() {
        return id;
    }
}