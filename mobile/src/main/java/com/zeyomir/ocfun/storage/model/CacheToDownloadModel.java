package com.zeyomir.ocfun.storage.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

@Table(name = "cacheToDownload", id = "_id")
public class CacheToDownloadModel extends Model {
    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, index = true)
    public String code;

    public static int count() {
        return new Select().from(CacheToDownloadModel.class).count();
    }

    public static CacheToDownloadModel getFirst() {
        return new Select().from(CacheToDownloadModel.class).orderBy("_id").limit(1).executeSingle();
    }

    public static void remove(String code) {
        new Delete().from(CacheToDownloadModel.class).where("code LIKE ?", code).execute();
    }
}
