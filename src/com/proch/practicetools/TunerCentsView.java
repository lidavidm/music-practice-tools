package com.proch.practicetools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class TunerCentsView extends View {

	private static final int MARKER_WIDTH = 5;
	private static final int MARKER_OFFSET = MARKER_WIDTH / 2;
	private static final int REFRESH_RATE = 50; // In milliseconds
	private static final int TRANSITION_TIME = 1000; // Also in milliseconds
	private static final int NUM_STEPS_BETWEEN_UPDATES = TRANSITION_TIME / REFRESH_RATE;
	private ShapeDrawable mCentsMarker;
	private ShapeDrawable mMiddleMarker;
	private boolean mMiddleMarkerInitialized;
	private double mCurrentCents; // Currently displayed cents
	private double mCents; // Cents that we are moving to
	private long mLastMove;
	private double mCentsStep;
	/**
	 * Create a simple handler that we can use to cause animation to happen. We
	 * set ourselves as a target and we can use the sleep() function to cause an
	 * update/invalidate to occur at a later date.
	 */
	private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			TunerCentsView.this.update();
			TunerCentsView.this.invalidate();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	public TunerCentsView(Context context) {
		super(context);
	}

	public TunerCentsView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setBackgroundColor(getResources().getColor(R.color.white));

		mMiddleMarker = new ShapeDrawable(new RectShape());
		mMiddleMarker.getPaint().setColor(getResources().getColor(R.color.black));

		mCentsMarker = new ShapeDrawable(new RectShape());
		mCentsMarker.getPaint().setColor(getResources().getColor(R.color.cents_marker));
		
		update();
	}

	public void update() {
		long now = System.currentTimeMillis();

		if (now - mLastMove > REFRESH_RATE && !doneMovingLine()) {
			setMarker(mCentsMarker, mCurrentCents + mCentsStep);
			mLastMove = now;
		}
		mRedrawHandler.sleep(REFRESH_RATE);
	}

	private boolean doneMovingLine() {
		return Math.abs(mCurrentCents - mCents) < 0.01;
	}

	// takes as input number of cents (should be in range -50 to 50) and sets
	// marker to correct position
	public void setCentsMarker(double newCents) {
		if(!mMiddleMarkerInitialized) { // TODO: possibly figure out a more elegant way to do this
			setMarker(mMiddleMarker, 0);
			mMiddleMarkerInitialized = true;
		}
		mCents = newCents;
		mCentsStep = (newCents - mCurrentCents) / NUM_STEPS_BETWEEN_UPDATES;
	}

	// takes as input number of cents (should be in range -50 to 50) and sets
	// marker to correct position
	private void setMarker(ShapeDrawable marker, double cents) {
		int newX = (int) (getWidth() / 100.0 * (cents + 50.0) - MARKER_OFFSET);
		marker.setBounds(newX, 0, newX + MARKER_WIDTH, getHeight());
		mCurrentCents = cents;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mCentsMarker.draw(canvas);
		mMiddleMarker.draw(canvas);
	}

}
