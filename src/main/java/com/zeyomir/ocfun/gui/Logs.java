package com.zeyomir.ocfun.gui;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.ListLogs;

public class Logs extends ListActivity {
	private ActionBar actionBar;
	private SimpleCursorAdapter sca;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();
		setActionBarOptions();
		setContentView(R.layout.list);
	}

	private void setActionBarOptions() {
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.logs);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		sca.getCursor().close();
	}
	
	private void fillData(){
		sca = ListLogs.createAdapter(this);
		setListAdapter(sca);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.logs, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(this, OCFun.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
		/*case R.id.add_log:
			//i = new Intent(this, OCFun.class);
			//startActivity(i);
			return true;*/
		}
		return super.onOptionsItemSelected(item);
	}
}
