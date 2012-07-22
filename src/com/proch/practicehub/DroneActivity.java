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
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.proch.practicehub.DroneService.DroneBinder;

public class DroneActivity extends Activity {

  private static final int NUM_NOTES = 12;
  private Button[] mNoteButtons = new Button[NUM_NOTES];
  private SharedPreferences mPreferences;
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

      for (Button noteButton : mNoteButtons) {
        updateButtonColor(noteButton);
      }
      mDroneService.setAddFifth(mAddFifth);

      if (mDroneService.isPlayingSomething()) {
        mDroneService.stopNotification();
      }
    }

    public void onServiceDisconnected(ComponentName className) {
      mBound = false;
    }
  };

  private static final SparseArray<Note> ID_TO_NOTE = new SparseArray<Note>();
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
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.drone);

    // Make volume button always control just the media volume
    setVolumeControlStream(AudioManager.STREAM_MUSIC);

    for (int i = 0; i < NUM_NOTES; i++) {
      final int id = ID_TO_NOTE.keyAt(i);

      mNoteButtons[i] = (Button) findViewById(id);

      mNoteButtons[i].setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
          final int id = view.getId();
          toggleNote(ID_TO_NOTE.get(id));
          updateButtonColor(view);
        }
      });
    }

    mPreferences = getSharedPreferences("Drone", MODE_PRIVATE);
    mAddFifth = mPreferences.getBoolean("addFifth", true);

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

    if (mBound && mDroneService.hasNotificationUp()) {
      mDroneService.stopNotification();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    saveState();
    if (mBound && mDroneService.isPlayingSomething()) {
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

  /**
   * Starts playing the given note if it was stopped, or stops it if it was playing.
   * 
   * @param note Note to start or stop playing
   * @return true if the note is now playing, or false if was stopped
   */
  private boolean toggleNote(Note note) {
    return mDroneService.togglePlayingNote(note);
  }

  private void updateButtonColor(View button) {
    boolean state = mDroneService.isPlayingNote(ID_TO_NOTE.get(button.getId()));
    int color = state ? getResources().getColor(R.color.button_pressed) : getResources().getColor(
        R.color.button_normal);
    button.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    button.setSelected(state);
  }

  private void setUpFifthButton() {
    final ToggleButton fifthButton = (ToggleButton) findViewById(R.id.togglebutton);

    fifthButton.setChecked(mAddFifth);
    fifthButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        mAddFifth = fifthButton.isChecked();
        mDroneService.setAddFifth(mAddFifth);
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
  private void turnOffAllDrones() {
    mDroneService.stopPlayingAllNotes();
    for (Button noteButton : mNoteButtons) {
      updateButtonColor(noteButton);
    }
  }

}
