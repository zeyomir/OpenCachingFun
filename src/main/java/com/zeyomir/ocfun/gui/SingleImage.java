package com.zeyomir.ocfun.gui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.DisplayImage;
import com.zeyomir.ocfun.controller.helper.ConnectionHelper;
import com.zeyomir.ocfun.model.Image;
import org.holoeverywhere.app.Activity;

import java.io.File;

public class SingleImage extends Activity {
	private Image image;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image);

		image = DisplayImage.getImage(getIntent());
		actionBar = getSupportActionBar();
		setActionBarOptions();
		fillData();
	}

	private void setActionBarOptions() {
		actionBar.hide();
	}

	private void fillData() {
        String path = getAbsoluteImagePath();
        Log.i("Image", path);
        if (new File(path).exists())
            displayImage(path);
        else {
            download(image.path);
        }
    }

    private String getAbsoluteImagePath() {
        return Environment.getExternalStorageDirectory().toString()
                    + "/OCFun/" + image.path;
    }

    private void download(String path) {
        View imageView = findViewById(R.id.imageView1);
        imageView.setVisibility(View.GONE);

        View textView = findViewById(R.id.textView1);
        View progressBarView = findViewById(R.id.progressBar1);

        boolean internetAvailable = ConnectionHelper.isInternetAvailable(this);
        if (!internetAvailable){
            textView.setVisibility(View.VISIBLE);
            progressBarView.setVisibility(View.GONE);
            return;
        }
        textView.setVisibility(View.GONE);
        progressBarView.setVisibility(View.VISIBLE);

        OnDemandImagesDownloader onDemandImagesDownloader = new OnDemandImagesDownloader();
        onDemandImagesDownloader.execute(path);
    }

    private void displayImage(String path) {
        findViewById(R.id.textView1).setVisibility(View.GONE);
        findViewById(R.id.progressBar1).setVisibility(View.GONE);
        findViewById(R.id.imageView1).setVisibility(View.VISIBLE);

        WebView wv = ((WebView) findViewById(R.id.imageView1));
        wv.setBackgroundColor(Color.BLACK);
        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.loadUrl("file:///" + path);
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

    private class OnDemandImagesDownloader extends AsyncTask<String, Integer, Integer>{

        @Override
        protected Integer doInBackground(String... strings) {
            String filePath = strings[0];
            String url = "http://opencaching.pl/uploads/" + filePath.split("/")[1];
            ConnectionHelper.download(url, filePath);
            return null;
        }

        @Override
        protected void onPostExecute(Integer param){
            displayImage(getAbsoluteImagePath());
        }
    }
}