package com.proch.practicetools;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MainScreen extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, MetronomeScreen.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("metronome")
				.setIndicator("Metronome",res.getDrawable(R.drawable.ic_tab_artists_grey))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, TunerScreen.class);
		spec = tabHost.newTabSpec("tuner")
				.setIndicator("Tuner",res.getDrawable(R.drawable.ic_tab_artists_grey))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, DroneScreen.class);
		spec = tabHost.newTabSpec("drone")
				.setIndicator("Drone",res.getDrawable(R.drawable.ic_tab_artists_grey))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}
}
