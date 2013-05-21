package com.zeyomir.ocfun.controller;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.dao.ImageDAO;
import com.zeyomir.ocfun.dao.InternalResourceMapper;
import com.zeyomir.ocfun.dao.LogDAO;
import com.zeyomir.ocfun.gui.SingleImage;
import org.holoeverywhere.app.Activity;

public class ListLogs {
	static private final SimpleCursorAdapter.ViewBinder imagesBinder = new SimpleCursorAdapter.ViewBinder() {
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			switch (view.getId()) {
				case R.id.list_logs_type:
					int i = cursor.getInt(columnIndex);
					((ImageView) view).setImageResource(InternalResourceMapper
							.map(i));
					return true;
				default:
					return false;
			}
		}
	};

	public static SimpleCursorAdapter createAdapter(Activity c) {
		long id = c.getIntent().getExtras().getLong(LogDAO.cacheIdColumn);
		Cursor cursor = LogDAO.list(id);
		SimpleCursorAdapter la = new SimpleCursorAdapter(c,
				R.layout.logs_row, cursor, LogDAO.from, LogDAO.to);
		la.setViewBinder(imagesBinder);
		return la;
	}

	public static Intent createIntent(Context c, long id) {//TODO: niby nie potrzebne, ale przyda sie przy robieniu 'dodaj wpis'
		Intent i = new Intent(c, SingleImage.class);
		i.putExtra(ImageDAO.idColumn, id);
		return i;
	}

	public static void clean() {
		LogDAO.clean();
	}
}
