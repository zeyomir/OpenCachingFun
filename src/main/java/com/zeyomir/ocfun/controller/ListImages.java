package com.zeyomir.ocfun.controller;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.dao.CacheDAO;
import com.zeyomir.ocfun.dao.ImageDAO;
import com.zeyomir.ocfun.gui.SingleImage;
import com.zeyomir.ocfun.model.Cache;
import org.holoeverywhere.app.Activity;

import java.io.File;

public class ListImages {

	public static SimpleCursorAdapter createAdapter(Activity c) {
		long id = c.getIntent().getExtras().getLong(ImageDAO.cacheIdColumn);
		Cursor cursor = ImageDAO.list(id);
		SimpleCursorAdapter la = new SimpleCursorAdapter(c,
				R.layout.images_row, cursor, ImageDAO.from, ImageDAO.to);
		return la;
	}

	public static Intent createIntent(Context c, long id) {
		Intent i = new Intent(c, SingleImage.class);
		i.putExtra(ImageDAO.idColumn, id);
		return i;
	}

	public static void clean() {
		ImageDAO.clean();
		new Thread(new Runnable() {

			private void delete(File f) {
				if (f.isDirectory()) {
					for (File file : f.listFiles()) {
						delete(file);
					}
				}
				f.delete();
			}

			@Override
			public void run() {
				File dir = new File(Environment.getExternalStorageDirectory().toString() + "/OCFun");
				delete(dir);
			}
		}).start();
	}

    public static void deleteForCacheId(long id){
        final Cache cache = CacheDAO.get(id);
        new Thread(new Runnable() {

            private void delete(File f) {
                if (f.isDirectory()) {
                    for (File file : f.listFiles()) {
                        delete(file);
                    }
                }
                f.delete();
            }

            @Override
            public void run() {
                File dir = new File(Environment.getExternalStorageDirectory().toString() + "/OCFun/" + cache.code);
                delete(dir);
            }
        }).start();

        ImageDAO.deleteForCache(id);
    }
}
