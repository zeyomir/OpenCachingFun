package com.zeyomir.ocfun.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

import com.zeyomir.ocfun.dao.InternalResourceMapper;

public class LogbookEntry {
	public final long id;
	public final long cacheId;
	public final String user;
	public final String date;
	public final String message;
	public final int type;
	
	public LogbookEntry(long id, long cacheId, String user, String date, String message,
			int type) {
		super();
		this.id = id;
		this.cacheId = cacheId;
		this.user = user;
		this.date = date;
		this.message = message;
		this.type = type;
	}
	
	public LogbookEntry(long cacheId, JSONObject data) throws JSONException {
		this.id = 0;
		this.cacheId = cacheId;
		this.user = data.getJSONObject("user").getString("username");
		this.date = data.getString("date").split("T")[0].replace('-', '.');
		this.type = getTypeImageResource(data.getString("type"));
		this.message = Html.fromHtml(data.getString("comment")).toString();
	}
	
	private int getTypeImageResource(String s) {
		s = s.toLowerCase();

		if (s.equals("found it"))
			return InternalResourceMapper.found.id();
		else if (s.equals("didn't find it"))
			return InternalResourceMapper.notfound.id();
		else
			return InternalResourceMapper.comment.id();
	}
}
