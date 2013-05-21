package com.zeyomir.ocfun.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.ListImages;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;

public class Images extends ListActivity {
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
		actionBar.setTitle(R.string.images);
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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		startActivity(ListImages.createIntent(this, id));
	}

	private void fillData() {
		sca = ListImages.createAdapter(this);
		setListAdapter(sca);
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
		return super.onOptionsItemSelected(item);
	}
}
