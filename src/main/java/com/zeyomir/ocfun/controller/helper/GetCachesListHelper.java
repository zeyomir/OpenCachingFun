package com.zeyomir.ocfun.controller.helper;

import android.content.Context;
import android.os.AsyncTask;
import com.zeyomir.ocfun.controller.AddCaches;
import com.zeyomir.ocfun.dao.PreferencesDAO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetCachesListHelper {
	private final AddCaches notify;
	private final PreferencesDAO p;
	private final Context context;

	public GetCachesListHelper(AddCaches notify, Context c) {
		this.notify = notify;
		this.context = c;
		this.p = new PreferencesDAO(context);
	}

	public void getByName(String name) {
		new GetListByName().execute(name);
	}

	public void getByCache(String cacheCode, String distance) {
		new GetListByCache().execute(cacheCode, distance);
	}

	public void getByLocation(String location, String distance) {
		new GetListByLocation().execute(location, distance);
	}

    public void getWatched() {
        new GetListWatched().execute();
    }

	private void go(String list) {
		list = list.replaceAll("\"", "");
		notify.download(list);
	}

	private String addOptionsToLink(String link) {
		if (p.isAuthenticated() && p.getSkipOwn())
			link += "&found_status=notfound_only&exclude_my_own=true";
		return link;
	}

    private String handleSimpleJSONAnswer(String link) {
        String jsonAnswer = ConnectionHelper.get(addOptionsToLink(link), context);

        try {
            JSONObject jo = new JSONObject(jsonAnswer);
            JSONArray ja = jo.getJSONArray("results");
            return ja.join(",");
        } catch (JSONException je) {
            return null;
        } catch (NullPointerException npe) {
            return null;
        }
    }

    private class GetListByName extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String link = ConnectionHelper.baseLink
					+ "/services/caches/search/all?name=" + params[0] + "&limit=500";
            return handleSimpleJSONAnswer(link);
		}
        @Override
		protected void onPostExecute(String param) {
			if (param != null) {
				go(param);
			}
		}
	}

	private class GetListByLocation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String link = ConnectionHelper.baseLink
					+ "/services/caches/search/nearest?center=" + ConnectionHelper.encode(params[0])
					+ "&radius=" + params[1] + "&limit=500";
			return handleSimpleJSONAnswer(link);
		}

        @Override
		protected void onPostExecute(String param) {
			if (param != null) {
				go(param);
			}
		}
	}

    private class GetListByCache extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... params) {
			String link = ConnectionHelper.baseLink
					+ "/services/caches/geocache?fields=location&cache_code="
					+ params[0].toUpperCase();
			String answer = ConnectionHelper.get(addOptionsToLink(link), context);
			if (answer == null)
				return null;
			JSONObject jo = null;
			try {
				jo = new JSONObject(answer);
			} catch (JSONException e1) {
			}
			if (jo == null)
				return null;
			try {
				return new String[]{jo.getString("location"), params[1]};
			} catch (JSONException e) {
				return null;
			}
		}

        @Override
		protected void onPostExecute(String[] param) {
			new GetListByLocation().execute(param);
		}

	}

    private class GetListWatched extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String link = ConnectionHelper.baseLink
                    + "/services/caches/search/all?watched_only=true&limit=500";
            return handleSimpleJSONAnswer(link);
        }

        @Override
        protected void onPostExecute(String param) {
            if (param != null) {
                go(param);
            }
        }
    }
}
