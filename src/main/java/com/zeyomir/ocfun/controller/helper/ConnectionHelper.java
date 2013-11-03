package com.zeyomir.ocfun.controller.helper;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ConnectionHelper {

	public static final String baseLink = "http://opencaching.pl/okapi";

	public static String get(String url, Context c) {

		String msg = "";
		try {
			url = OAuthWrapper.get(c).sign(url);
			Log.i("OAuth", "Signed url: " + url);

			HttpClient client = new DefaultHttpClient();

			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			Log.i("MOJE INFO", "Status code: " + statusCode);
			if (statusCode == 500) {
				Log.e("HTTP", "Serwer zwrocil http 500");
				return null;
			}
			HttpEntity entity = response.getEntity();
			msg = EntityUtils.toString(entity);
			Log.i("", msg);

		} catch (Exception e) {
			Log.e("log_tag", "Error converting result \n" + e.toString());
		}
		return msg;
	}

	public static void download(String url, String name) {
		String dir = Environment.getExternalStorageDirectory().toString() + "/OCFun";
		createDirIfNeeded(dir);
		addNoMediaFileIfNeeded(dir);
		String[] explodedPath = name.split("/");
		dir += "/" + explodedPath[0];
		createDirIfNeeded(dir);

		InputStream in = null;
		FileOutputStream out = null;
		byte buffer[] = new byte[1024];
		int length;

		try {
			while (in == null)
				in = openHttpConnection(url);
			out = new FileOutputStream(new File(dir, explodedPath[1]));
			while ((length = in.read(buffer)) > 0)
				out.write(buffer, 0, length);

			in.close();
			out.flush();
			out.close();
			Log.i("Image download", "sciagnalem obrazek " + name);
		} catch (IOException e1) {
			Log.i("Image download", "a jednak nie :P\n" + e1.getStackTrace());
		}
	}

	private static void createDirIfNeeded(String dirPath) {
		File directory = new File(dirPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	private static void addNoMediaFileIfNeeded(String dirPath) {
		File noMediaFile = new File(dirPath + "/.nomedia");
		if (!noMediaFile.exists()) {
			try {
				noMediaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static InputStream openHttpConnection(String strURL) throws IOException {
		InputStream inputStream = null;
		URL url = new URL(strURL);
		URLConnection conn = url.openConnection();

		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();

			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = httpConn.getInputStream();
			}
		} catch (Exception ex) {
		}
		return inputStream;
	}

	public static String encode(String s) {
		String ret = "";
		try {
			ret = URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}
