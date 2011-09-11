package com.proch.practicehub;

import com.proch.practicehub.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class TunerScreen extends Activity {

	private static final String[] NOTE_NAMES = { "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F",
			"F#", "G", "Ab" };
	private Tuner mTuner;
	private final Handler mHandler = new Handler();
	private final Runnable callback = new Runnable() {
		public void run() {
			setNoteLabel(mTuner.currentFrequency);
		}
	};

	private TextView mNoteLabel;
	private TextView mCentsLabel;
	private TunerCentsView mTunerCentsView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tuner);
		mTunerCentsView = (TunerCentsView) findViewById(R.id.tuner_cents_view);
		mNoteLabel = (TextView) findViewById(R.id.note_label);
		mCentsLabel = (TextView) findViewById(R.id.cents_label);
	}

	@Override
	public void onResume() {
		super.onResume();
		startTuner();
	}

	@Override
	public void onPause() {
		super.onPause();
		mTuner.close();
	}
	
	public void startTuner() {
		mTuner = new Tuner(mHandler, callback);
		mTuner.start();
	}

	public void setNoteLabel(double frequency) {
		double linearFreq = Math.log(frequency / 440.0) / Math.log(2) + 4;
		double octave = Math.floor(linearFreq);
		double cents = 1200 * (linearFreq - octave);
		int noteNum = (int) Math.round(cents / 100);
		cents = Math.round(cents - noteNum * 100);

		mNoteLabel.setText(NOTE_NAMES[noteNum % 12]);
		String centsStr = (int) cents + " cents";
		if (cents > 0) {
			mCentsLabel.setText("+" + centsStr);
		} else {
			mCentsLabel.setText(centsStr);
		}

		mTunerCentsView.setCentsMarker(cents);
	}
}
