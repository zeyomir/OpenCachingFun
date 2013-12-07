package com.zeyomir.ocfun.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {
	static final String KEY_CACHES_ID = "_id";
	static final String KEY_CACHES_CODE = "code";
	static final String KEY_CACHES_NAME = "name";
	static final String KEY_CACHES_COORDS = "coords";
	static final String KEY_CACHES_TYPE = "type";
	static final String KEY_CACHES_OWNER = "owner";
	static final String KEY_CACHES_SIZE = "size";
	static final String KEY_CACHES_DIFFICULTY = "difficulty";
	static final String KEY_CACHES_TERRAIN = "terrain";
	static final String KEY_CACHES_REQUIRE_PASSWORD = "reqPass";
	static final String KEY_CACHES_DESCRIPTION = "description";
    static final String KEY_CACHES_NOTE = "my_notes";
	static final String KEY_CACHES_ATTRIBUTES = "attributes";
	static final String KEY_CACHES_HINT = "hint";
	static final String KEY_CACHES_LAST_FOUND = "lastFound";
	static final String KEY_CACHES_IS_FOUND = "isFound";

	static final String KEY_LOGS_ID = "_id";
	static final String KEY_LOGS_DATE = "date";
	static final String KEY_LOGS_WHO = "user";
	static final String KEY_LOGS_TYPE = "type";
    static final String KEY_LOGS_BODY = "body";
    static final String KEY_LOGS_PASSWORD = "pass";
    static final String KEY_LOGS_RATING = "rating";
    static final String KEY_LOGS_RECOMMEND = "recommend";
    static final String KEY_LOGS_MAINTENANCE = "maintenance";
    static final String KEY_LOGS_ERROR = "error";
	static final String KEY_LOGS_CACHE_ID = "cacheId";

	static final String KEY_IMAGES_ID = "_id";
	static final String KEY_IMAGES_CACHE_ID = "cacheId";
	static final String KEY_IMAGES_TITLE = "title";
	static final String KEY_IMAGES_SRC = "source";

	private static final String DATABASE_NAME = "data";
	static final String DATABASE_TABLE_CACHES = "caches";
    static final String DATABASE_TABLE_LAST_LOGS = "lastLogs";
    static final String DATABASE_TABLE_MY_LOGS = "myLogs";
	static final String DATABASE_TABLE_IMAGES = "images";
	private static DbAdapter instance;

	private final Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;

	public static void initializeDbAdapter(Context c) {
		instance = new DbAdapter(c);
	}

	private DbAdapter(Context c) {
		this.context = c;
	}

	static DbAdapter open() throws SQLException {
		Log.d("DB", "opening...");
		instance.dbHelper = new DatabaseHelper(instance.context);
		instance.database = instance.dbHelper.getWritableDatabase();
		return instance;
	}

	static void close() {
		Log.d("DB", "closing...");
        DatabaseHelper databaseHelper = instance.dbHelper;
        if (databaseHelper != null)
            databaseHelper.close();
	}

	long insert(ContentValues what, String where) {
		Log.d("DB", "inserting " + what + " into " + where);
		return database.insert(where, null, what);
	}

	long update(ContentValues what, String where, String id) {
		Log.d("DB", "updating...");
		return database.update(where, what, "_id=" + id, null);
	}

	Cursor fetch(String query) {
		Log.d("DB", "fetching...");
		Cursor mCursor = database.rawQuery(query, new String[]{});
		mCursor.moveToFirst();
		return mCursor;
	}

	void clean(String table, String condition) {
		Log.d("DB", "cleaning...");
		database.delete(table, condition, null);
	}

	int count(String table, String condition){
		Cursor c = database.rawQuery("select count(*) from " + table + " where " + condition, null);
		c.moveToFirst();
		int ret =  c.getInt(0);
		c.close();
		return ret;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 5;

		private static final String CREATE_TABLE_IMAGES = "create table if not exists "
				+ DATABASE_TABLE_IMAGES + "(" + KEY_IMAGES_ID
				+ " integer primary key autoincrement, " + KEY_IMAGES_CACHE_ID
				+ " integer not null, " + KEY_IMAGES_TITLE + " text, "
				+ KEY_IMAGES_SRC + " text not null); ";

        private static final String CREATE_TABLE_CACHES = "create table if not exists "
                + DATABASE_TABLE_CACHES + "(" + KEY_CACHES_ID
                + " integer primary key autoincrement, " + KEY_CACHES_CODE
                + " text not null, " + KEY_CACHES_NAME + " text not null, "
                + KEY_CACHES_COORDS + " text not null, " + KEY_CACHES_TYPE
                + " integer not null, " + KEY_CACHES_OWNER + " text not null, "
                + KEY_CACHES_SIZE + " real, " + KEY_CACHES_DIFFICULTY
                + " real not null, " + KEY_CACHES_TERRAIN + " real not null, "
                + KEY_CACHES_REQUIRE_PASSWORD + " boolean not null, "
                + KEY_CACHES_DESCRIPTION + " text, " + KEY_CACHES_NOTE + " text, "
                + KEY_CACHES_ATTRIBUTES + " text, " + KEY_CACHES_HINT + " text, "
                + KEY_CACHES_LAST_FOUND + " text, " + KEY_CACHES_IS_FOUND
                + " boolean); ";

        private static final String CREATE_TABLE_LAST_LOGS = "create table if not exists "
				+ DATABASE_TABLE_LAST_LOGS + "(" + KEY_LOGS_ID
				+ " integer primary key autoincrement, " + KEY_LOGS_CACHE_ID
				+ " integer not null, " + KEY_LOGS_DATE + " text not null, "
				+ KEY_LOGS_TYPE + " integer not null, " + KEY_LOGS_WHO
				+ " text not null, " + KEY_LOGS_BODY + " text not null); ";

        private static final String CREATE_TABLE_MY_LOGS = "create table if not exists "
                + DATABASE_TABLE_MY_LOGS + "(" + KEY_LOGS_ID
                + " integer primary key autoincrement, " + KEY_LOGS_CACHE_ID
                + " integer not null, " + KEY_LOGS_DATE + " text not null, "
                + KEY_LOGS_TYPE + " integer not null, " + KEY_LOGS_WHO
                + " text not null, " + KEY_LOGS_BODY + " text not null, "
                + KEY_LOGS_PASSWORD + " text, " + KEY_LOGS_RATING
                + " integer, " + KEY_LOGS_RECOMMEND + " boolean not null, "
                + KEY_LOGS_MAINTENANCE + " boolean not null, " + KEY_LOGS_ERROR
                + " text); ";

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_CACHES);
			db.execSQL(CREATE_TABLE_IMAGES);
			db.execSQL(CREATE_TABLE_LAST_LOGS);
            db.execSQL(CREATE_TABLE_MY_LOGS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 4) {
                db.execSQL("alter table " + DbAdapter.DATABASE_TABLE_CACHES +
                        " add " + DbAdapter.KEY_CACHES_NOTE + " text;");
            }
            if (oldVersion < 5) {
                db.execSQL(CREATE_TABLE_MY_LOGS);
            }
		}
	}
}
