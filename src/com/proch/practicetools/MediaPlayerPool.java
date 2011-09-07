package com.proch.practicetools;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class MediaPlayerPool {
	private MediaPlayer mp[];
	private int mNumPools;
	private int mNext = 0;

	public MediaPlayerPool(Context context, int num, int resourceId) {
		mp = new MediaPlayer[num];
		for (int i = 0; i < num; i++) {
			mp[i] = MediaPlayer.create(context, resourceId);
			mp[i].setLooping(false);

			mp[i].setOnErrorListener(new MediaPlayer.OnErrorListener() {
				// @Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					Log.v("MediaPlayerPool", "error on media player what=" + what + " extra=" + extra);
					return false;
				}
			});
		}
		mNumPools = num;
	}

	public void start() {
		mp[mNext].start();
		Log.i("metronome", "starting i=" + mNext);
		mNext++;
		mNext %= mNumPools;

		return;
	}

	public void stop() {
		for (MediaPlayer element : mp) {
			if (element.isPlaying()) {
				element.stop();
			}
		}
	}

	public void onDestroy() {
		for (int i = 0; i < mp.length; i++) {
			if (mp[i].isPlaying()) {
				mp[i].stop();
			}
			System.out.println("releasing");
			mp[i].release();
			mp[i] = null;
		}
	}
}
