package com.proch.practicetools;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class MainScreen extends TabActivity {

	private TabHost mTabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		setupTab(new TextView(this), "Metronome", MetronomeScreen.class);
		setupTab(new TextView(this), "Tuner", TunerScreen.class);
		setupTab(new TextView(this), "Drone", DroneScreen.class);
	}

	private void setupTab(final View view, final String tag, Class<?> cls) {
		View tabview = createTabView(mTabHost.getContext(), tag);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(
				new Intent().setClass(this, cls));
		mTabHost.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.main);
	//
	// TabHost tabHost = getTabHost(); // The activity TabHost
	// TabHost.TabSpec spec; // Reusable TabSpec for each tab
	// Intent intent; // Reusable Intent for each tab
	//
	// // Create an Intent to launch an Activity for the tab (to be reused)
	// intent = new Intent().setClass(this, MetronomeScreen.class);
	//
	// // Initialize a TabSpec for each tab and add it to the TabHost
	// spec =
	// tabHost.newTabSpec("metronome").setIndicator(getTabText("Metronome")).setContent(intent);
	// tabHost.addTab(spec);
	//
	// intent = new Intent().setClass(this, TunerScreen.class);
	// spec =
	// tabHost.newTabSpec("tuner").setIndicator(getTabText("Tuner")).setContent(intent);
	// tabHost.addTab(spec);
	//
	// intent = new Intent().setClass(this, DroneScreen.class);
	// spec =
	// tabHost.newTabSpec("drone").setIndicator(getTabText("Drone")).setContent(intent);
	// tabHost.addTab(spec);
	//
	// tabHost.setCurrentTab(0);
	// }
	//	
	// private TextView getTabText(String title) {
	// TextView textView = new TextView(this);
	// textView.setText(title);
	// textView.setGravity(android.view.Gravity.CENTER);
	// textView.setTextSize(18.0f);
	// return textView;
	// }
}
