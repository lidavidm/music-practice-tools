// TODO: Figure out how this will work with SherlockFragmentActivity.

//package com.proch.practicehub.test;
//
//import static org.hamcrest.CoreMatchers.*;
//import static org.junit.Assert.*;
//
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.view.LayoutInflater;
//import android.widget.Button;
//
//import com.proch.practicehub.MainActivity;
//import com.proch.practicehub.MetronomeFragment;
//import com.proch.practicehub.R;
//import com.xtremelabs.robolectric.RobolectricTestRunner;
//
//@RunWith(CustomRobolectricTestRunner.class)
//public class MetronomeFragmentTest {
//
//  private MainActivity activity;
//  private MetronomeFragment fragment;
//  private Button startButton;
//
//  @Before
//  public void setUp() throws Exception {
//    activity = new MainActivity();
//    //activity.onCreate(null);
//    
//    fragment = new MetronomeFragment();
//    
////    FragmentManager fragmentManager = fragment.getFragmentManager();
////    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////    fragmentTransaction.add(fragment, "");
////    fragmentTransaction.commit();
//    
//    fragment.onCreateView(LayoutInflater.from(activity), null, null);
//    fragment.onAttach(activity);
//    fragment.onActivityCreated(null);
//    fragment.onStart();
//    fragment.onResume();
//    
//    startButton = (Button) activity.findViewById(R.id.metronome_start_button);
//  }
//
//  @Ignore("Temp ")
//  @Test
//  public void pressingStartButtonShouldToggleStartAndStopTextOnButton() throws Exception {
//    assertThat((String) startButton.getText(), equalTo(fragment.getText(R.string.metronome_start)));
//
//    startButton.performClick();
//    assertThat((String) startButton.getText(), equalTo(fragment.getText(R.string.metronome_stop)));
//
//    startButton.performClick();
//    assertThat((String) startButton.getText(), equalTo(fragment.getText(R.string.metronome_start)));
//  }
//}
