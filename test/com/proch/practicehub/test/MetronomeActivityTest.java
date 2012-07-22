package com.proch.practicehub.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.widget.Button;

import com.proch.practicehub.MetronomeActivity;
import com.proch.practicehub.R;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MetronomeActivityTest {

  private MetronomeActivity activity;
  private Button startButton;

  @Before
  public void setUp() throws Exception {
    activity = new MetronomeActivity();
    activity.onCreate(null);
    startButton = (Button) activity.findViewById(R.id.metronome_start_button);
  }

  @Test
  public void pressingStartButtonShouldToggleStartAndStopTextOnButton() throws Exception {
    assertThat((String) startButton.getText(), equalTo(activity.getText(R.string.metronome_start)));

    startButton.performClick();
    assertThat((String) startButton.getText(), equalTo(activity.getText(R.string.metronome_stop)));

    startButton.performClick();
    assertThat((String) startButton.getText(), equalTo(activity.getText(R.string.metronome_start)));
  }
}
