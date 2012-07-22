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

public class DroneService extends Service {

  private final IBinder mBinder = new DroneBinder();
  private PowerManager.WakeLock mWakeLock;
  private boolean hasNotificationUp;
  private static final int DRONE_NOTIFICATION_ID = 2;
  private static DroneService instance = null;

  public class DroneBinder extends Binder {
    DroneService getService() {
      return DroneService.this;
    }
  }

  @Override
  public void onCreate() {
    instance = this;
    // TODO: Set 12 drones
    final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DroneLock");
    
    setUpPhoneListener();
  }
  
  /**
   * Set up phone listener to make incoming phone calls stop all running drones.
   */
  private void setUpPhoneListener() {
    PhoneStateListener phoneStateListener = new PhoneStateListener() {
      @Override
      public void onCallStateChanged(int state, String incomingNumber) {
        if (state == TelephonyManager.CALL_STATE_RINGING) {
          // TODO: Stop running drones
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
    // TODO: Stop drones
    instance = null;
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    startNotification();
    if (intent.hasExtra("Stop")) {
      // TODO: Stop drones
      stopNotification();
    }
    return START_STICKY;
  }
  
  public static boolean isRunning() { // TODO: Finish
    return (instance != null);// && instance.mMetronome.isRunning());
  }

  public boolean hasNotificationUp() {
    return hasNotificationUp;
  }
  
  @Override
  public IBinder onBind(Intent arg0) {
    return mBinder;
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
    String notificationText = "Drone playing";
    contentView.setTextViewText(R.id.custom_notification_text, notificationText);

    Intent stopDroneIntent = new Intent(this, DroneService.class).putExtra("Stop", true);
    PendingIntent stopDronePendingIntent = PendingIntent.getService(this, 0,
        stopDroneIntent, 0);
    contentView.setOnClickPendingIntent(R.id.custom_notification_stop,
        stopDronePendingIntent);

    Notification notification = new NotificationCompat.Builder(getApplicationContext())
        .setSmallIcon(R.drawable.drone_icon)
        .setContent(contentView)
        .setOngoing(true)
        .setContentIntent(pendingIntent)
        .getNotification();

    startForeground(DRONE_NOTIFICATION_ID, notification);
    hasNotificationUp = true;
  }

  public void stopNotification() {
    stopForeground(true);
    hasNotificationUp = false;
    stopSelf();
  }
  
}
