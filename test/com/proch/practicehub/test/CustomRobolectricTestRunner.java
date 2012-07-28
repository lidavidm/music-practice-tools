package com.proch.practicehub.test;

import java.lang.reflect.Method;

import org.junit.runners.model.InitializationError;

import android.os.Build;

import com.proch.practicehub.test.shadow.ShadowSherlockActivity;
import com.proch.practicehub.test.shadow.ShadowSherlockFragmentActivity;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;


public class CustomRobolectricTestRunner extends RobolectricTestRunner {

  public CustomRobolectricTestRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
    addClassOrPackageToInstrument("com.actionbarsherlock.app.SherlockActivity");
    addClassOrPackageToInstrument("com.actionbarsherlock.app.SherlockFragmentActivity");
  }

  @Override
  protected void bindShadowClasses() {
    super.bindShadowClasses();
    Robolectric.bindShadowClass(ShadowSherlockActivity.class);
    Robolectric.bindShadowClass(ShadowSherlockFragmentActivity.class);
  }

  @Override
  public void beforeTest(final Method method) {
    final int targetSdkVersion = robolectricConfig.getSdkVersion();
    setStaticValue(Build.VERSION.class, "SDK_INT", targetSdkVersion);
  }

  @Override
  public void afterTest(final Method method) {
    resetStaticState();
  }

  @Override
  public void resetStaticState() {
    // ADDED
    int SDK_INT = 16;
    setStaticValue(Build.VERSION.class, "SDK_INT", SDK_INT);
  }
}
