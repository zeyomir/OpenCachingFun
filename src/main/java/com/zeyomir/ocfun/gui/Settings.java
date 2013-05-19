package com.zeyomir.ocfun.gui;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.zeyomir.ocfun.LocationProvider;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.helper.OAuthWrapper;
import com.zeyomir.ocfun.dao.PreferencesDAO;

public class Settings extends PreferenceActivity implements
		OnPreferenceClickListener {
	private PreferencesDAO prefDAO;
	private OAuthWrapper oauthWrapper;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();
		setActionBarOptions();
		addPreferencesFromResource(R.xml.settings);
		prefDAO = new PreferencesDAO(this);
		oauthWrapper = OAuthWrapper.get(this);
		adjustView();
	}

	@Override
	protected void onResume(){
		super.onResume();
		oauthWrapper.authenticate(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent i) {
		super.onNewIntent(i);
		Log.i("OAuth", "przyszedl NOWY intent");
		oauthWrapper.authenticate(i);
	}

	private void setActionBarOptions() {
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.settings);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	private void getBackHome() {
		Intent intent = new Intent(this, OCFun.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getBackHome();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void adjustView() {
		Preference auth = (Preference) findPreference("auth");
		CheckBoxPreference own = (CheckBoxPreference) findPreference("own");
		if (prefDAO.isAuthenticated()) {
			own.setEnabled(true);
			auth.setTitle(R.string.auth_undo);
			auth.setSummary(R.string.auth_undo);
		} else {
			own.setEnabled(false);
			auth.setTitle(R.string.auth_do);
			auth.setSummary(R.string.auth_do_summary);
		}
		auth.setOnPreferenceClickListener(this);
		((Preference) findPreference("feedback"))
				.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference p) {
		if (p.getKey().equals("auth")) {
			if (!prefDAO.isAuthenticated()) {
				oauthWrapper.startAuthentication();
			} else {
				prefDAO.unsetAuthenticated();
			}
		} else if (p.getKey().equals("feedback")) {
			Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "zeyomir@gmail.com", null));
			i.putExtra(Intent.EXTRA_SUBJECT, "ocFun - feedback");
			startActivity(Intent.createChooser(i,"Wyślij feedback"));
		}
		adjustView();
		return false;
	}

	@Override
	public void onBackPressed() {
		getBackHome();
		finish();
	}

	public void authenticated(String token, String secret) {
		prefDAO.setAuthenticated(token, secret);
		adjustView();
	}
}
