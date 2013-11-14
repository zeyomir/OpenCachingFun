package com.zeyomir.ocfun.dao;

import android.content.Context;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

public class PreferencesDAO {
	final private Context c;
	final private SharedPreferences sp;

	public PreferencesDAO(Context c) {
		this.c = c;
		this.sp = PreferenceManager.getDefaultSharedPreferences(c);
	}

	public boolean isAuthenticated() {
		return sp.getBoolean("authenticated", false);
	}

	public void setAuthenticated(String token, String secret) {
		SharedPreferences.Editor e = sp.edit();
		e.putBoolean("authenticated", true);
		e.putString("token", token);
		e.putString("secret", secret);
		e.commit();
	}

	public void unsetAuthenticated() {
		SharedPreferences.Editor e = sp.edit();
		e.putBoolean("authenticated", false);
		e.putString("token", "");
		e.putString("secret", "");
		e.commit();
	}

	public String getToken() {
		return sp.getString("token", "");
	}

	public String getSecret() {
		return sp.getString("secret", "");
	}

	public boolean getSkipOwn() {
		return sp.getBoolean("own", false);
	}

	public boolean getMobile() {
		return sp.getBoolean("mobile", false);
	}

	public int getLimit() {
		return sp.getBoolean("limit", false) ? Integer.parseInt(sp.getString("limit_value", "100")) : 500;
	}

    public boolean getSateliteView() {
        return sp.getBoolean("satelite_map", false);
    }

}
