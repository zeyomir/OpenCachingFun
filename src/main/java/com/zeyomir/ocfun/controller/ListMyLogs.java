package com.zeyomir.ocfun.controller;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.dao.InternalResourceMapper;
import com.zeyomir.ocfun.dao.MyLogDAO;
import org.holoeverywhere.app.Activity;

public class ListMyLogs {
	static private final SimpleCursorAdapter.ViewBinder myLogsBinder = new SimpleCursorAdapter.ViewBinder() {
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			switch (view.getId()) {
				case R.id.list_logs_type:
					int i = cursor.getInt(columnIndex);
					((ImageView) view).setImageResource(InternalResourceMapper
							.map(i));
					return true;
                case R.id.list_logs_recommend:
                case R.id.list_logs_maintenance:
                    view.setVisibility(View.VISIBLE);
                    int visible = Integer.parseInt(cursor.getString(columnIndex));
                    if (visible != 1) {
                            view.setVisibility(View.GONE);
                    }
                    return true;
                case R.id.list_logs_rating1:
                case R.id.list_logs_rating2:
                case R.id.list_logs_rating3:
                case R.id.list_logs_rating4:
                case R.id.list_logs_rating5:
                    handleRating(view, cursor, columnIndex);
                    return true;
                case R.id.list_logs_pass:
                case R.id.list_logs_error:
                    view.setVisibility(View.VISIBLE);
                    String s = cursor.getString(columnIndex);
                    if (s == null || s.length() == 0) {
                        view.setVisibility(View.GONE);
                    }
                    return false;
				default:
					return false;
			}
		}

        private void handleRating(View view, Cursor cursor, int columnIndex) {
            view.setVisibility(View.VISIBLE);

            int rating = cursor.getInt(columnIndex);
            int id = view.getId();
            if (id == R.id.list_logs_rating5 && rating < 5
             || id == R.id.list_logs_rating4 && rating < 4
             || id == R.id.list_logs_rating3 && rating < 3
             || id == R.id.list_logs_rating2 && rating < 2
             || id == R.id.list_logs_rating1 && rating < 1)
                view.setVisibility(View.GONE);
        }
    };

	public static SimpleCursorAdapter createAdapter(Activity c) {
		Cursor cursor = MyLogDAO.list();
		SimpleCursorAdapter la = new SimpleCursorAdapter(c,
				R.layout.my_logs_row, cursor, MyLogDAO.from, MyLogDAO.to);
		la.setViewBinder(myLogsBinder);
		return la;
	}

	public static void clean() {
		MyLogDAO.clean();
	}

    public static void delete(long id){
        MyLogDAO.delete(id);
    }
}
