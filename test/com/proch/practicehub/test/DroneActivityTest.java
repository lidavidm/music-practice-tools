package com.proch.practicehub.test;

import static com.xtremelabs.robolectric.Robolectric.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.widget.Button;

import com.proch.practicehub.DroneActivity;
import com.proch.practicehub.DroneService;
import com.proch.practicehub.Note;
import com.proch.practicehub.R;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DroneActivityTest {

  private DroneActivity activity;
  private Button cButton, dButton;
  private DroneService mockService;

  @Before
  public void setUp() throws Exception {
    activity = new DroneActivity();
    activity.onCreate(null);
    
    mockService = mock(DroneService.class);
    activity.setDroneService(mockService);

    cButton = (Button) activity.findViewById(R.id.c_button);
    dButton = (Button) activity.findViewById(R.id.d_button);
  }

  @Test
  public void pressingNoteButtonShouldToggleSelectedState() throws Exception {
    assertFalse("Should be unselected to start", cButton.isSelected());

    when(mockService.isPlayingNote(Note.C)).thenReturn(true);
    clickOn(cButton); // Turn on C drone
    assertTrue("Should be selected after clicking", cButton.isSelected());
    
    when(mockService.isPlayingNote(Note.C)).thenReturn(false);
    clickOn(cButton); // Turn off C drone
    assertFalse("Should go back to unselected after clicking again", cButton.isSelected());
  }

  @Test
  public void leavingAndReturningShouldNotChangeButtonSelectedStates() throws Exception {
    when(mockService.isPlayingNote(Note.C)).thenReturn(true);
    clickOn(cButton); // Turn on C drone
    assertTrue("Should be selected after clicking", cButton.isSelected());
    assertFalse("Should not be selected with no interaction", dButton.isSelected());
    
    // Make sure state is maintained even when restarting the activity
    restartActivity();    
    assertTrue("Should still be selected after recreating activity", cButton.isSelected());
    assertFalse("Should still not be selected after recreating activity", dButton.isSelected());
    
    when(mockService.isPlayingNote(Note.C)).thenReturn(false);
    clickOn(cButton); // Turn off C drone
    when(mockService.isPlayingNote(Note.D)).thenReturn(true);
    clickOn(dButton); // Turn on D drone
    
    restartActivity();
    assertFalse("Should be unselected after recreating activity", cButton.isSelected());
    assertTrue("Should still be selected after recreating activity", dButton.isSelected());
  }
  
  /**
   * Simulates exiting and restarting the app, by destroying and creating the activity again.
   */
  private void restartActivity() {
    activity.onDestroy();
    activity.onCreate(null);
  }
}
