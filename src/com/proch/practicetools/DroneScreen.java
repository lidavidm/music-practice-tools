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

	// private static final double[] FREQUENCIES = { 110.00, 116.54, 123.47,
	// 130.81, 138.59, 146.83,
	// 155.56, 164.81, 174.61, 77.78, 82.41, 87.31, 92.50, 98.00, 103.83 };

	private enum Note {
		A(440.000, R.id.a_button),
		Bb(466.164, R.id.b_flat_button),
		B(493.883, R.id.b_button),
		C(523.251, R.id.c_button),
		Db(554.365, R.id.c_sharp_button),
		D(587.330, R.id.d_button),
		Eb(622.254, R.id.e_flat_button),
		E(659.255, R.id.e_button),
		F(698.456, R.id.f_button),
		Gb(739.989, R.id.f_sharp_button),
		G(783.991, R.id.g_button),
		Ab(830.69, R.id.a_flat_button);

		private static final Map<Integer, Note> idToNote = new HashMap<Integer, Note>();
		static {
			for (Note note : values()) {
				idToNote.put(note.getButtonId(), note);
			}
		}
		private final double frequency;
		private final int buttonId;
		private final Drone drone;

		private Note(double frequency, int buttonId) {
			this.frequency = frequency;
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
			note.getDrone().playPitch(note.getFrequency() * 1.5);
			return true;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
