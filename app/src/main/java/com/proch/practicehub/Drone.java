package com.proch.practicehub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Drone {

  private static final float MIN_VOLUME = 0f;
  private static final float MAX_VOLUME = 1f;
  public static final float DEFAULT_VOLUME = MAX_VOLUME;
  private boolean mRunning = false;
  private boolean mAddFifth = false;
  private Note mLastNotePlayed;
  private ExecutorService mExecutor;
  private PitchGenerator mPitchGenerator;
  private float mInitialVolume; // Volume to start drone on, will not be updated if changes after

  public Drone(float initialVolume) {
    mExecutor = Executors.newSingleThreadExecutor();
    mInitialVolume = initialVolume;
  }

  public Drone() {
    // Start with default volume, if not specified
    this(DEFAULT_VOLUME);
  }

  /**
   * Releases resources used by the drone. Should be called when drone is no longer in use.
   */
  public void destroy() {
    mExecutor.shutdown();
  }

  public Note getLastNotePlayed() {
    return mLastNotePlayed;
  }

  public boolean isRunning() {
    return mRunning;
  }

  public boolean addFifth() {
    return mAddFifth;
  }

  public void setAddFifth(boolean newValue) {
    mAddFifth = newValue;
  }

  /**
   * Starts playing the given frequency and our initial volume indefinitely.
   * 
   * @param frequency Frequency in Hz to be played
   */
  public void playPitch(double frequency) {
    mRunning = true;
    mPitchGenerator = new PitchGenerator(frequency, mInitialVolume);
    mExecutor.execute(mPitchGenerator);
  }

  /**
   * Plays the given note's frequency until stopped.
   * 
   * @param note Note that is played
   */
  public void playNote(Note note) {
    mAddFifth = false;
    playPitch(note.getFrequency());
    mLastNotePlayed = note;
  }

  /**
   * Plays the specified note and the note a fifth above it until stopped.
   * 
   * @param note Fundamental Note that is played
   */
  public void playNoteWithFifth(Note note) {
    mAddFifth = true;
    playPitch(note.getFrequency());
    mLastNotePlayed = note;
  }

  public void stop() {
    mRunning = false;
  }

  /**
   * Return the drones volume that it either is playing at, or will play at.
   * 
   * @return Float between 0 (silent) and 1 (full volume)
   */
  public float getVolume() {
    if (mPitchGenerator != null) {
      return mPitchGenerator.getVolume();
    }
    return mInitialVolume;
  }

  public void setVolume(float newVolume) {
    mInitialVolume = newVolume;
    if (mPitchGenerator != null) {
      mPitchGenerator.setVolume(newVolume);
    }
  }

  private class PitchGenerator implements Runnable {

    private static final int SAMPLE_RATE = 8000;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private final int BUFFER_SIZE;
    private double mIncrement; // Angular increment for each sample
    private float mVolume; // Ranging from 0 (silent) to 1 (full volume)
    private AudioTrack mTrack;

    public PitchGenerator(double frequency, float volume) {
      BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, ENCODING);

      mIncrement = (2 * Math.PI) * frequency / SAMPLE_RATE;
      mTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, ENCODING,
          BUFFER_SIZE * 2, AudioTrack.MODE_STREAM);
      mTrack.play();
      setVolume(volume);
    }

    public float getVolume() {
      return mVolume;
    }

    /**
     * Sets the new volume for this pitch generator.
     * 
     * @param newVolume Float value between MIN_VOLUME and MAX_VOLUME
     */
    public void setVolume(float newVolume) {
      if (newVolume < MIN_VOLUME || newVolume > MAX_VOLUME) {
        throw new IllegalArgumentException("Volume outside of valid range");
      }
      mVolume = newVolume;
      mTrack.setStereoVolume(mVolume, mVolume);
    }

    public void run() {
      double angle = 0;
      short samples[] = new short[BUFFER_SIZE];

      while (mRunning) {
        for (int i = 0; i < samples.length; i++) {
          double sinValue = mAddFifth ? // Divide by 2 just to get into approximate range [-1, -1]
          (Math.sin(angle) + Math.sin(1.5 * angle)) / 2
              : Math.sin(angle);

          double decreaseVolumeConst = 0.5;
          samples[i] = (short) (sinValue * Short.MAX_VALUE * decreaseVolumeConst);
          angle += mIncrement;
        }
        mTrack.write(samples, 0, samples.length);
      }
      mTrack.release();
    }
  }
}
