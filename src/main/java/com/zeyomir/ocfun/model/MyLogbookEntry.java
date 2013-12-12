package com.zeyomir.ocfun.model;

public class MyLogbookEntry {

    public final long id;
    public final long cacheId;
    public final String cacheCode;
    public final String cacheName;
    public final String user;
    public final String date;
    public final String message;
    public final int type;
    public final String password;
    public final int rating;
    public final boolean recommendation;
    public final boolean needsMaintenance;
    public final String errorMessage;

    public MyLogbookEntry(long cacheId, String cacheCode, String cacheName, String user, String date, String message, int type, String password, int rating, boolean recommendation, boolean needsMaintenance, String errorMessage) {
        this(0, cacheId, cacheCode, cacheName, user, date, message, type, password, rating, recommendation, needsMaintenance, errorMessage);
    }

    public MyLogbookEntry(long id, long cacheId, String cacheCode, String cacheName, String user, String date, String message, int type, String password, int rating, boolean recommendation, boolean needsMaintenance, String errorMessage) {
        this.id = id;
        this.cacheId = cacheId;
        this.cacheCode = cacheCode;
        this.cacheName = cacheName;
        this.user = user;
        this.date = date;
        this.message = message;
        this.type = type;
        this.password = password;
        this.rating = rating;
        this.recommendation = recommendation;
        this.needsMaintenance = needsMaintenance;
        this.errorMessage = errorMessage;
    }
}
