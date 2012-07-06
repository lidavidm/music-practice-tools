package com.proch.practicehub;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class DroneScreen extends Activity {

	private Button[] mNoteButtons = new Button[12];
	private Note[] mNotes = new Note[12];
	private SharedPreferences mPreferences;
	private PowerManager.WakeLock mWakeLock;
	private boolean mAddFifth;

	private enum Note {
		A(0, R.id.a_button),
		Bb(1, R.id.b_flat_button),
		B(2, R.id.b_button),
		C(3, R.id.c_button),
		Db(4, R.id.c_sharp_button),
		D(5, R.id.d_button),
		Eb(6, R.id.e_flat_button),
		E(7, R.id.e_button),
		F(8, R.id.f_button),
		Gb(-3, R.id.f_sharp_button),
		G(-2, R.id.g_button),
		Ab(-1, R.id.a_flat_button);

		private static final Map<Integer, Note> idToNote = new HashMap<Integer, Note>();
		static {
			for (Note note : values()) {
				idToNote.put(note.getButtonId(), note);
			}
		}
		private final double frequency;
		private final int buttonId;
		private final Drone drone;

		private Note(int halfStepsFromA440, int buttonId) {
			this.frequency = 440.0 * Math.pow(2.0, halfStepsFromA440 / 12.0);
			this.buttonId = buttonId;
			this.drone = new Drone();
		}

		public int getButtonId() {
			return buttonId;
		}

		public void playDrone() {
			drone.playPitch(frequency);
		}

		public void playDroneWithFifth() {
			playDrone();
			drone.playPitch(frequency * 1.5); // Plays fifth above fundamental
		}

		public void stopDrone() {
			drone.stop();
		}

		public boolean isDronePlaying() {
			return drone.isRunning();
		}

		public static Note fromId(int id) {
			return idToNote.get(id);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drone);

		// Make volume button always control just the media volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		final int[] buttonIDs = { R.id.a_button, R.id.b_flat_button, R.id.b_button, R.id.c_button,
				R.id.c_sharp_button, R.id.d_button, R.id.e_flat_button, R.id.e_button, R.id.f_button,
				R.id.f_sharp_button, R.id.g_button, R.id.a_flat_button };

		for (int i = 0; i < buttonIDs.length; i++) {
			mNoteButtons[i] = (Button) findViewById(buttonIDs[i]);
			updateButtonColor(mNoteButtons[i]);
			mNotes[i] = Note.fromId(buttonIDs[i]);
			mNoteButtons[i].setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					toggleDrone(Note.fromId(view.getId()));
					updateButtonColor(view);
				}
			});
		}

		mPreferences = getSharedPreferences("Drone", MODE_PRIVATE);
		mAddFifth = mPreferences.getBoolean("addFifth", true);

		setUpWakeLock();
		setUpFifthButton();
		setUpAllOffButton();
	}

	@Override
	public void onStop() {
		super.onStop();
		saveState();
	}

	// Returns true if drone is now turned on, or false if it is now off
	public boolean toggleDrone(Note note) {
		if (note.isDronePlaying()) {
			note.stopDrone();

			if (mWakeLock.isHeld()) {
				mWakeLock.release();
			}
			return false;
		} else {
			note.playDrone();
			if (mAddFifth) {
				note.playDroneWithFifth();
			}
			mWakeLock.acquire();
			return true;
		}
	}

	public void updateButtonColor(View button) {
		boolean state = Note.fromId(button.getId()).isDronePlaying();
		int color = state ? getResources().getColor(R.color.button_pressed) : getResources().getColor(
				R.color.button_normal);
		button.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
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

	private void setUpAllOffButton() {
		final Button allOffButton = (Button) findViewById(R.id.allOffButton);
		allOffButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				turnOffAllDrones();
			}
		});
	}

	public void turnOffAllDrones() {
		for (Note note : mNotes) {
			if (note.isDronePlaying()) {
				note.stopDrone();
			}
		}
		for (Button noteButton : mNoteButtons) {
			updateButtonColor(noteButton);
		}
	}

	/*
	 * Restarts all drones that currently running, effectively resetting them.
	 */
	public boolean resetRunningDrones() {
		for (Note note : mNotes) {
			if (note.isDronePlaying()) {
				resetDrone(note);
			}
		}
		return false;
	}

	/*
	 * Reset a drone by stopping and starting it if it was running.
	 */
	private void resetDrone(Note note) {
		if (note.isDronePlaying()) {
			note.stopDrone();
			note.playDrone();
			if (mAddFifth) {
				note.playDroneWithFifth();
			}
		}
	}
}
