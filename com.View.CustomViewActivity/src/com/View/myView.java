package com.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class myView extends View {
	private Paint paint;
	private Path path;
	private JSONArray touchPoints;
	private JSONObject toSend;

	public myView(Context context, AttributeSet attrs) {
		super(context, attrs);

		touchPoints = new JSONArray();
		toSend = new JSONObject();
		paint = new Paint();
		path = new Path();

		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(1f);
		this.setOnTouchListener(new MyOnTouchListener());
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

	private void addTouchPoint(JSONObject position) {
		try {
			int length = touchPoints.length();
			if (length == 0) {
				touchPoints.put(position);
				return;
			}
			JSONObject lastPoint = touchPoints.getJSONObject(length - 1);
			Long t1 = (Long) lastPoint.get("timestamp");
			Long t2 = (Long) position.get("timestamp");
			if (t2 - t1 > Parameters.CLUTCH_TIMEOUT) {
				touchPoints = new JSONArray();
			}
			touchPoints.put(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class MyOnTouchListener implements OnTouchListener {

		private long touchStartTime;
		

		public MyOnTouchListener() {
			this.touchStartTime = 0;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			long currTime = System.currentTimeMillis();
			int eventX = (int) event.getX();
			int eventY = (int) event.getY();
			JSONObject position = new JSONObject();

			try {
				position.put("x", eventX);
				position.put("y", eventY);
				position.put("timestamp", currTime);
				addTouchPoint(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//path.moveTo(eventX, eventY);
				touchStartTime = currTime;
			case MotionEvent.ACTION_MOVE:
				//path.lineTo(eventX, eventY);
				break;
			case MotionEvent.ACTION_UP:
				if (currTime - touchStartTime < Parameters.IS_CLICK_TIMEOUT) {
					toSend = new JSONObject();
					try {
						toSend.put("action", "MOUSE_CLICK");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					new SendToServer(Parameters.MOUSE_CONTROLLER_SERVER_PORT)
							.execute(toSend.toString());
					new SendToServer(Parameters.WEB_SERVER_UDP_LISTENER_PORT)
					.execute(toSend.toString());
					touchStartTime = 0;
					touchPoints = new JSONArray();
					return true;
				}
				break;
			default:
				return true;
			}

			if (touchPoints.length() >= Parameters.BATCH_SIZE) {
				toSend = new JSONObject();
				try{
					toSend.put("action", "MOUSE_MOVE");
					toSend.put("value", touchPoints);
				}catch(JSONException e){
					/* Do nothing for now */
				}
				new SendToServer(Parameters.MOUSE_CONTROLLER_SERVER_PORT).execute(toSend.toString());
				new SendToServer(Parameters.WEB_SERVER_UDP_LISTENER_PORT).execute(toSend.toString());
				touchPoints = new JSONArray();
			}
			invalidate();
			return true;
		}

	}
}
