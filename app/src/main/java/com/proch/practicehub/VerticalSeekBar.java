/**
 * Copied and modified from Stack Overflow.
 * http://stackoverflow.com/questions/4892179/need-working-vertical-seekbar
 */
package com.proch.practicehub;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Exactly the same as SeekBar, just displayed vertically instead of horizontally.
 */
public class VerticalSeekBar extends SeekBar {

    private OnSeekBarChangeListener mListener;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        mListener = listener;
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mListener != null)
                    mListener.onStartTrackingTouch(this);
                break;
            case MotionEvent.ACTION_MOVE:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                if (mListener != null) {
                    int newProgress = getMax() - (int) (getMax() * event.getY() / getHeight());
                    mListener.onProgressChanged(this, newProgress, true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mListener != null) {
                    mListener.onStopTrackingTouch(this);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}