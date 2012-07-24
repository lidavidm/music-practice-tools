package com.proch.practicehub.test;

import static com.xtremelabs.robolectric.Robolectric.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.proch.practicehub.DroneActivity;
import com.proch.practicehub.DroneService;
import com.proch.practicehub.Note;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowContextWrapper;

@RunWith(RobolectricTestRunner.class)
public class DroneServiceTest {

  private DroneActivity context;
  private DroneService service;
  private Intent serviceIntent;
  private ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceDisconnected(ComponentName arg0) {
    }

    @Override
    public void onServiceConnected(ComponentName comp, IBinder b) {
    }
  };

  @Before
  public void setUp() throws Exception {
    context = new DroneActivity();
    service = new DroneService();
    service.onCreate();
    serviceIntent = new Intent(context, DroneService.class);
  }

  @Test
  public void shouldBeAbleToBindToService() throws Exception {
    assertTrue(context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE));

    ShadowContextWrapper contextShadow = shadowOf(context);
    Intent receivedIntent = contextShadow.getNextStartedService();
    assertThat(receivedIntent, equalTo(serviceIntent));
  }

  @Test
  public void shouldBeAbleToStartService() throws Exception {
    context.startService(serviceIntent);
    ShadowContextWrapper contextShadow = shadowOf(context);
    Intent receivedIntent = contextShadow.getNextStartedService();
    assertThat(receivedIntent, equalTo(serviceIntent));
  }

  @Test
  public void shouldKnowIfPlayingAGivenNote() throws Exception {
    assertFalse("Should not be playing C when started", service.isPlayingNote(Note.C));
    assertFalse("Should not be playing D when started", service.isPlayingNote(Note.D));

    service.startPlayingNote(Note.C);
    assertTrue("Should now be playing C", service.isPlayingNote(Note.C));
    assertFalse("Should still not be playing D", service.isPlayingNote(Note.D));

    service.startPlayingNote(Note.D);
    assertTrue("Should still be playing C", service.isPlayingNote(Note.C));
    assertTrue("Should now be playing D", service.isPlayingNote(Note.D));

    service.stopPlayingNote(Note.C);
    assertFalse("Should not be playing C", service.isPlayingNote(Note.C));
    assertTrue("Should still be playing D", service.isPlayingNote(Note.D));

    service.stopPlayingNote(Note.D);
    assertFalse("Should not be playing C", service.isPlayingNote(Note.C));
    assertFalse("Should not be playing D", service.isPlayingNote(Note.D));
  }

  @Test
  public void shouldBeAbleToStopPlayingAllNotes() throws Exception {
    service.startPlayingNote(Note.C);
    service.startPlayingNote(Note.D);

    assertTrue(service.isPlayingNote(Note.C));
    assertTrue(service.isPlayingNote(Note.D));
    assertTrue(service.isPlayingSomething());

    service.stopPlayingAllNotes();

    assertFalse("Should have stopped playing C", service.isPlayingNote(Note.C));
    assertFalse("Should have stopped playing D", service.isPlayingNote(Note.D));
    assertFalse(service.isPlayingSomething());
  }
}
