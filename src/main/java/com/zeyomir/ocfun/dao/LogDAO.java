package com.zeyomir.ocfun.dao;


import android.content.ContentValues;
import android.database.Cursor;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.model.LogbookEntry;

public class LogDAO {

	public static final String[] from = {DbAdapter.KEY_LOGS_TYPE, DbAdapter.KEY_LOGS_WHO, DbAdapter.KEY_LOGS_DATE, DbAdapter.KEY_LOGS_BODY};
	public static final int[] to = {R.id.list_logs_type, R.id.list_logs_user, R.id.list_logs_date, R.id.list_logs_body};

	public static final String idColumn = DbAdapter.KEY_LOGS_ID;
	public static final String cacheIdColumn = DbAdapter.KEY_LOGS_CACHE_ID;

	public static void save(LogbookEntry l) {
		DbAdapter db = DbAdapter.open();
		db.insert(map(l), DbAdapter.DATABASE_TABLE_LAST_LOGS);
		DbAdapter.close();
	}

	private static ContentValues map(LogbookEntry l) {
		ContentValues values = new ContentValues();
		values.put(DbAdapter.KEY_LOGS_CACHE_ID, l.cacheId);
		values.put(DbAdapter.KEY_LOGS_TYPE, l.type);
		values.put(DbAdapter.KEY_LOGS_WHO, l.user);
		values.put(DbAdapter.KEY_LOGS_DATE, l.date);
		values.put(DbAdapter.KEY_LOGS_BODY, l.message);
		return values;
	}

	public static Cursor list(long id) {
		DbAdapter db = DbAdapter.open();
		Cursor c = db.fetch("select * from " + DbAdapter.DATABASE_TABLE_LAST_LOGS + " where " + DbAdapter.KEY_LOGS_CACHE_ID + " = " + id);
		DbAdapter.close();
		return c;
	}

	public static void clean() {
		DbAdapter db = DbAdapter.open();
		db.clean(DbAdapter.DATABASE_TABLE_IMAGES, null);
		DbAdapter.close();
	}

	public static void delete(long id) {
		DbAdapter db = DbAdapter.open();
		db.clean(DbAdapter.DATABASE_TABLE_IMAGES, " where id=" + id);
		DbAdapter.close();
	}
}
