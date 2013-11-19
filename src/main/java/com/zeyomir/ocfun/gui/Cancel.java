package com.zeyomir.ocfun.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.AddCaches;
import org.holoeverywhere.app.Activity;

public class Cancel extends Activity {

	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cancel);

		actionBar = getSupportActionBar();
		setActionBarOptions();
	}

	private void setActionBarOptions() {
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Pobieranie");
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	public void cancel(View v) {
		AddCaches.cancel();
		Intent i = new Intent(this, Add.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

    private void getBackHome() {
        Intent intent = new Intent(this, OCFun.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        getBackHome();
        finish();
    }
}
