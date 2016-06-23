package com.zeyomir.ocfun.network.response;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CacheData {
    @Expose
    public String code;
    @Expose
    public String name;
    @Expose
    public float difficulty;
    @Expose
    public float terrain;
    @Expose
    public int founds;
    @Expose
    public int notFounds;
    @Expose
    public String shortDescription;
    @Expose
    public String description;
    @Expose
    public String hint;
    @Expose
    public String type;
    @Expose
    public String location;
    @Expose
    public String lastFound;
    @Expose
    public String owner;
    @Expose
    public String attributes;
    @Expose
    public String notes;
    @Expose
    public String trackables;
    @Expose
    public boolean requiresPassword;
    @Expose
    public boolean isFound;
    @Expose
    public ContainerSize size;
    @Expose
    public List<ImageData> images;
    @Expose
    public List<LogData> latestLogs;
    @Expose
    public List<AlternateWaypointData> alternateWaypoints;
}
