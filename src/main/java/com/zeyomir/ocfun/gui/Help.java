package com.zeyomir.ocfun.gui;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.zeyomir.ocfun.R;
import org.holoeverywhere.app.Activity;

public class Help extends Activity {
	private ActionBar actionBar;
	private boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		actionBar = getSupportActionBar();
		setActionBarOptions();
		fillData();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, OCFun.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
		}
		return false;
	}

	private void setActionBarOptions() {
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.help_title);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	private void fillData(){
		WebView view = (WebView) findViewById(R.id.helpView);
		view.loadUrl("file:///android_asset/help.html");
		view.setVerticalScrollBarEnabled(false);

		view.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView view, String url) {
				if (url.contains("#") && flag == false) {
					view.loadUrl(url);
					flag = true;
				} else {
					flag = false;
				}
			}
		});
	}
}
