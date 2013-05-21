package com.zeyomir.ocfun.model;

import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.dao.InternalResourceMapper;
import org.json.JSONException;
import org.json.JSONObject;

public class Cache {
	public final long id;
	public final String code;
	public final String name;
	public final String coords;
	public final int type;
	public final String owner;
	public final double size;
	public final double difficulty;
	public final double terrain;
	public final boolean requiresPassword;
	public final String description;
	public final String attributes;
	public final String hint;
	public final String lastFoundOn;
	public final boolean isFound;

	private final String url = "opencaching.pl/viewcache.php?wp=";

	public Cache(JSONObject data) throws JSONException {
		this.id = 0;
		this.code = data.getString("code");
		this.name = data.getString("name");
		this.coords = data.getString("location");
		this.type = getTypeImageResource(data.getString("type"));
		this.owner = data.getJSONObject("owner").getString("username");
		this.size = data.isNull("size") ? 0 : data.getDouble("size");
		this.difficulty = data.getDouble("difficulty");
		this.terrain = data.getDouble("terrain");
		this.requiresPassword = data.getBoolean("req_passwd");
		this.description = data.getString("description");
		this.attributes = data.isNull("attrnames") ? "bez atrybutów" : data.getJSONArray(
				"attrnames").join(", ");
		this.hint = data.getString("hint");
		String lastFound = data.getString("last_found").split("T")[0].replace(
				'-', '.');
		if (lastFound.contains("1970.01.01") || lastFound.equals("null"))
			lastFound = "nigdy";
		this.lastFoundOn = lastFound;
		this.isFound = data.isNull("is_found") ? false : data
				.getBoolean("is_found");
	}

	public Cache(long id, String code, String name, String coords, int type, String owner, double size,
	             double difficulity, double terrain, boolean requiresPassword,
	             String description, String attributes, String hint,
	             String lastFoundOn, boolean isFound) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.coords = coords;
		this.type = type;
		this.owner = owner;
		this.size = size;
		this.difficulty = difficulity;
		this.terrain = terrain;
		this.requiresPassword = requiresPassword;
		this.description = description;
		this.attributes = attributes;
		this.hint = hint;
		this.lastFoundOn = lastFoundOn;
		this.isFound = isFound;
	}

	public String getUrl(boolean mobile) {
		if (mobile)
			return "http://m." + url + code;
		else
			return "http://" + url + code;
	}

	private int getTypeImageResource(String s) {
		s = s.toLowerCase();

		if (s.equals("traditional"))
			return InternalResourceMapper.traditional.id();
		else if (s.equals("multi"))
			return InternalResourceMapper.multi.id();
		else if (s.equals("quiz"))
			return InternalResourceMapper.quiz.id();
		else if (s.equals("moving"))
			return InternalResourceMapper.moving.id();
		else if (s.equals("owncache"))
			return InternalResourceMapper.owncache.id();
		else if (s.equals("event"))
			return InternalResourceMapper.event.id();
		else if (s.equals("virtual"))
			return InternalResourceMapper.virtual.id();
		else if (s.equals("webcam"))
			return InternalResourceMapper.webcam.id();
		else
			return InternalResourceMapper.unknown.id();
	}

	public static int getSizeResource(double size) {
		if (size >= 5)
			return R.string.size_vbig;
		else if (size >= 4)
			return R.string.size_big;
		else if (size >= 3)
			return R.string.size_med;
		else if (size >= 2)
			return R.string.size_small;
		else if (size >= 1)
			return R.string.size_vsmall;
		else
			return R.string.size_none;
	}
}
