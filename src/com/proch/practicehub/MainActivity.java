package com.proch.practicehub;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {

  ViewPager mViewPager;
  TabsAdapter mTabsAdapter;
  TextView tabCenter;
  TextView tabText;
  Tab mMetronomeTab, mTunerTab, mDroneTab;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mViewPager = new ViewPager(this);
    mViewPager.setId(R.id.view_pager);

    setContentView(mViewPager);
    ActionBar bar = getSupportActionBar();
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    bar.setDisplayShowHomeEnabled(false);
    bar.setDisplayShowTitleEnabled(false);

    mTabsAdapter = new TabsAdapter(this, mViewPager);

    mMetronomeTab = bar.newTab().setText("Metronome");
    mTunerTab = bar.newTab().setText("Tuner");
    mDroneTab = bar.newTab().setText("Drone");

    mTabsAdapter.addTab(mMetronomeTab, MetronomeFragment.class, null);
    mTabsAdapter.addTab(mTunerTab, TunerFragment.class, null);
    mTabsAdapter.addTab(mDroneTab, DroneFragment.class, null);

    goToTabIfSpecifiedInIntent(getIntent());
  }

  @Override
  public void onStart() {
    super.onStart();

    MetronomeService metronomeService = MetronomeService.getInstance();
    if (metronomeService != null && metronomeService.hasNotificationUp()) {
      metronomeService.stopNotification();
    }

    DroneService droneService = DroneService.getInstance();
    if (droneService != null && droneService.hasNotificationUp()) {
      droneService.stopNotification();
    }
  }

  @Override
  public void onStop() {
    super.onStop();

    if (MetronomeService.hasInstanceRunning()) {
      MetronomeService.getInstance().startNotification();
    }
    if (DroneService.hasInstanceRunning()) {
      DroneService.getInstance().startNotification();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
//    case R.id.menu_volume:
//      
//      return true;
    case R.id.menu_stop_all:
      stopAll();
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Stops the metronome and drone is either or both of them are running.
   */
  private void stopAll() {
    if (MetronomeService.hasInstanceRunning()) {
      MetronomeService.getInstance().stopMetronome();
    }
    if (DroneService.hasInstanceRunning()) {
      DroneService.getInstance().stopPlayingAllNotes();
    }
    // TODO: Update fragments UI and state
  }
  
  @Override
  public void onNewIntent(Intent intent) {
    goToTabIfSpecifiedInIntent(intent);
  }

  /**
   * Checks the intent to see if there was an extra variable passed indicating to open up the app to
   * a specific tab, and if so, opens that tab.
   * 
   * @param intent Intent that possibly carrys the extra string variable
   */
  private void goToTabIfSpecifiedInIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras != null && extras.containsKey("GOTO")) {

      String gotoActivityName = (String) extras.get("GOTO");
      if (gotoActivityName.equals("Metronome")) {
        mTabsAdapter.onTabSelected(mMetronomeTab, null);
      }
      else if (gotoActivityName.equals("Drone")) {
        mTabsAdapter.onTabSelected(mDroneTab, null);
      }
    }
  }

  public static class TabsAdapter extends FragmentPagerAdapter implements
      ActionBar.TabListener, ViewPager.OnPageChangeListener
  {
    private final Context mContext;
    private final ActionBar mActionBar;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    static final class TabInfo
    {
      private final Class<?> clss;
      private final Bundle args;

      TabInfo(Class<?> _class, Bundle _args)
      {
        clss = _class;
        args = _args;
      }
    }

    public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager)
    {
      super(activity.getSupportFragmentManager());
      mContext = activity;
      mActionBar = activity.getSupportActionBar();
      mViewPager = pager;
      mViewPager.setAdapter(this);
      mViewPager.setOnPageChangeListener(this);
    }

    public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args)
    {
      TabInfo info = new TabInfo(clss, args);
      tab.setTag(info);
      tab.setTabListener(this);
      mTabs.add(info);
      mActionBar.addTab(tab);
      notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
      return mTabs.size();
    }

    @Override
    public Fragment getItem(int position)
    {
      TabInfo info = mTabs.get(position);
      return Fragment.instantiate(mContext, info.clss.getName(),
          info.args);
    }

    public void onPageScrolled(int position, float positionOffset,
        int positionOffsetPixels)
    {
    }

    public void onPageSelected(int position)
    {
      mActionBar.setSelectedNavigationItem(position);
    }

    public void onPageScrollStateChanged(int state)
    {
    }

    public void onTabSelected(Tab tab, FragmentTransaction ft)
    {
      Object tag = tab.getTag();
      for (int i = 0; i < mTabs.size(); i++)
      {
        if (mTabs.get(i) == tag)
        {
          mViewPager.setCurrentItem(i);
        }
      }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft)
    {
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft)
    {
    }
  }

}
