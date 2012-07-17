package com.proch.practicehub.test;

import static com.xtremelabs.robolectric.Robolectric.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.widget.Button;

import com.proch.practicehub.DroneActivity;
import com.proch.practicehub.R;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DroneActivityTest {

  private DroneActivity activity;

  @Before
  public void setUp() throws Exception {
    activity = new DroneActivity();
    activity.onCreate(null);
  }

  @Test
  public void pressingNoteButtonShouldToggleSelectedState() throws Exception {
    final Button cButton = (Button) activity.findViewById(R.id.c_button);

    assertFalse("Should be unselected to start", cButton.isSelected());

    clickOn(cButton);
    assertTrue("Should be selcted state after clicking", cButton.isSelected());
    
    clickOn(cButton);
    assertFalse("Should go back to unselected after clicking again", cButton.isSelected());
  }

}
