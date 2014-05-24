package com.zeyomir.ocfun.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.helper.ConnectionHelper;
import com.zeyomir.ocfun.dao.InternalResourceMapper;
import com.zeyomir.ocfun.dao.MyLogDAO;
import com.zeyomir.ocfun.gui.LogAdd;
import com.zeyomir.ocfun.gui.MyLogs;
import com.zeyomir.ocfun.model.MyLogbookEntry;
import org.holoeverywhere.app.Activity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
                    int visible = cursor.getInt(columnIndex);
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
    private static NotificationManager mManager;
    private static Notification notification;
    private static LogSyncronizer syncronizer;
    private final Context context;

    public ListMyLogs(Context context) {
        this.context = context;
    }

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

    public static void delete(long id) {
        MyLogDAO.delete(id);
    }

    public void synchronize() {
        List<MyLogbookEntry> entriesList = MyLogDAO.listForSyncronization();

        mManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification(R.drawable.notification_icon,
                "Synchronizacja logbooka", System.currentTimeMillis());
        RemoteViews contentView = new RemoteViews(context.getPackageName(),
                R.layout.progressbar_notification);
        contentView.setProgressBar(R.id.progressBar, 10, 0, false);
        contentView.setTextViewText(R.id.text, "Synchronizacja...");
        contentView.setTextViewText(R.id.text2, "nieznana ilość...");
        notification.contentView = contentView;

        Intent notificationIntent = new Intent(context, LogAdd.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        notification.contentIntent = contentIntent;
        notification.flags |= Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        mManager.notify(44, notification);
        Log.i("DEBUG", "run- before execute");
        syncronizer = new LogSyncronizer();
        syncronizer.execute(entriesList.toArray(new MyLogbookEntry[0]));
    }

    private class LogSyncronizer extends AsyncTask<MyLogbookEntry, Integer, Integer> {
        MyLogbookEntry[] entries;
        int max = 0;

        @Override
        protected Integer doInBackground(MyLogbookEntry... myLogbookEntries) {
            this.entries = myLogbookEntries;
            this.max = entries.length;
            int progress = 0;

            for (int i = 0; i < max; i++) {
                publishProgress(i, progress);
                MyLogbookEntry item = entries[i];

                String type;
                if (item.type == InternalResourceMapper.found.id())
                    type = "Found it";
                else if (item.type == InternalResourceMapper.notfound.id())
                    type = "Didn't find it";
                else
                    type = "Comment";

                StringBuilder link = new StringBuilder()
                        .append(ConnectionHelper.baseLink).append("/services/logs/submit")
                        .append("?cache_code=").append(item.cacheCode)
                        .append("&logtype=").append(ConnectionHelper.encode(type))
                        .append("&comment=").append(ConnectionHelper.encode(item.message))
                        .append("&comment_format=plaintext")
                        .append("&when=").append(item.date.replace(' ', 'T').replace('.', '-'))
                        .append("&langpref=pl");
                if (item.password != null && !item.password.isEmpty())
                    link.append("&password=" + ConnectionHelper.encode(item.password));
                if (item.rating != 0)
                    link.append("&rating=" + item.rating);
                if (item.recommendation)
                    link.append("&recommend=true");
                if (item.needsMaintenance)
                    link.append("&needs_maintenance=true");

                String answer = ConnectionHelper.get(link.toString(), context);
                if (answer == null)
                    continue;
                JSONObject jo = null;
                try {
                    jo = new JSONObject(answer);
                } catch (JSONException e1) {
                    Log.w("SyncLogs", "unable to parse response for log " + item.id);
                }
                if (jo == null)
                    continue;
                boolean success = false;
                String message = "";
                try {
                    success = jo.getBoolean("success");
                    message = jo.getString("message");
                } catch (JSONException e) {
                    Log.w("SyncLogs", "unable to read status for log " + item.id);
                }
                if (success) {
                    MyLogDAO.delete(item.id);
                    progress++;
                } else {
                    ContentValues contentValues = new ContentValues();
                    MyLogDAO.setErrorMessage(contentValues, message);
                    MyLogDAO.update(contentValues, item.id);
                }
            }

            return progress;
        }

        protected void onProgressUpdate(Integer... params) {
            notification.contentView.setProgressBar(R.id.progressBar, max,
                    params[0], false);
            notification.contentView.setTextViewText(R.id.text2, "Wysłano "
                    + params[1] + " z " + max);
            mManager.notify(44, notification);
            ((MyLogs) context).fillData();
        }

        protected void onPostExecute(Integer param) {
            mManager.cancel(44);
            notification.contentView = new RemoteViews(
                    context.getPackageName(), R.layout.simple_notification);
            notification.contentView.setTextViewText(R.id.text,
                    "Wysłano logów: " + param);
            Intent notificationIntent = new Intent(context, MyLogs.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);
            notification.contentIntent = contentIntent;
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mManager.notify(44, notification);
            ((MyLogs) context).fillData();
        }
    }
}
