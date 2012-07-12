package com.proch.practicehub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Drone {

  private static final double DEFAULT_VOLUME = 0.5;
  private boolean mRunning = false;

  public void playPitch(double frequency, double volume) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(new PitchGenerator(frequency, volume));
    executor.shutdown();
    mRunning = true;
  }

  // With no volume specified, play at default volume
  public void playPitch(double frequency) {
    playPitch(frequency, DEFAULT_VOLUME);
  }

  public void stop() {
    mRunning = false;
  }

  public boolean isRunning() {
    return mRunning;
  }

  private class PitchGenerator implements Runnable {

    private static final int SAMPLE_RATE = 8000;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private final int BUFFER_SIZE;
    private double mIncrement; // Angular increment for each sample
    private double mVolume; // Ranging from 0 (silent) to 1 (full volume)
    private AudioTrack mTrack;

    public PitchGenerator(double frequency, double volume) {
      BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, ENCODING);

      mIncrement = (2 * Math.PI) * frequency / SAMPLE_RATE;
      this.mVolume = volume;
      mTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, ENCODING,
          BUFFER_SIZE * 2, AudioTrack.MODE_STREAM);
      mTrack.play();
    }

    public void run() {
      double angle = 0;
      short samples[] = new short[BUFFER_SIZE];

      while (mRunning) {
        for (int i = 0; i < samples.length; i++) {
          samples[i] = (short) (Math.sin(angle) * Short.MAX_VALUE * mVolume);
          angle += mIncrement;
        }
        mTrack.write(samples, 0, samples.length);
      }
      mTrack.release();
    }
  }
}
