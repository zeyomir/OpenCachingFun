package com.zeyomir.ocfun.controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class DisplayCompass {
	private static float[] valuesAccelerometer = new float[3];
	;
	private static float[] valuesMagneticField = new float[3];

	private static float[] matrixR = new float[9];
	private static float[] matrixI = new float[9];
	private static float[] matrixValues = new float[3];

	public static RotateAnimation getAnimation(float direction) {
		RotateAnimation animation = new RotateAnimation(direction, direction,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(1);
		animation.setFillAfter(true);
		return animation;
	}

	public static Double getUserOrientation(SensorEvent event) {
		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				for (int i = 0; i < 3; i++) {
					valuesAccelerometer[i] = event.values[i];
				}
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				for (int i = 0; i < 3; i++) {
					valuesMagneticField[i] = event.values[i];
				}
				break;
		}

		Double userOrientation = null;

		boolean success = SensorManager.getRotationMatrix(
				matrixR,
				matrixI,
				valuesAccelerometer,
				valuesMagneticField);

		if (success) {
			SensorManager.getOrientation(matrixR, matrixValues);

			userOrientation = Math.toDegrees(matrixValues[0]);

			if (userOrientation < 0)
				userOrientation += 360;
		}

		return userOrientation;
	}
}
