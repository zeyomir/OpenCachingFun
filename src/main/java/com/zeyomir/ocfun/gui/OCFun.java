package com.zeyomir.ocfun.gui;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.zeyomir.ocfun.LocationProvider;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.helper.PropertiesLoader;
import com.zeyomir.ocfun.dao.DbAdapter;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

public class OCFun extends Activity {

	LocationProvider globalContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		globalContext = (LocationProvider) getApplicationContext();

		setContentView(R.layout.main);
		DbAdapter.initializeDbAdapter(this);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.app_name);

        TextView textView = (TextView) findViewById(R.id.version);
        textView.setText(PropertiesLoader.get().applicationVersionName);
	}

	public void onClick(View v) {
		Intent i = null;
		switch (v.getId()) {
			case R.id.button1:
				i = new Intent(this, Add.class);
				break;
			case R.id.button2:
				i = new Intent(this, List.class);
				break;
			case R.id.button3:
				i = new Intent(this, Maps.class);
				Location l = ((LocationProvider) getApplicationContext()).getLast();
				if (l == null) {
					Log.i("MAP", "location is null");
					i.putExtra("lat", "");
					break;
				}
				Log.i("MAP", "sent location: " + l.getLatitude() + ", " + l.getLongitude());
				i.putExtra("lat", l.getLatitude() + "");
				i.putExtra("lon", l.getLongitude() + "");
				break;
            case R.id.button4:
                i = new Intent(this, MyLogs.class);
                break;
			case R.id.button5:
				i = new Intent(this, Settings.class);
				break;
			case R.id.button6:
				i = new Intent(this, Help.class);
				break;
		}
		if (i != null)
			startActivity(i);
		else

			Toast.makeText(this, getString(R.string.not_implemented),
					Toast.LENGTH_SHORT).show();
	}
}
