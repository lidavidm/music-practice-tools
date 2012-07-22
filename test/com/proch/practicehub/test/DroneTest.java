package com.proch.practicehub.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.proch.practicehub.Drone;
import com.proch.practicehub.Note;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DroneTest {

  private Drone drone;
  
  @Before
  public void setUp() {
    drone = new Drone();
  }
  
  @Test
  public void shouldKeepTrackOfLastNotePlayed() {
    assertThat(drone.getLastNotePlayed(), equalTo(null));
    
    Note noteToPlay = Note.C;
    drone.playNote(noteToPlay);
    assertThat(drone.getLastNotePlayed(), equalTo(noteToPlay));

    Drone differentDrone = new Drone();
    
    noteToPlay = Note.D;
    drone.playNote(noteToPlay);
    assertThat(drone.getLastNotePlayed(), equalTo(noteToPlay));
    assertThat(differentDrone.getLastNotePlayed(), equalTo(null));
  }
}
