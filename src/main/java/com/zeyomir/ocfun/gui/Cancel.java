package com.zeyomir.ocfun.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.AddCaches;

public class Cancel extends Activity {

	private ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cancel);

		actionBar = getActionBar();
		setActionBarOptions();		
	}
	private void setActionBarOptions() {
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Pobieranie");
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}
	
	public void cancel(View v){
		AddCaches.cancel();
		Intent i = new Intent(this, Add.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
}
