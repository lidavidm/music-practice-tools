package com.proch.practicehub;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity {

  ViewPager mViewPager;
  TabsAdapter mTabsAdapter;
  TextView tabCenter;
  TextView tabText;

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

    mTabsAdapter.addTab(
        bar.newTab().setText("Metronome"),
        MetronomeFragment.class, null);
    mTabsAdapter.addTab(
        bar.newTab().setText("Tuner"),
        TunerFragment.class, null);
    mTabsAdapter.addTab(
        bar.newTab().setText("Drone"),
        DroneFragment.class, null);
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
