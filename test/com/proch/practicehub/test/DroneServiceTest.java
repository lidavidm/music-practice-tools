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
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowContextWrapper;

@RunWith(RobolectricTestRunner.class)
public class DroneServiceTest {

  private DroneActivity context;
  private Intent serviceIntent;

  @Before
  public void setUp() throws Exception {
    context = new DroneActivity();
    serviceIntent = new Intent(context, DroneService.class);
  }

  @Test
  public void shouldBeAbleToBindToService() throws Exception {
    ServiceConnection connection = new ServiceConnection() {
      @Override
      public void onServiceDisconnected(ComponentName arg0) {
      }
      @Override
      public void onServiceConnected(ComponentName comp, IBinder b) {
      }
    };
    assertTrue(context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE));

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
}
