package com.zeyomir.ocfun.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.model.MyLogbookEntry;

public class MyLogDAO {
    public static final String[] from = {DbAdapter.KEY_LOGS_TYPE, DbAdapter.KEY_LOGS_RECOMMEND, DbAdapter.KEY_LOGS_MAINTENANCE, DbAdapter.KEY_LOGS_RATING, DbAdapter.KEY_LOGS_RATING, DbAdapter.KEY_LOGS_RATING, DbAdapter.KEY_LOGS_RATING, DbAdapter.KEY_LOGS_RATING, DbAdapter.KEY_LOGS_CACHE_NAME, DbAdapter.KEY_LOGS_DATE, DbAdapter.KEY_LOGS_BODY, DbAdapter.KEY_LOGS_PASSWORD, DbAdapter.KEY_LOGS_ERROR};
    public static final int[] to = {R.id.list_logs_type, R.id.list_logs_recommend, R.id.list_logs_maintenance, R.id.list_logs_rating1, R.id.list_logs_rating2, R.id.list_logs_rating3, R.id.list_logs_rating4, R.id.list_logs_rating5, R.id.list_logs_cache_name, R.id.list_logs_date, R.id.list_logs_body, R.id.list_logs_pass, R.id.list_logs_error};

    public static final String idColumn = DbAdapter.KEY_LOGS_ID;
    public static final String cacheIdColumn = DbAdapter.KEY_LOGS_CACHE_ID;

    public static void save(MyLogbookEntry l) {
        DbAdapter db = DbAdapter.open();
        db.insert(map(l), DbAdapter.DATABASE_TABLE_MY_LOGS);
        DbAdapter.close();
    }

    private static ContentValues map(MyLogbookEntry l) {
        ContentValues values = new ContentValues();
        values.put(DbAdapter.KEY_LOGS_CACHE_ID ,l.cacheId);
        values.put(DbAdapter.KEY_LOGS_CACHE_CODE ,l.cacheCode);
        values.put(DbAdapter.KEY_LOGS_CACHE_NAME ,l.cacheName);
        values.put(DbAdapter.KEY_LOGS_WHO ,l.user);
        values.put(DbAdapter.KEY_LOGS_DATE ,l.date);
        values.put(DbAdapter.KEY_LOGS_BODY ,l.message);
        values.put(DbAdapter.KEY_LOGS_TYPE ,l.type);
        values.put(DbAdapter.KEY_LOGS_PASSWORD ,l.password);
        values.put(DbAdapter.KEY_LOGS_RATING ,l.rating);
        values.put(DbAdapter.KEY_LOGS_RECOMMEND ,l.recommendation ? 1 : 0);
        values.put(DbAdapter.KEY_LOGS_MAINTENANCE ,l.needsMaintenance ? 1 : 0);
        values.put(DbAdapter.KEY_LOGS_ERROR ,l.errorMessage);
        return values;
    }

    public static Cursor list() {
        DbAdapter db = DbAdapter.open();
        Cursor c = db.fetch("select * from " + DbAdapter.DATABASE_TABLE_MY_LOGS);
        DbAdapter.close();
        return c;
    }

    public static void clean() {
        DbAdapter db = DbAdapter.open();
        db.clean(DbAdapter.DATABASE_TABLE_MY_LOGS, null);
        DbAdapter.close();
    }

    public static void delete(long id) {
        DbAdapter db = DbAdapter.open();
        db.clean(DbAdapter.DATABASE_TABLE_MY_LOGS, "_id=" + id);
        DbAdapter.close();
    }
}
