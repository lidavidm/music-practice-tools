package com.proch.practicehub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Drone {

  private static final double DEFAULT_VOLUME = 0.5;
  private boolean mRunning = false;
  private boolean mAddFifth = false;
  private Note mLastNotePlayed;
  private ExecutorService executor;
  
  public Drone() {
    executor = Executors.newSingleThreadExecutor();
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
   * Starts playing the given frequency and the given volume indefinitely.
   * 
   * @param frequency Frequency in Hz to be played
   * @param volume Number between 0 and 1, describing the volume of the pitch
   */
  public void playPitch(double frequency, double volume) {
    mRunning = true;
    executor.execute(new PitchGenerator(frequency, volume));
  }

  /**
   * Plays the given frequency at the default volume until stopped.
   * 
   * @param frequency Frequency in Hz of the note to be played
   */
  public void playPitch(double frequency) {
    playPitch(frequency, DEFAULT_VOLUME);
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
          double sinValue = mAddFifth ?
              (Math.sin(angle) + Math.sin(1.5 * angle)) / 2 : Math.sin(angle);

          samples[i] = (short) (sinValue * Short.MAX_VALUE * mVolume);
          angle += mIncrement;
        }
        mTrack.write(samples, 0, samples.length);
      }
      mTrack.release();
    }
  }
}
