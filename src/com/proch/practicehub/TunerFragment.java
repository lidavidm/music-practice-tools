package com.proch.practicehub;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class TunerFragment extends SherlockFragment {

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
	private Activity mActivity;
	private View mView;

	@Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
		
    mView = inflater.inflate(R.layout.tuner, container, false);
    mActivity = getActivity();
    
		mTunerCentsView = (TunerCentsView) mView.findViewById(R.id.tuner_cents_view);
		mNoteLabel = (TextView) mView.findViewById(R.id.note_label);
		mCentsLabel = (TextView) mView.findViewById(R.id.cents_label);

		// Make volume button always control just the media volume
		mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		return mView;
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
