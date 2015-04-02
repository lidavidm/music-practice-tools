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
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.proch.practicehub.DroneService.DroneBinder;

public class DroneFragment extends Fragment {

  private static final int NUM_NOTES = 12;
  private static final boolean ADD_FIFTH_DEFAULT = true;
  private Button[] mNoteButtons = new Button[NUM_NOTES];
  private SharedPreferences mPreferences;
  private boolean mAddFifth;
  private boolean mBound;
  private DroneService mDroneService;
  private Activity mActivity;
  private View mView;

  /**
   * Class for interacting with the main interface of the service.
   */
  private ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      DroneBinder binder = (DroneBinder) service;
      setDroneService(binder.getService());
      mBound = true;

      updateAllButtonColors();
      mDroneService.setAddFifth(mAddFifth);
      setUpServiceListener();
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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mView = inflater.inflate(R.layout.drone, container, false);
    mActivity = getActivity();

    // Make volume button always control just the media volume
    mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

    mPreferences = mActivity.getSharedPreferences("Drone", Activity.MODE_PRIVATE);
    mAddFifth = mPreferences.getBoolean("addFifth", ADD_FIFTH_DEFAULT);

    setUpNoteButtons();
    setUpFifthButton();
    setUpAllDronesOffButton();

    return mView;
  }

  @Override
  public void onStart() {
    super.onStart();

    mActivity.getApplicationContext().bindService(
        new Intent(mActivity, DroneService.class),
        mConnection,
        Context.BIND_AUTO_CREATE);
  }

  @Override
  public void onResume() {
    super.onResume();

    if (mBound) {
      updateAllButtonColors();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    saveState();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mBound) {
      if (mDroneService.isPlayingSomething()) {
        mActivity.startService(new Intent(mActivity, DroneService.class));
      }
      else {
        mActivity.stopService(new Intent(mActivity, DroneService.class));
      }

      mActivity.getApplicationContext().unbindService(mConnection);
      mBound = false;
      
      removeServiceListener();
    }
  }

  public void setDroneService(DroneService service) {
    mDroneService = service;
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
    int color = state ? getResources().getColor(R.color.button_pressed) :
        getResources().getColor(R.color.button_normal);
    button.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

    button.setSelected(state);
  }

  private void setUpNoteButtons() {
    for (int i = 0; i < NUM_NOTES; i++) {
      final int id = ID_TO_NOTE.keyAt(i);

      mNoteButtons[i] = (Button) mView.findViewById(id);

      mNoteButtons[i].setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
          final int id = view.getId();
          toggleNote(ID_TO_NOTE.get(id));
          updateButtonColor(view);
        }
      });
    }
  }

  private void setUpFifthButton() {
    final ToggleButton fifthButton = (ToggleButton) mView.findViewById(R.id.drone_toggle_fifth);

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
    final Button allDronesOffButton = (Button) mView.findViewById(R.id.turn_off_all_drones);
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

  /**
   * Updates the color of all the note buttons.
   */
  private void updateAllButtonColors() {
    for (Button noteButton : mNoteButtons) {
      updateButtonColor(noteButton);
    }
  }

  /**
   * Sets up a listener for the drone service to listen for when all drones are stopped and updates
   * the UI accordingly.
   */
  private void setUpServiceListener() {
    mDroneService
        .setOnDroneChangeListener(new DroneService.OnDroneChangeListener() {

          public void onStopAll() {
            updateAllButtonColors();
          }

        });
  }
  
  /**
   * Cancels any listener created by the above method.
   */
  private void removeServiceListener() {
    mDroneService.setOnDroneChangeListener(null);
  }
}
