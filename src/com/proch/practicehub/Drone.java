package com.proch.practicehub;

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
	private static final double DEFAULT_VOLUME = 0.5;
	private boolean running = false;

	public void playPitch(double frequency, double volume) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new PitchGenerator(frequency, volume));
		executor.shutdown();
		running = true;
	}

	// With no volume specified, play at default volume
	public void playPitch(double frequency) {
		playPitch(frequency, DEFAULT_VOLUME);
	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	private class PitchGenerator implements Runnable {

		private double increment; // Angular increment for each sample
		private double volume; // Ranging from 0 (silent) to 1 (full volume)
		private AudioTrack track;

		public PitchGenerator(double frequency, double volume) {
			increment = (2 * Math.PI) * frequency / SAMPLE_RATE;
			this.volume = volume;
			track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, ENCODING,
					BUFFER_SIZE * 2, AudioTrack.MODE_STREAM);
			track.play();
		}

		public void run() {
			double angle = 0;
			short samples[] = new short[BUFFER_SIZE];

			while (running) {
				for (int i = 0; i < samples.length; i++) {
					samples[i] = (short) (Math.sin(angle) * Short.MAX_VALUE * volume);
					angle += increment;
				}
				track.write(samples, 0, samples.length);
			}
			track.release();
		}
	}
}
