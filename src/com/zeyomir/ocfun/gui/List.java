package com.zeyomir.ocfun.gui;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.zeyomir.ocfun.LocationProvider;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.ListCaches;

public class List extends ListActivity {

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
		actionBar.setTitle(R.string.list);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ListCaches.openDB();
		fillData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		sca.getCursor().close();
		ListCaches.closeDB();
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		startActivity(ListCaches.createIntent(this, id));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list, menu);
		/*MenuItem mi = menu.findItem(R.id.menu_search);
		EditText et =(EditText)mi.getActionView(); 
		et.setHint("Szukaj");
		et.setText("");*/
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, OCFun.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
/*		case R.id.menu_search:
			item.expandActionView();
			((EditText)item.getActionView()).setWidth(LayoutParams.MATCH_PARENT);
			return true;*/
		case R.id.menu_refresh:
			refresh();
			return true;
		case R.id.menu_delete:
			ListCaches.clean();
			fillData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void fillData(){
		Location l = ((LocationProvider)getApplication()).getLast();
		sca = ListCaches.createAdapter(this, l);
		setListAdapter(sca);
	}
	
	private void refresh(){
		sca.notifyDataSetInvalidated();
		fillData();
	}
}
