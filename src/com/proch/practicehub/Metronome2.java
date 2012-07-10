package com.proch.practicehub;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Metronome2 {

  private short[] tick_data;
  private short[] tock_data;
  private boolean running = false;
  private int tempo;
  private boolean[] pattern = { true };
  private int currentBeat;
  private AudioTrack track;
  private static final int SAMPLE_RATE = 44100;
  private static final int BUFFER_SIZE = 22100; // AudioTrack.getMinBufferSize(SAMPLE_RATE,
                                                // CHANNEL_CONFIG, ENCODING);

  public Metronome2(Context context) {
    tick_data = Utility.intToShortArray(context.getResources().getIntArray(R.array.tick));
    tock_data = Utility.intToShortArray(context.getResources().getIntArray(R.array.tock));

    track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE, AudioTrack.MODE_STREAM);
  }

  /**
   * Releases resources used by the metronome. Should be called when metronome is no longer in use.
   */
  public void destroy() {
    track.release();
  }

  /**
   * Starts the metronome at the given tempo and beats.
   * 
   * @param tempo Tempo in beats per minute of the metronome
   * @param beatsOn Number of consecutive beats it will click for in one cycle
   * @param beatsOff Number of consecutive beats of rest at the end of each cycle
   */
  public void start(int tempo, int beatsOn, int beatsOff) {
    this.tempo = tempo;
    pattern = generatePattern(beatsOn, beatsOff);
    currentBeat = 0;
    running = true;

    track.play();

    new Thread(new Clicker()).start();
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
    // TODO: Finish
    track.stop();
  }

  /**
   * Stops and starts the metronome with the given tempo and beats pattern.
   * 
   * @param tempo Beats per minute that the metronome will click
   * @param beatsOn Number of consecutive beats it will click for in one cycle
   * @param beatsOff Number of consecutive beats of rest at the end of each cycle
   */
  public void restart(int tempo, int beatsOn, int beatsOff) {
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

    public void run() {
      int interval_in_frames = 60 * SAMPLE_RATE / tempo;
      int frames_since_last_played = 0;

      while (true) {
        if (frames_since_last_played >= interval_in_frames) {
          track.write(tick_data, 0, tick_data.length);
          frames_since_last_played = 0;
        } else {
          int frames_left_to_wait = interval_in_frames - frames_since_last_played;

          int rest_length_in_frames = Math.min(frames_left_to_wait, 2205);
          track.write(new short[rest_length_in_frames], 0, rest_length_in_frames);

          frames_since_last_played += rest_length_in_frames;
        }
      }
    }
  }

}
