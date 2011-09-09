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

	private static final int BG_X = 0;
	private static final int BG_Y = 0;
	private static final int BG_WIDTH = 280;
	private static final int BG_HEIGHT = 200;
	private static final int MARKER_Y = BG_Y;
	private static final int MARKER_WIDTH = 5;
	private static final int MARKER_OFFSET = MARKER_WIDTH / 2;
	private static final int MARKER_HEIGHT = BG_HEIGHT;
	private static final int NUM_STEPS_BETWEEN_UPDATES = 10;
	private static final int TRANSITION_TIME_IN_MILLIS = 400;
	private static final long UPDATE_DELAY = TRANSITION_TIME_IN_MILLIS / NUM_STEPS_BETWEEN_UPDATES;
	private ShapeDrawable mBackground;
	private ShapeDrawable mCentsMarker;
	private ShapeDrawable mMiddleMarker;
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

		mBackground = new ShapeDrawable(new RectShape());
		mBackground.getPaint().setColor(0xffffffff);
		mBackground.setBounds(BG_X, BG_Y, BG_X + BG_WIDTH, BG_Y + BG_HEIGHT);

		mMiddleMarker = new ShapeDrawable(new RectShape());
		mMiddleMarker.getPaint().setColor(0xff000000);
		setMarker(mMiddleMarker, 0);

		mCentsMarker = new ShapeDrawable(new RectShape());
		mCentsMarker.getPaint().setColor(0xffff0000);
		setMarker(mCentsMarker, 0);
		
		update();
	}

	public void update() {
		long now = System.currentTimeMillis();

		if (now - mLastMove > UPDATE_DELAY && !doneMovingLine()) {
			setMarker(mCentsMarker, mCurrentCents + mCentsStep);
			mLastMove = now;
		}
		mRedrawHandler.sleep(UPDATE_DELAY);
	}

	private boolean doneMovingLine() {
		return Math.abs(mCurrentCents - mCents) < 0.01;
	}

	// takes as input number of cents (should be in range -50 to 50) and sets
	// marker to correct position
	public void setCentsMarker(double newCents) {
		mCents = newCents;
		mCentsStep = (newCents - mCurrentCents) / NUM_STEPS_BETWEEN_UPDATES;
	}

	// takes as input number of cents (should be in range -50 to 50) and sets
	// marker to correct position
	private void setMarker(ShapeDrawable marker, double cents) {
		int newX = (int) (BG_WIDTH / 100.0 * (cents + 50.0) + BG_X - MARKER_OFFSET);
		marker.setBounds(newX, MARKER_Y, newX + MARKER_WIDTH, MARKER_Y + MARKER_HEIGHT);
		mCurrentCents = cents;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mBackground.draw(canvas);
		mCentsMarker.draw(canvas);
		mMiddleMarker.draw(canvas);
	}

}
