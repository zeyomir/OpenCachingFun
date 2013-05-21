package com.zeyomir.ocfun.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.model.Image;

public class ImageDAO {

	public static final String[] from = {DbAdapter.KEY_IMAGES_TITLE};
	public static final int[] to = {R.id.img_title};

	public static final String idColumn = DbAdapter.KEY_IMAGES_ID;
	public static final String cacheIdColumn = DbAdapter.KEY_IMAGES_CACHE_ID;

	public static void save(Image i) {
		DbAdapter db = DbAdapter.open();
		db.insert(map(i), DbAdapter.DATABASE_TABLE_IMAGES);
		DbAdapter.close();
	}

	private static ContentValues map(Image i) {
		ContentValues values = new ContentValues();
		values.put(DbAdapter.KEY_IMAGES_CACHE_ID, i.cacheId);
		values.put(DbAdapter.KEY_IMAGES_SRC, i.path);
		values.put(DbAdapter.KEY_IMAGES_TITLE, i.name);
		return values;
	}


	private static Image map(Cursor c) {
		long id = c.getLong(c.getColumnIndex(DbAdapter.KEY_IMAGES_ID));
		long cacheId = c.getLong(c.getColumnIndex(DbAdapter.KEY_IMAGES_CACHE_ID));
		String name = c.getString(c.getColumnIndex(DbAdapter.KEY_IMAGES_TITLE));
		String path = c.getString(c.getColumnIndex(DbAdapter.KEY_IMAGES_SRC));
		return new Image(id, cacheId, name, path);
	}

	public static Cursor list(long id) {
		DbAdapter db = DbAdapter.open();
		Cursor c = db.fetch("select * from " + DbAdapter.DATABASE_TABLE_IMAGES + " where " + DbAdapter.KEY_IMAGES_CACHE_ID + " = " + id);
		DbAdapter.close();
		return c;
	}

	public static Image get(long id) {
		DbAdapter db = DbAdapter.open();
		Cursor c = db.fetch("select * from " + DbAdapter.DATABASE_TABLE_IMAGES
				+ " where " + DbAdapter.KEY_IMAGES_ID + "=" + id);
		DbAdapter.close();
		if (c.getCount() > 0)
			return map(c);
		return null;
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
