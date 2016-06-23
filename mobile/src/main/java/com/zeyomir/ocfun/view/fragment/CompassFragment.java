package com.zeyomir.ocfun.view.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.command.sensors.GetLastKnownLocationCommand;
import com.zeyomir.ocfun.eventbus.command.sensors.SetLocationUpdatesCommand;
import com.zeyomir.ocfun.eventbus.event.LastLocationReceivedEvent;
import com.zeyomir.ocfun.eventbus.event.LocationUpdateEvent;
import com.zeyomir.ocfun.eventbus.event.SetUpToolbarEvent;
import com.zeyomir.ocfun.utils.FuzzyTextGenerator;

import butterknife.BindView;
import butterknife.OnClick;
import io.codetail.animation.ViewAnimationUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class CompassFragment extends BaseFragment implements SensorEventListener {
    private static final String NAME = "name";
    private static final String LAT = "latitude";
    private static final String LON = "longitude";
    private static final float ALPHA = 0.2f;

    @BindView(R.id.gps)
    View noGps;
    @BindView(R.id.compass)
    ImageView compass;
    @BindView(R.id.compass2)
    ImageView compass2;
    @BindView(R.id.compass_accuracy)
    TextView compassAccuracyTextView;
    @BindView(R.id.gps_accuracy)
    TextView gpsAccuracyTextView;
    @BindView(R.id.heading)
    TextView headingTextView;
    @BindView(R.id.distance)
    TextView distanceTextView;

    private Location targetLocation;
    private boolean sensorsInitialized;
    private SensorManager sensorManager;
    private Sensor sensorGravity;
    private Sensor sensorMagnetic;
    private GeomagneticField geomagneticField;

    private float[] gravity;
    private float[] geomagnetic;
    private float[] rotation = new float[9];
    private float[] orientation = new float[3];
    private double bearing;
    private double targetBearing;
    private FuzzyTextGenerator fuzzyTextGenerator;

    @Override
    public int getLayoutResourceId() {
        return R.layout.f_compass;
    }

    @Override
    protected void onPostInjection() {
        super.onPostInjection();
        bus.post(new SetUpToolbarEvent(getString(R.string.toolbar_title_single_cache), getArguments().getString(NAME), -1, false));
        targetLocation = new Location("?");
        targetLocation.setLatitude(getArguments().getFloat(LAT));
        targetLocation.setLongitude(getArguments().getFloat(LON));
        fuzzyTextGenerator = new FuzzyTextGenerator(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        CompassFragmentPermissionsDispatcher.getLocationWithCheck(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorsInitialized) {
            bus.post(new SetLocationUpdatesCommand(SetLocationUpdatesCommand.RequestType.STOP));
            sensorManager.unregisterListener(this, sensorGravity);
            sensorManager.unregisterListener(this, sensorMagnetic);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CompassFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void getLocation() {
        sensorsInitialized = true;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        bus.post(new GetLastKnownLocationCommand());
        bus.post(new SetLocationUpdatesCommand(SetLocationUpdatesCommand.RequestType.CONTINUOUS));
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void showRationaleForGps(final PermissionRequest request) {
        super.showRationaleForGps(request);
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void showDeniedForGps() {
        super.showDeniedForGps();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void showNeverAskForGps() {
        super.showNeverAskForGps();
    }

    private void updateView() {
        compass2.setRotation((float) (360 - bearing));
        compass.setRotation((float) (targetBearing - bearing));
    }

    @Subscribe
    public void lastLocationFound(final LastLocationReceivedEvent event) {
        if (event.lastLocation == null)
            return;
        long timeDelta = System.currentTimeMillis() - event.lastLocation.getTime();
        if (timeDelta < 60_000) {
            handleLocation(event.lastLocation);
        }
    }

    @Subscribe
    public void locationFound(LocationUpdateEvent event) {
        handleLocation(event.location);
    }

    private void handleLocation(Location newLocation) {
        if (newLocation == null) {
            animateToVisible();
            return;
        }
        if (noGps.getVisibility() == View.VISIBLE) {
            animateToGone();
        }
        int accuracy = (int) newLocation.getAccuracy();
        gpsAccuracyTextView.setText(getString(R.string.accuracy, accuracy));
        Context context = getContext();
        if (accuracy < 5)
            gpsAccuracyTextView.setTextColor(ContextCompat.getColor(context, R.color.good));
        else if (accuracy < 10)
            gpsAccuracyTextView.setTextColor(ContextCompat.getColor(context, R.color.medium));
        else if (accuracy < 15)
            gpsAccuracyTextView.setTextColor(ContextCompat.getColor(context, R.color.bad));
        else
            gpsAccuracyTextView.setTextColor(ContextCompat.getColor(context, R.color.tragedy));
        targetBearing = newLocation.bearingTo(targetLocation);

        if (targetBearing < 0) {
            targetBearing += 360;
        }
        if (targetBearing > 360) {
            targetBearing -= 360;
        }
        headingTextView.setText(String.format("%d°", (int) targetBearing));

        distanceTextView.setText(fuzzyTextGenerator.forDistance((long) newLocation.distanceTo(targetLocation)));

        geomagneticField = new GeomagneticField(
                (float) newLocation.getLatitude(),
                (float) newLocation.getLongitude(),
                (float) newLocation.getAltitude(),
                System.currentTimeMillis());
        updateView();
    }

    private void animateToVisible() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int startRadius = 0;
        int finalRadius = Math.max(size.x, size.y);
        noGps.setVisibility(View.VISIBLE);
        Animator animator = ViewAnimationUtils.createCircularReveal(noGps, compass2.getLeft() + compass2.getWidth() / 2, compass2.getTop() + compass2.getHeight() / 2, startRadius, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(600);
        animator.start();
    }

    private void animateToGone() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int startRadius = 0;
        int finalRadius = Math.max(size.x, size.y);
        Animator animator = ViewAnimationUtils.createCircularReveal(noGps, compass2.getLeft() + compass2.getWidth() / 2, compass2.getTop() + compass2.getHeight() / 2, finalRadius, startRadius);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                noGps.setVisibility(View.GONE);
            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(600);
        animator.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean accelOrMagnetic = false;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = lowPassFilter(event.values, gravity);
            accelOrMagnetic = true;

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = lowPassFilter(event.values, geomagnetic);
            accelOrMagnetic = true;

        }
        if (!accelOrMagnetic)
            return;
        if (gravity == null || geomagnetic == null)
            return;
        SensorManager.getRotationMatrix(rotation, null, gravity, geomagnetic);
        SensorManager.getOrientation(rotation, orientation);
        bearing = orientation[0];
        bearing = Math.toDegrees(bearing);

        if (geomagneticField != null) {
            bearing -= geomagneticField.getDeclination();
        }

        if (bearing < 0) {
            bearing += 360;
        } else if (bearing > 360) {
            bearing -= 360;
        }

        updateView();
    }

    private float[] lowPassFilter(float[] input, float[] output) {
        if (output == null) {
            output = new float[input.length];
            System.arraycopy(input, 0, output, 0, input.length);
            return output;
        }
        for (int i = 0; i < input.length; i++)
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        return output;
    }

    @OnClick({R.id.compass_accuracy, R.id.compass_accuracy_label})
    public void calibrationHint() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.calibration_dialog_title)
                .setMessage(R.string.calibration_dialog_message)
                .show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        int stringId, colorId;
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    stringId = R.string.high;
                    colorId = R.color.good;
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    stringId = R.string.medium;
                    colorId = R.color.medium;
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    stringId = R.string.low;
                    colorId = R.color.bad;
                    break;
                default:
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    stringId = R.string.unreliable;
                    colorId = R.color.tragedy;
                    break;
            }
            compassAccuracyTextView.setText(stringId);
            compassAccuracyTextView.setTextColor(ContextCompat.getColor(getContext(), colorId));
        }
    }

    public static CompassFragment create(float latitude, float longitude, String name) {
        CompassFragment fragment = new CompassFragment();
        Bundle arguments = new Bundle();
        arguments.putString(NAME, name);
        arguments.putFloat(LAT, latitude);
        arguments.putFloat(LON, longitude);
        fragment.setArguments(arguments);
        return fragment;
    }
}
