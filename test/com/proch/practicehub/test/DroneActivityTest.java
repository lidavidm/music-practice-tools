package com.proch.practicehub.test;

import static com.xtremelabs.robolectric.Robolectric.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.widget.Button;

import com.proch.practicehub.DroneActivity;
import com.proch.practicehub.R;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DroneActivityTest {

  private DroneActivity activity;
  private Button cButton, dButton;

  @Before
  public void setUp() throws Exception {
    activity = new DroneActivity();
    activity.onCreate(null);
    cButton = (Button) activity.findViewById(R.id.c_button);
    dButton = (Button) activity.findViewById(R.id.d_button);
  }

  @Ignore("Need to fix")
  @Test
  public void pressingNoteButtonShouldToggleSelectedState() throws Exception {
    assertFalse("Should be unselected to start", cButton.isSelected());

    clickOn(cButton);
    assertTrue("Should be selcted state after clicking", cButton.isSelected());
    
    clickOn(cButton);
    assertFalse("Should go back to unselected after clicking again", cButton.isSelected());
  }

  @Ignore("Need to fix")
  @Test
  public void leavingAndReturningShouldNotChangeButtonSelectedStates() throws Exception {
    clickOn(cButton);
    assertTrue("Should be selected after clicking", cButton.isSelected());
    assertFalse("Should not be selected with no interaction", dButton.isSelected());
    
    // Set up again, to recreate activity and check selected state of buttons
    setUp();
    assertTrue("Should still be selected after recreating activity", cButton.isSelected());
    assertFalse("Should still not be selected after recreating activity", dButton.isSelected());
    
    clickOn(cButton);
    clickOn(dButton);
    setUp();
    
    assertFalse("Should be unselected after recreating activity", cButton.isSelected());
    assertTrue("Should still be selected after recreating activity", dButton.isSelected());
  }
}
