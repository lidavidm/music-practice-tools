package com.proch.practicehub;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.proch.practicehub.MetronomeService.MetronomeBinder;
import com.proch.practicehub.NumberPicker.OnChangedListener;
import com.proch.practicehub.R;

public class MetronomeActivity extends Activity {
  private Button mStartStopButton;
  private boolean mRunning;
  private static final int DEFAULT_TEMPO = 120;
  private static final int DEFAULT_BEATS_ON = 1;
  private static final int DEFAULT_BEATS_OFF = 0;
  private static final int MIN_TEMPO = 10;
  private static final int MAX_TEMPO = 400;
  private int mTempo;
  private boolean mBound;
  private NumberPicker mTempoPicker;
  private NumberPicker mBeatsOnPicker;
  private NumberPicker mBeatsOffPicker;
  private int mBeatsOn;
  private int mBeatsOff;
  private SeekBar mTempoSeekBar;
  private SharedPreferences mPreferences;
  private MetronomeService mMetronomeService;
  private long mTempoTapLastTappedTime = 0;

  /**
   * Class for interacting with the main interface of the service.
   */
  private ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      // This is called when the connection with the service has been
      // established, giving us the object we can use to
      // interact with the service. We are communicating with the
      // service using a Messenger, so here we get a client-side
      // representation of that from the raw IBinder object.
      MetronomeBinder binder = (MetronomeBinder) service;
      mMetronomeService = binder.getService();
      mBound = true;
      if (mRunning) {
        mMetronomeService.stopNotification();
      }
    }

    public void onServiceDisconnected(ComponentName className) {
      // This is called when the connection with the service has been
      // unexpectedly disconnected -- that is, its process crashed.
      mBound = false;
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.metronome);

    // Make volume button always control just the media volume
    setVolumeControlStream(AudioManager.STREAM_MUSIC);

    // Load stored persistent data
    mPreferences = getSharedPreferences("Metronome", MODE_PRIVATE);
    mTempo = mPreferences.getInt("tempo", DEFAULT_TEMPO);
    mBeatsOn = mPreferences.getInt("beatsOn", DEFAULT_BEATS_ON);
    mBeatsOff = mPreferences.getInt("beatsOff", DEFAULT_BEATS_OFF);

    setUpStartStopButton();
    setUpBeatsControls();
    setUpTempoControls();
    setUpTempoTapButton();
  }

  @Override
  public void onStart() {
    super.onStart();
    getApplicationContext().bindService(new Intent(this, MetronomeService.class), mConnection,
        Context.BIND_AUTO_CREATE);
  }

  @Override
  public void onResume() {
    super.onResume();
    updateRunningState();
    if (mBound && mMetronomeService.hasNotificationUp()) {
      mMetronomeService.stopNotification();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    saveState();
    if (mBound && mRunning) {
      // Starts the notification for the already-running service
      startService(new Intent(this, MetronomeService.class));
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

  private void setUpStartStopButton() {
    mStartStopButton = (Button) findViewById(R.id.metronome_start_button);
    mStartStopButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        mRunning = !mRunning;
        if (mRunning) {
          startMetronome();
          mStartStopButton.setText(getText(R.string.metronome_stop));
        } else {
          stopMetronome();
          mStartStopButton.setText(getText(R.string.metronome_start));
        }
      }
    });
  }

  /*
   * Sets up the tempo controls with the given tempo
   */
  private void setUpTempoControls() {
    mTempoPicker = (NumberPicker) findViewById(R.id.tempo_picker);
    mTempoPicker.setSpeed(50);

    EditText tempoText = (EditText) findViewById(R.id.timepicker_input);
    tempoText.setTextSize(50);

    mTempoPicker.setRange(MIN_TEMPO, MAX_TEMPO);
    mTempoPicker.setCurrent(mTempo);
    mTempoPicker.setOnChangeListener(new OnChangedListener() {
      public void onChanged(NumberPicker picker, int oldVal, int newVal) {
        updateTempo(newVal);
      }
    });

    mTempoSeekBar = (SeekBar) findViewById(R.id.tempo_seek);
    mTempoSeekBar.setMax(MAX_TEMPO + 1);
    mTempoSeekBar.setProgress(mTempo);
    mTempoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        updateTempo(progress);
      }

      public void onStopTrackingTouch(SeekBar seekBar) {
        updateTempo(seekBar.getProgress());
      }

      public void onStartTrackingTouch(SeekBar seekBar) {
      }
    });
  }

  private void setUpBeatsControls() {
    mBeatsOnPicker = (NumberPicker) findViewById(R.id.beats_on_picker);
    mBeatsOnPicker.setRange(1, 16);
    mBeatsOnPicker.setCurrent(mBeatsOn);
    mBeatsOnPicker.setOnChangeListener(new OnChangedListener() {
      public void onChanged(NumberPicker picker, int oldVal, int newVal) {
        updateBeatsOn(newVal);
      }
    });

    mBeatsOffPicker = (NumberPicker) findViewById(R.id.beats_off_picker);
    mBeatsOffPicker.setRange(0, 16);
    mBeatsOffPicker.setCurrent(mBeatsOff);
    mBeatsOffPicker.setOnChangeListener(new OnChangedListener() {
      public void onChanged(NumberPicker picker, int oldVal, int newVal) {
        updateBeatsOff(newVal);
      }
    });
  }

  private void setUpTempoTapButton() {
    ((Button) findViewById(R.id.tempo_tap)).setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {
        double diffInSeconds = (System.currentTimeMillis() - mTempoTapLastTappedTime) / 1000.0;
        if (diffInSeconds < 3) {
          updateTempo((int) (60 / diffInSeconds));
          // mTempo = (int) (60 / diffInSeconds);
          // mTempoPicker.setCurrent(mTempo);
        }
        mTempoTapLastTappedTime = System.currentTimeMillis();
      }
    });
  }

  private void startMetronome() {
    if (mBound) {
      mMetronomeService.startMetronome(mTempo, mBeatsOn, mBeatsOff);
    }
  }

  private void stopMetronome() {
    if (mBound) {
      mMetronomeService.stopMetronome();
    }
  }

  private void updateService() {
    if (mBound && mRunning) {
      mMetronomeService.updateMetronome(mTempo, mBeatsOn, mBeatsOff);
    }
  }

  /*
   * Updates the running state of the metronome service by updating the variable and button.
   */
  private void updateRunningState() {
    mRunning = MetronomeService.isRunning();
    mStartStopButton.setText(mRunning ? getString(R.string.metronome_stop)
        : getString(R.string.metronome_start));
  }

  private void updateTempo(int tempo) {
    mTempo = tempo > MAX_TEMPO ? MAX_TEMPO : tempo;
    mTempo = mTempo < MIN_TEMPO ? MIN_TEMPO : mTempo;
    updateService();
    mTempoSeekBar.setProgress(mTempo);
    mTempoPicker.setCurrent(mTempo);
  }

  private void updateBeatsOn(int beatsOn) {
    mBeatsOn = beatsOn;
    updateService();
  }

  private void updateBeatsOff(int beatsOff) {
    mBeatsOff = beatsOff;
    updateService();
  }

  private void saveState() {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putInt("tempo", mTempo);
    editor.putInt("beatsOn", mBeatsOn);
    editor.putInt("beatsOff", mBeatsOff);

    editor.commit();
  }
}