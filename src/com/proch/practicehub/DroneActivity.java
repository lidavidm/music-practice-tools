package com.proch.practicehub;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class DroneActivity extends Activity {

  private static final int NUM_NOTES = 12;
  private Button[] mNoteButtons = new Button[NUM_NOTES];
  private Drone[] mDrones = new Drone[12];
  private SharedPreferences mPreferences;
  private PowerManager.WakeLock mWakeLock;
  private boolean mAddFifth;

  private static final SparseArray<Note> ID_TO_NOTE = new SparseArray<Note>();
  private static final SparseArray<Drone> ID_TO_DRONE = new SparseArray<Drone>();
  static {
    ID_TO_NOTE.put(R.id.a_button, Note.A);
    ID_TO_NOTE.put(R.id.b_flat_button, Note.Bb);
    ID_TO_NOTE.put(R.id.b_button, Note.B);
    ID_TO_NOTE.put(R.id.c_button, Note.C);
    ID_TO_NOTE.put(R.id.c_sharp_button, Note.Db);
    ID_TO_NOTE.put(R.id.d_button, Note.D);
    ID_TO_NOTE.put(R.id.e_flat_button, Note.Eb);
    ID_TO_NOTE.put(R.id.e_button, Note.E);
    ID_TO_NOTE.put(R.id.f_button, Note.F);
    ID_TO_NOTE.put(R.id.f_sharp_button, Note.Gb);
    ID_TO_NOTE.put(R.id.g_button, Note.G);
    ID_TO_NOTE.put(R.id.a_flat_button, Note.Ab);

    for (int i = 0; i < ID_TO_NOTE.size(); i++) {
      ID_TO_DRONE.put(ID_TO_NOTE.keyAt(i), new Drone());
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.drone);

    // Make volume button always control just the media volume
    setVolumeControlStream(AudioManager.STREAM_MUSIC);

    for (int i = 0; i < NUM_NOTES; i++) {
      final int id = ID_TO_NOTE.keyAt(i);

      mDrones[i] = ID_TO_DRONE.valueAt(i);
      mNoteButtons[i] = (Button) findViewById(id);
      updateButtonColor(mNoteButtons[i]);

      mNoteButtons[i].setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
          final int id = view.getId();
          toggleDrone(ID_TO_DRONE.get(id), ID_TO_NOTE.get(id));
          updateButtonColor(view);
        }
      });
    }

    mPreferences = getSharedPreferences("Drone", MODE_PRIVATE);
    mAddFifth = mPreferences.getBoolean("addFifth", true);

    setUpWakeLock();
    setUpFifthButton();
    setUpAllDronesOffButton();
  }

  @Override
  public void onStop() {
    super.onStop();
    saveState();
  }

  // Returns true if drone is now turned on, or false if it is now off
  public boolean toggleDrone(Drone drone, Note note) {
    if (drone.isRunning()) {
      drone.stop();

      if (mWakeLock.isHeld()) {
        mWakeLock.release();
      }
      return false;
    } else {
      drone.playNote(note);
      if (mAddFifth) {
        drone.playNoteWithFifth(note);
      }
      mWakeLock.acquire();
      return true;
    }
  }

  public void updateButtonColor(View button) {
    boolean state = ID_TO_DRONE.get(button.getId()).isRunning();
    int color = state ? getResources().getColor(R.color.button_pressed) : getResources().getColor(
        R.color.button_normal);
    button.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    button.setSelected(state);
  }

  private void setUpWakeLock() {
    // TODO: Use single wakelock between this and the metronome
    // TODO: Should only have lock while drone is actually playing
    final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DroneLock");
  }

  private void setUpFifthButton() {
    final ToggleButton fifthButton = (ToggleButton) findViewById(R.id.togglebutton);

    fifthButton.setChecked(mAddFifth);
    fifthButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        mAddFifth = fifthButton.isChecked();
        resetRunningDrones();
      }
    });
  }

  private void saveState() {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putBoolean("addFifth", mAddFifth);
    editor.commit();
  }

  private void setUpAllDronesOffButton() {
    final Button allDronesOffButton = (Button) findViewById(R.id.turn_off_all_drones);
    allDronesOffButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        turnOffAllDrones();
      }
    });
  }

  /**
   * Turn off all drones that are turned on and update button colors accordingly.
   */
  public void turnOffAllDrones() {
    for (Drone drone : mDrones) {
      drone.stop();
    }
    for (Button noteButton : mNoteButtons) {
      updateButtonColor(noteButton);
    }
  }

  /**
   * Restarts all drones that currently running, effectively resetting them.
   */
  public void resetRunningDrones() {
    for (Drone drone : mDrones) {
      if (drone.isRunning()) {
        resetDrone(drone);
      }
    }
  }

  /**
   * Reset a drone by stopping and starting it if it was running
   * 
   * @param drone Drone to reset
   */
  private void resetDrone(Drone drone) {
    if (drone.isRunning()) {
      drone.stop();
      Note noteToPlay = drone.getLastNotePlayed();

      if (mAddFifth) {
        drone.playNoteWithFifth(noteToPlay);
      }
      else {
        drone.playNote(noteToPlay);
      }
    }
  }
}
