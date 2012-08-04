package com.proch.practicehub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.proch.practicehub.DroneService.DroneBinder;
import com.proch.practicehub.MetronomeService.MetronomeBinder;

public class VolumeMixerDialog extends DialogFragment {

  public interface VolumeControlDialogListener {
    void onFinishEditDialog(String inputText);
  }

  private View mView;
  private MetronomeService mMetronomeService;
  private DroneService mDroneService;

  public VolumeMixerDialog() {
    // Empty constructor required for DialogFragment
  }

  private ServiceConnection mMetronomeServiceConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      mMetronomeService = ((MetronomeBinder) service).getService();
      setUpMetronomeSeekBar();
    }

    public void onServiceDisconnected(ComponentName className) {
    }
  };

  private ServiceConnection mDroneServiceConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      mDroneService = ((DroneBinder) service).getService();
      setUpDroneSeekBar();
    }

    public void onServiceDisconnected(ComponentName className) {
    }
  };

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    mView = LayoutInflater.from(getActivity()).inflate(R.layout.volume_mixer, null);

    getActivity().getApplicationContext().bindService(
        new Intent(getActivity(), MetronomeService.class),
        mMetronomeServiceConnection,
        Context.BIND_AUTO_CREATE);

    getActivity().getApplicationContext().bindService(
        new Intent(getActivity(), DroneService.class),
        mDroneServiceConnection,
        Context.BIND_AUTO_CREATE);

    return new AlertDialog.Builder(getActivity())
        .setTitle(getResources().getString(R.string.menu_volume))
        .setView(mView)
        .setNeutralButton(getResources().getString(R.string.menu_volume_done), null)
        .create();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    getActivity().getApplicationContext().unbindService(mMetronomeServiceConnection);
    getActivity().getApplicationContext().unbindService(mDroneServiceConnection);
  }

  private void setUpMetronomeSeekBar() {
    VerticalSeekBar metronomeSeekBar = (VerticalSeekBar)
        mView.findViewById(R.id.volume_metronome_seekbar);

    float currentVolume = mMetronomeService.getVolume();
    int initialProgress = (int) (currentVolume * metronomeSeekBar.getMax());
    metronomeSeekBar.setProgress(initialProgress);
    metronomeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      public void onStopTrackingTouch(SeekBar seekBar) {
      }

      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float newVolume = (float) progress / seekBar.getMax();
        mMetronomeService.setVolume(newVolume);
      }
    });
  }
  
  private void setUpDroneSeekBar() {
    VerticalSeekBar droneSeekBar = (VerticalSeekBar)
        mView.findViewById(R.id.volume_drone_seekbar);

    float currentVolume = mDroneService.getVolume();
    int initialProgress = (int) (currentVolume * droneSeekBar.getMax());
    droneSeekBar.setProgress(initialProgress);
    droneSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      public void onStopTrackingTouch(SeekBar seekBar) {
      }

      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float newVolume = (float) progress / seekBar.getMax();
        mDroneService.setVolume(newVolume);
      }
    });
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
