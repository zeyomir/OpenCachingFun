package com.zeyomir.ocfun.storage.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.zeyomir.ocfun.network.response.ContainerSize;

import java.util.List;

@Table(name = "cache", id = "_id")
public class CacheModel extends Model {
    public static final String TOKENIZER = "#!#";
    public static final String ORDER_BY_NAME = "name ASC";
    public static final String ORDER_BY_DISTANCE = "distance ASC";

    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, index = true)
    public String code;
    @Column
    public String name;
    @Column
    public float difficulty;
    @Column
    public float terrain;
    @Column
    public int founds;
    @Column
    public int notFounds;
    @Column
    public String description;
    @Column
    public String shortDescription;
    @Column
    public String hint;
    @Column
    public CacheType type;
    @Column
    public float latitude;
    @Column
    public float longitude;
    @Column
    public String lastFound;
    @Column
    public String owner;
    @Column
    public String attributes;
    @Column
    public String notes;
    @Column
    public boolean requiresPassword;
    @Column
    public boolean isFound;
    @Column
    public ContainerSize size;
    @Column
    public String trackables;
    @Column
    public int distance = -1;
    @Column
    public int azimuth = -1;

    public static int count() {
        return new Select().from(CacheModel.class).count();
    }

    public static void setPersonalNote(String code, String personalNote) {
        new Update(CacheModel.class).set("notes = ?", personalNote).where("code LIKE ?", code).execute();
    }

    public static void setDistance(String code, long distance, int azimuth) {
        new Update(CacheModel.class).set("distance = ?, azimuth = ?", distance, azimuth).where("code = ?", code).execute();
    }

    public static CacheModel getByCacheCode(String cacheCode) {
        return new Select().from(CacheModel.class).where("code LIKE ?", cacheCode).executeSingle();
    }

    public static List<CacheModel> getForList(String order, String filter) {
        From from = new Select("_id", "name", "code", "shortDescription", "type", "isFound", "distance", "azimuth").from(CacheModel.class);
        if (filter != null && !filter.isEmpty())
            from = from.where("name LIKE '%" + filter + "%'");
        return from.orderBy(order).execute();
    }

    public static List<CacheModel> getForDistanceComputing() {
        return new Select("_id", "code", "latitude", "longitude").from(CacheModel.class).execute();
    }

    public static List<CacheModel> getAllCodes() {
        return new Select("_id", "code").from(CacheModel.class).execute();
    }

    public static void truncate() {
        new Delete().from(CacheModel.class).execute();
    }
}
