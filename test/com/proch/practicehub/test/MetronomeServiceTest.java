package com.proch.practicehub.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.content.Intent;

import com.proch.practicehub.MetronomeService;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MetronomeServiceTest {

  private MetronomeService service;
  
  @Before
  public void setUp() throws Exception {
    service = new MetronomeService();
    //service.onCreate();
  }
  
  @Test
  public void shouldPass() throws Exception{
//    Context context = service.getApplicationContext();
//    context.startService(new Intent(context, MetronomeService.class));
//    
//    service.onStartCommand(new Intent(context, MetronomeService.class), 0, 0);
  }
}
