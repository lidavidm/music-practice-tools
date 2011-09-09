package com.proch.practicetools;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DroneScreen extends Activity {

	private Button[] mNoteButtons = new Button[12];

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
		
		public double getFrequency() {
			return frequency;
		}

		public int getButtonId() {
			return buttonId;
		}

		public Drone getDrone() {
			return drone;
		}

		public static Note fromId(int id) {
			return idToNote.get(id);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drone);

		final int[] buttonIDs = { R.id.a_button, R.id.b_flat_button, R.id.b_button, R.id.c_button,
				R.id.c_sharp_button, R.id.d_button, R.id.e_flat_button, R.id.e_button, R.id.f_button,
				R.id.f_sharp_button, R.id.g_button, R.id.a_flat_button };

		for (int i = 0; i < buttonIDs.length; i++) {
			mNoteButtons[i] = (Button) findViewById(buttonIDs[i]);
			mNoteButtons[i].setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					boolean newState = toggleDrone(Note.fromId(view.getId()));
					int color = newState ? getResources().getColor(R.color.button_pressed) : getResources()
							.getColor(R.color.button_normal);
					view.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
				}
			});
		}
	}

	public boolean toggleDrone(Note note) {
		if (note.getDrone().isRunning()) {
			note.getDrone().stop();
			return false;
		} else {
			note.getDrone().playPitch(note.getFrequency());
			// Play fifth above
			note.getDrone().playPitch(note.getFrequency() * 1.5);
			// Play octave above
			note.getDrone().playPitch(note.getFrequency() * 2);
			return true;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
