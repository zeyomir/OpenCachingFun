package com.zeyomir.ocfun.gui;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import com.zeyomir.ocfun.LocationProvider;
import com.zeyomir.ocfun.LocationUser;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.DisplayCache;
import com.zeyomir.ocfun.controller.DisplayCompass;
import com.zeyomir.ocfun.controller.helper.LocationHelper;
import com.zeyomir.ocfun.model.Cache;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;

public class Compass extends Activity implements LocationUser, SensorEventListener {
    public static final double EASING_FACTOR = 0.374;
    SensorManager sensorManager;
    private Cache cache;
    private Location cacheLocation;
    private double azimuth, error;
    private String distance;
    private Sensor sensorAccelerometer, sensorMagneticField;


    private double userOrientation = 0, lastUserOrientation = 0;
    private ImageView compassImage;
    private TextView distanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass);
        compassImage = (ImageView) findViewById(R.id.imageView);
        distanceText = (TextView) findViewById(R.id.textView1);

        cache = DisplayCache.getCache(getIntent());
        String[] coords = cache.coords.split("\\|");
        cacheLocation = new Location("???");
        cacheLocation.setLatitude(Double.parseDouble(coords[0]));
        cacheLocation.setLongitude(Double.parseDouble(coords[1]));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        ((LocationProvider) getApplicationContext())
                .registerForFrequentlyLocationUpdates(this);
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this,
                sensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorMagneticField,
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this,
                sensorAccelerometer);
        sensorManager.unregisterListener(this,
                sensorMagneticField);
        ((LocationProvider) getApplicationContext())
                .unregister(this);
        super.onPause();
    }

    @Override
    public void locationFound(Location l) {
        distance = LocationHelper.getDistance(l, cacheLocation);
        error = l.getAccuracy();
        azimuth = l.bearingTo(cacheLocation);
        if (azimuth < 0)
            azimuth += 360;
        updateArrowDirection();
    }

    private void updateArrowDirection() {
        //low pass filter to smooth out the readings from sensor and get rid of the noise
        userOrientation = lastUserOrientation * (1 - EASING_FACTOR) + userOrientation * EASING_FACTOR;
        float direction = (float) (azimuth - userOrientation);
        lastUserOrientation = userOrientation;
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            RotateAnimation animation = DisplayCompass.getAnimation(direction);
            compassImage.startAnimation(animation);
        } else {
            compassImage.setRotation(direction);
        }
        distanceText.setText(distance + " (+/- " + error + " m)");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Double computedUserOrientation = DisplayCompass.getUserOrientation(event);
        if (computedUserOrientation == null)
            return;
        userOrientation = computedUserOrientation;
        updateArrowDirection();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
