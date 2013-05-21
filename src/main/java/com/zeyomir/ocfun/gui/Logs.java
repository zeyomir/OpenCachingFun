package com.zeyomir.ocfun.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.ListLogs;
import org.holoeverywhere.app.ListActivity;

public class Logs extends ListActivity {
	private ActionBar actionBar;
	private SimpleCursorAdapter sca;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getSupportActionBar();
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

	private void fillData() {
		sca = ListLogs.createAdapter(this);
		setListAdapter(sca);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
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
