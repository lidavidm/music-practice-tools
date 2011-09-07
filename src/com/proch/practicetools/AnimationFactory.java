package com.proch.practicetools;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationFactory {
	public static final int IN_FROM_RIGHT = 1;
	public static final int IN_FROM_LEFT = 2;
	public static final int OUT_TO_RIGHT = 3;
	public static final int OUT_TO_LEFT = 4;
	// alpha
	public static final int FADE_IN = 5;
	public static final int FADE_OUT = 6;
	public static final int FADE_IN_50 = 7;
	public static final int FADE_OUT_50 = 8;

	public static Animation getAnimation(int type, int duration) {

		Animation result = null;

		switch (type) {
		case IN_FROM_RIGHT:
			result = BasicAnimation.inFromRightAnimation(duration);
			break;
		case IN_FROM_LEFT:
			result = BasicAnimation.inFromLeftAnimation(duration);
			break;
		case OUT_TO_RIGHT:
			result = BasicAnimation.outToRightAnimation(duration);
			break;
		case OUT_TO_LEFT:
			result = BasicAnimation.outToLeftAnimation(duration);
			break;
		case FADE_IN:
			result = BasicAnimation.fadeIn(duration);
			break;
		case FADE_OUT:
			result = BasicAnimation.fadeOut(duration);
			break;
		case FADE_IN_50:
			result = BasicAnimation.fadeIn50(duration);
			break;
		case FADE_OUT_50:
			result = BasicAnimation.fadeOut50(duration);
			break;
		}
		return result;
	}

	static class BasicAnimation {

		public static Animation inFromRightAnimation(int duration) {

			Animation inFromRight = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT,
					0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);
			inFromRight.setDuration(duration);
			inFromRight.setInterpolator(new AccelerateInterpolator());
			return inFromRight;
		}

		public static Animation outToLeftAnimation(int duration) {
			Animation outtoLeft = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
					-1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);
			outtoLeft.setDuration(duration);
			outtoLeft.setInterpolator(new AccelerateInterpolator());
			return outtoLeft;
		}

		public static Animation inFromLeftAnimation(int duration) {
			Animation inFromLeft = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT,
					0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);
			inFromLeft.setDuration(duration);
			inFromLeft.setInterpolator(new AccelerateInterpolator());
			return inFromLeft;
		}

		public static Animation outToRightAnimation(int duration) {
			Animation outtoRight = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
					+1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);
			outtoRight.setDuration(duration);
			outtoRight.setInterpolator(new AccelerateInterpolator());
			return outtoRight;
		}

		public static Animation fadeIn(int duration) {
			Animation fadeInAnimation = new AlphaAnimation(0, 1);
			fadeInAnimation.setDuration(duration);
			return fadeInAnimation;
		}

		public static Animation fadeOut(int duration) {
			Animation fadeOutAnimation = new AlphaAnimation(1, 0);
			fadeOutAnimation.setDuration(duration);
			return fadeOutAnimation;
		}

		public static Animation fadeIn50(int duration) {
			Animation fadeOutAnimation = new AlphaAnimation(0.5f, 1f);
			fadeOutAnimation.setDuration(duration);
			return fadeOutAnimation;
		}

		public static Animation fadeOut50(int duration) {
			Animation fadeOutAnimation = new AlphaAnimation(1, 0.5f);
			fadeOutAnimation.setDuration(duration);
			return fadeOutAnimation;
		}

	}
}
