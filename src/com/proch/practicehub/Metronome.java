package com.proch.practicehub;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.proch.practicehub.R;

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

	/**
	 * Creates and initializes a metronome by allocating and loading its resources.
	 * 
	 * @param context
	 */
	public Metronome(Context context) {
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		tickId = soundPool.load(context.getResources().openRawResourceFd(R.raw.tick), 1);
		tockId = soundPool.load(context.getResources().openRawResourceFd(R.raw.tock), 1);
		clicker = new Clicker(soundPool, tickId, tockId);
		
		executor = new ScheduledThreadPoolExecutor(SIZE_THREAD_POOL);
	}

	/**
	 * Releases resources used by the metronome. Should be called when metronome is no longer in use.
	 */
	public void destroy() {
		soundPool.release();
		soundPool = null;
	}

	/**
	 * Starts the metronome at the given tempo and beats.
	 * 
	 * @param tempo Tempo in beats per minute of the metronome
	 * @param beatsOn Number of consecutive beats it will click for in one cycle
	 * @param beatsOff Number of consecutive beats of rest at the end of each cycle
	 */
	public void start(int tempo, int beatsOn, int beatsOff) {
		pattern = generatePattern(beatsOn, beatsOff);
		currentBeat = 0;
		running = true;
		executor = new ScheduledThreadPoolExecutor(SIZE_THREAD_POOL); // TODO: figure out this hack
		executor.scheduleAtFixedRate(clicker, 0, 60000 / tempo, TimeUnit.MILLISECONDS);
	}

	/**
	 * Starts the metronome with the default settings for tempo and beats on/off
	 */
	public void start(int tempo) {
		start(tempo, 1, 0);
	}

	/**
	 * Stops the metronome if it was running.
	 */
	public void stop() {
		running = false;
		executor.shutdown();
	}

	/**
	 * Updates the metronome with the given tempo and beats pattern.
	 * 
	 * @param tempo Beats per minute that the metronome will click
	 * @param beatsOn Number of consecutive beats it will click for in one cycle
	 * @param beatsOff Number of consecutive beats of rest at the end of each cycle
	 */
	public void update(int tempo, int beatsOn, int beatsOff) {
		stop();
		start(tempo, beatsOn, beatsOff);
	}

	/**
	 * Returns true if the metronome is currently running.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Generates a pattern of beatsOn number of trues, and beatsOff number of falses.
	 * 
	 * @param beatsOn Number of consecutive beats it will click for in one cycle
	 * @param beatsOff Number of consecutive beats of rest at the end of each cycle
	 * 
	 * @return Array of booleans of a single cycle for the metronome with true representing clicks
	 */
	private boolean[] generatePattern(int beatsOn, int beatsOff) {
		boolean[] result = new boolean[beatsOn + beatsOff];
		// Pattern is all falses by default, so just set beatsOn indices to true
		for (int i = 0; i < beatsOn; i++)
			result[i] = true;

		return result;
	}

	/**
	 * Runnable class that keeps looping through the cycle clicking as specified by the pattern array. 
	 */
	private class Clicker implements Runnable {

		private SoundPool soundPool;
		private int tickId, tockId;

		/**
		 * Assigns instance variables from arguments.
		 * 
		 * @param soundPool Initilized and loaded SoundPool used to play tickId and tockId
		 * @param tickId ID of the loaded tick sound file
		 * @param tockId ID of the loaded tock sound file
		 */
		public Clicker(SoundPool soundPool, int tickId, int tockId) {
			this.soundPool = soundPool;
			this.tickId = tickId;
			this.tockId = tockId;
		}

		/**
		 * Starts clicking based on the specified pattern, looping indefinitely.
		 */
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
