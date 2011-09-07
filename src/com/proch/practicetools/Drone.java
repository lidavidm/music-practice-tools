package com.proch.practicetools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Drone {

	private static final int SAMPLE_RATE = 8000;
	private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private static final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG,
			ENCODING);
	private boolean running = false;

	public void playPitch(double frequency) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new PitchGenerator(frequency));
		executor.shutdown();
		running = true;
	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	private class PitchGenerator implements Runnable {

		private double increment; // Angular increment for each sample
		private AudioTrack track;

		public PitchGenerator(double frequency) {
			increment = (2 * Math.PI) * frequency / SAMPLE_RATE;
			track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, ENCODING,
					BUFFER_SIZE * 2, AudioTrack.MODE_STREAM);
			track.play();
		}

		public void run() {
			double angle = 0;
			short samples[] = new short[BUFFER_SIZE];

			while (running) {
				for (int i = 0; i < samples.length; i++) {
					samples[i] = (short) (Math.sin(angle) * Short.MAX_VALUE);
					angle += increment;
				}
				track.write(samples, 0, samples.length);
			}
			track.release();
		}
	}
}
