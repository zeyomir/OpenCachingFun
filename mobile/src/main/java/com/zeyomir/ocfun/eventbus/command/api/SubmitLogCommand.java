package com.zeyomir.ocfun.eventbus.command.api;

import com.zeyomir.ocfun.storage.model.LogType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SubmitLogCommand extends ApiCommand {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    public final long logModelId;
    public final String cacheCode;
    public final LogType logtype;
    public final String comment;
    public final String when;
    public final String password;
    public final Integer rating;
    public final Boolean recommend;
    public final Boolean needsMaintenance;

    public SubmitLogCommand(long id, String cacheCode, LogType logtype, String comment, Date when, String password, Integer rating, Boolean recommend, Boolean needsMaintenance) {
        this.logModelId = id;
        this.cacheCode = cacheCode;
        this.logtype = logtype;
        this.comment = comment;
        this.when = dateFormat.format(when);
        this.password = password;
        this.rating = rating;
        this.recommend = recommend;
        this.needsMaintenance = needsMaintenance;
    }
}
