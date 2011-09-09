package com.proch.practicetools;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Metronome {

	private SoundPool soundPool;
	private static final int SIZE_THREAD_POOL = 4;
	private ScheduledThreadPoolExecutor executor;
	private int tickId, tockId;
	private Clicker clicker;
	private boolean running = false;
	private boolean[] pattern = { true };
	private int currentBeat;

	public Metronome(Context context) {
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		tickId = soundPool.load(context.getResources().openRawResourceFd(R.raw.tick), 1);
		tockId = soundPool.load(context.getResources().openRawResourceFd(R.raw.tock), 1);
		clicker = new Clicker(soundPool, tickId, tockId);
		
		executor = new ScheduledThreadPoolExecutor(SIZE_THREAD_POOL);
	}

	public void close() {
		soundPool.release();
		soundPool = null;
	}

	public void start(int tempo, int beatsOn, int beatsOff) {
		pattern = generatePattern(beatsOn, beatsOff);
		currentBeat = 0;
		running = true;
		executor = new ScheduledThreadPoolExecutor(SIZE_THREAD_POOL); // TODO: figure out this hack
		executor.scheduleAtFixedRate(clicker, 0, 60000 / tempo, TimeUnit.MILLISECONDS);
	}

	public void start(int tempo) {
		start(tempo, 1, 0);
	}

	public void stop() {
		running = false;
		executor.shutdown();
	}

	public void restart(int tempo, int beatsOn, int beatsOff) {
		stop();
		start(tempo, beatsOn, beatsOff);
	}

	public boolean isRunning() {
		return running;
	}

	// returns pattern array with mBeatsOn trues and mBeatsOff falses
	private boolean[] generatePattern(int beatsOn, int beatsOff) {
		boolean[] result = new boolean[beatsOn + beatsOff];
		// Pattern is all falses by default, so just set beatsOn indices to true
		for (int i = 0; i < beatsOn; i++)
			result[i] = true;

		return result;
	}

	private class Clicker implements Runnable {

		private SoundPool soundPool;
		private int tickId, tockId;

		public Clicker(SoundPool soundPool, int tickId, int tockId) {
			this.soundPool = soundPool;
			this.tickId = tickId;
			this.tockId = tockId;
		}

		public void run() {
			if (pattern[currentBeat]) {
				if (currentBeat == 0) {
					soundPool.play(tockId, 1.0f, 1.0f, 1, 0, 1.0f);
				} else {
					soundPool.play(tickId, 1.0f, 1.0f, 1, 0, 1.0f);
				}
			}
			currentBeat++;
			currentBeat %= pattern.length;
		}

	}
}
