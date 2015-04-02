package com.proch.practicehub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.proch.practicehub.VolumeMixerDialog.VolumeControlDialogListener;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements VolumeControlDialogListener {

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
        bar.setDisplayShowHomeEnabled(false);
        bar.setDisplayShowTitleEnabled(false);

        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(MetronomeFragment.class, "Metronome", null);
        mTabsAdapter.addTab(DroneFragment.class, "Drone", null);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_volume:
                showVolumeControlDialog();
                return true;
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
    }

    private void showVolumeControlDialog() {
        FragmentManager fm = getSupportFragmentManager();
        VolumeMixerDialog volumeMixerDialog = new VolumeMixerDialog();
        volumeMixerDialog.show(fm, "fragment_edit_name");
    }

    //@Override
    public void onFinishEditDialog(String inputText) {
        Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
    }

    public static class TabsAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        public TabsAdapter(ActionBarActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActionBar = activity.getSupportActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(Class<?> klass, String title, Bundle args) {
            TabInfo info = new TabInfo(klass, title, args);
            mTabs.add(info);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(),
                    info.args);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).title;
        }

        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {

        }

        public void onPageScrollStateChanged(int state) {
        }

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;
            private final String title;

            TabInfo(Class<?> _class, String _title, Bundle _args) {
                clss = _class;
                args = _args;
                title = _title;
            }
        }
    }

}
