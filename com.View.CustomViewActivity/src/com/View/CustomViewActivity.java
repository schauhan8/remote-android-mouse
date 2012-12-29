package com.View;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class CustomViewActivity extends Activity implements SensorEventListener {
	private SensorManager mSensorManager = null;
	JSONObject accelerometerData;
	JSONObject gyroscopeData;
	private static final int CORRECTED_GYROSCOPE_SENSOR = 4;

	private float[] gravity = { 0, 0, 0 };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new myView(this, null));

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		List<Sensor> deviceSensors = mSensorManager
				.getSensorList(Sensor.TYPE_ALL);
//		for (Sensor s : deviceSensors) {
//			log(s.toString());
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		mSensorManager.registerListener(this,
//				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//				SensorManager.SENSOR_DELAY_NORMAL);
//		mSensorManager.registerListener(this,
//				mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
//				SensorManager.SENSOR_DELAY_NORMAL);
//		mSensorManager.registerListener(this,
//				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
//				SensorManager.SENSOR_DELAY_NORMAL);
//		mSensorManager.registerListener(this,
//				mSensorManager.getDefaultSensor(CORRECTED_GYROSCOPE_SENSOR),
//				SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
//		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//			getAccelerometer(event);
//		} else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//			getGyroscope(event);
//		} else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
//			getRotationVector(event);
//		}
	}

	private float magnitude(float[] input) {
		float magnitude = (float) Math.sqrt((double) (input[1] * input[1]
				+ input[0] * input[0] + input[2] * input[2]));
		return magnitude;
	}

	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;
		float[] acceleration = new float[3];
		accelerometerData = new JSONObject();

		final float alpha = (float) 0.8;

		gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
		gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
		gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

		acceleration[0] = event.values[0] - gravity[0];
		acceleration[1] = event.values[1] - gravity[1];
		acceleration[2] = event.values[2] - gravity[2];

		// acceleration[0] = event.values[0];
		// acceleration[1] = event.values[1];
		// acceleration[2] = event.values[2];

		float mag = magnitude(acceleration);
		if (mag > 0.173f) {
			// Log.d("MESSAGE", String.valueOf(acceleration[0]));
			// Log.d("MESSAGE", String.valueOf(acceleration[1]));
			// Log.d("MESSAGE", String.valueOf(acceleration[2]));

		}

		try {
			accelerometerData.put("x", String.valueOf(values[0]));
			accelerometerData.put("y", String.valueOf(values[1]));
			accelerometerData.put("z", String.valueOf(values[2]));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject toSend = new JSONObject();
		try {
			toSend.put("action", "ACCELEROMETER");
			toSend.put("value", accelerometerData);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// new SendToServer().execute(toSend.toString());
	}

	private void getGyroscope(SensorEvent event) {
		float[] values = event.values;

		gyroscopeData = new JSONObject();

		try {
			gyroscopeData.put("x", String.valueOf(values[0]));
			gyroscopeData.put("y", String.valueOf(values[1]));
			gyroscopeData.put("z", String.valueOf(values[2]));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject toSend = new JSONObject();
		try {
			toSend.put("action", "GYROSCOPE");
			toSend.put("value", gyroscopeData);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// new
		// SendToServer(Parameters.WEB_SERVER_UDP_LISTENER_PORT).execute(toSend.toString());
	}

	private void getCorrectedGyroscope(SensorEvent event) {
		float[] values = event.values;

		Log.d("MESSAGE", "***** CORRECTED GYROSCOPE DATA *****");
		Log.d("MESSAGE", String.valueOf(values[0]));
		Log.d("MESSAGE", String.valueOf(values[1]));
		Log.d("MESSAGE", String.valueOf(values[2]));
	}

	private void getRotationVector(SensorEvent event) {
		float[] values = event.values;

		// Log.d("MESSAGE", "***** ROTATION VECTOR DATA *****");
		// Log.d("MESSAGE", String.valueOf(values[0]));
		// Log.d("MESSAGE", String.valueOf(values[1]));
		// Log.d("MESSAGE", String.valueOf(values[2]));

	}

	private void log(Object o) {
		if (o instanceof String) {
			Log.d("MESSAGE", (String) o);
		} else {
			Log.d("MESSAGE", String.valueOf(o));
		}
	}
}
