package com.proch.practicehub;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.proch.practicehub.DroneService.DroneBinder;

public class DroneActivity extends Activity {

  private static final int NUM_NOTES = 12;
  private Button[] mNoteButtons = new Button[NUM_NOTES];
  private Drone[] mDrones = new Drone[12];
  private SharedPreferences mPreferences;
  private PowerManager.WakeLock mWakeLock;
  private boolean mAddFifth;
  private boolean mBound;
  private DroneService mDroneService;

  /**
   * Class for interacting with the main interface of the service.
   */
  private ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      DroneBinder binder = (DroneBinder) service;
      mDroneService = binder.getService();
      mBound = true;
      if (isRunning()) {
        mDroneService.stopNotification();
      }
    }

    public void onServiceDisconnected(ComponentName className) {
      mBound = false;
    }
  };

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

    for (int i = 0; i < NUM_NOTES; i++) {
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
  public void onStart() {
    super.onStart();
    getApplicationContext().bindService(new Intent(this, DroneService.class), mConnection,
        Context.BIND_AUTO_CREATE);
  }

  @Override
  public void onResume() {
    super.onResume();
    // TODO: Update button state
    if (mBound && mDroneService.hasNotificationUp()) {
      mDroneService.stopNotification();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    saveState();
    if (mBound && isRunning()) {
      // Starts the notification for the already-running service
      startService(new Intent(this, DroneService.class));
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mBound) {
      getApplicationContext().unbindService(mConnection);
      mBound = false;
    }
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
      if (mAddFifth) {
        drone.playNoteWithFifth(note);
      }
      else {
        drone.playNote(note);
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
    final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DroneLock");
  }

  private void setUpFifthButton() {
    final ToggleButton fifthButton = (ToggleButton) findViewById(R.id.togglebutton);

    fifthButton.setChecked(mAddFifth);
    fifthButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        mAddFifth = fifthButton.isChecked();
        updateDrones();
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
   * Updates drones by setting the addFifth variables to the most updated values for each drone.
   */
  public void updateDrones() {
    for (Drone drone : mDrones) {
      drone.setAddFifth(mAddFifth);
    }
  }

  /**
   * Returns true if any one or more of the drones are currently playing.
   */
  private boolean isRunning() {
    for (Drone drone : mDrones) {
      if (drone.isRunning()) {
        return true;
      }
    }
    return false;
  }
}
