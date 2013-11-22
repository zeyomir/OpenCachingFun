package com.zeyomir.ocfun.gui;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.zeyomir.ocfun.LocationProvider;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.dao.DbAdapter;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("application.properties");

        textView.setText("");
        try {
            prop.load(stream);
            textView.setText("wersja " + prop.getProperty("app.versionName"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
