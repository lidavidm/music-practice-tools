package com.proch.practicehub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

public class VolumeControlDialog extends DialogFragment {

  public interface VolumeControlDialogListener {
    void onFinishEditDialog(String inputText);
  }

  public VolumeControlDialog() {
    // Empty constructor required for DialogFragment
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final View view = LayoutInflater.from(getActivity()).inflate(R.layout.volume_control, null);
    VerticalSeekBar metronomeSeekBar = (VerticalSeekBar) 
        view.findViewById(R.id.volume_metronome_seekbar);
    metronomeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
      
      public void onStartTrackingTouch(SeekBar seekBar) {     
      }
      
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (MetronomeService.hasInstanceRunning()) {
          float newVolume = (float) progress / seekBar.getMax();
          MetronomeService.getInstance().setVolume(newVolume);
        }
      }
    });

    return new AlertDialog.Builder(getActivity())
        .setTitle(getResources().getString(R.string.menu_volume))
        .setView(view)
        .setNeutralButton(getResources().getString(R.string.menu_volume_done), null)
        .create();
  }

  // @Override
  // public View onCreateView(LayoutInflater inflater, ViewGroup container,
  // Bundle savedInstanceState) {
  //
  // View view = inflater.inflate(R.layout.volume_control, container);
  // // mEditText = (EditText) view.findViewById(R.id.txt_your_name);
  // getDialog().setTitle("Hello");
  //
  // // Show soft keyboard automatically
  // // mEditText.requestFocus();
  // // getDialog().getWindow().setSoftInputMode(
  // // LayoutParams.SOFT_INPUT_STATE_VISIBLE);
  // // mEditText.setOnEditorActionListener(this);
  //
  // return view;
  // }

  // @Override
  // public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
  //
  // if (EditorInfo.IME_ACTION_DONE == actionId) {
  // // Return input text to activity
  // VolumeControlDialogListener activity = (VolumeControlDialogListener) getActivity();
  // activity.onFinishEditDialog(mEditText.getText().toString());
  // this.dismiss();
  // return true;
  // }
  // return false;
  // }
}
