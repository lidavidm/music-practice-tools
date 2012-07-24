package com.proch.practicehub;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

public class MetronomeService extends Service {

  private final IBinder mBinder = new MetronomeBinder();
  private PowerManager.WakeLock mWakeLock;
  private Metronome mMetronome;
  private boolean hasNotificationUp;
  private static final int METRONOME_NOTIFICATION_ID = 1;
  private static MetronomeService instance = null;

  @Override
  public void onCreate() {
    instance = this;
    mMetronome = new Metronome(getApplicationContext());

    final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MetronomeLock");

    setUpPhoneListener();
  }

  /*
   * Make incoming phone calls stop the metronome.
   */
  private void setUpPhoneListener() {
    PhoneStateListener phoneStateListener = new PhoneStateListener() {
      @Override
      public void onCallStateChanged(int state, String incomingNumber) {
        if (state == TelephonyManager.CALL_STATE_RINGING) {
          shutdownService();
        }
        super.onCallStateChanged(state, incomingNumber);
      }
    };

    TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    if (mgr != null) {
      mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
  }

  @Override
  public void onDestroy() {
    stopMetronome();
    mMetronome.destroy();
    instance = null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    startNotification();
    if (intent.hasExtra("Stop")) {
      shutdownService();
    }
    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  public static boolean isRunning() {
    return (instance != null && instance.mMetronome.isRunning());
  }

  public boolean hasNotificationUp() {
    return hasNotificationUp;
  }
  
  public void startMetronome(int tempo, int beatsOn, int beatsOff) {
    mWakeLock.acquire();
    mMetronome.start(tempo, beatsOn, beatsOff);
  }

  public void stopMetronome() {
    mMetronome.stop();
    if (mWakeLock.isHeld()) {
      mWakeLock.release();
    }
  }

  public void updateMetronome(int tempo, int beatsOn, int beatsOff) {
    mMetronome.update(tempo, beatsOn, beatsOff);
  }

  public class MetronomeBinder extends Binder {
    MetronomeService getService() {
      return MetronomeService.this;
    }
  }

  /**
   * Starts the notification and service, by first setting up the notification with the proper icon,
   * text, and intent to open the app up upon clicking.
   */
  public void startNotification() {
    Intent notificationIntent = new Intent(this, MainScreen.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
    String notificationText = "Metronome playing: " + mMetronome.getTempo() + " bpm";
    contentView.setTextViewText(R.id.custom_notification_text, notificationText);

    Intent stopMetronomeIntent = new Intent(this, MetronomeService.class);
    stopMetronomeIntent.putExtra("Stop", true);
    PendingIntent stopMetronomePendingIntent = PendingIntent.getService(this, 0,
        stopMetronomeIntent, 0);
    contentView.setOnClickPendingIntent(R.id.custom_notification_stop,
        stopMetronomePendingIntent);

    Notification notification = new NotificationCompat.Builder(getApplicationContext())
        .setSmallIcon(R.drawable.ic_stat_metronome)
        .setContent(contentView)
        .setOngoing(true)
        .setContentIntent(pendingIntent)
        .getNotification();

    startForeground(METRONOME_NOTIFICATION_ID, notification);
    hasNotificationUp = true;
  }

  public void stopNotification() {
    stopForeground(true);
    hasNotificationUp = false;
  }
  
  /**
   * Shuts down the service by stopping the metronome if its running, stopping the notification,
   * and will destroy the service as long it is still not bound to an activity.
   */
  private void shutdownService() {
    stopMetronome();
    stopNotification();
    stopSelf();
  }
}
