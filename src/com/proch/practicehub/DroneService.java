package com.proch.practicehub;

import java.util.ArrayList;
import java.util.HashMap;

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
  private boolean mHasNotificationUp;
  private boolean mAddFifth;
  private final ArrayList<Drone> mDrones;
  private final HashMap<Note, Drone> mNotesToDrones;
  private static final int DRONE_NOTIFICATION_ID = 2;
  private static DroneService instance = null;

  public class DroneBinder extends Binder {
    DroneService getService() {
      return DroneService.this;
    }
  }

  public DroneService() {
    mDrones = new ArrayList<Drone>();
    mNotesToDrones = new HashMap<Note, Drone>();

    for (Note note : Note.values()) {
      Drone newDrone = new Drone();
      mDrones.add(newDrone);
      mNotesToDrones.put(note, newDrone);
    }
  }

  @Override
  public void onCreate() {
    instance = this;
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
    stopPlayingAllNotes();
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

  public static boolean isRunning() {
    return (instance != null && instance.isPlayingSomething());
  }

  public boolean hasNotificationUp() {
    return mHasNotificationUp;
  }

  /**
   * Update to the new value of mAddFifth and update all drones.
   * 
   * @param newValue true if we are now adding fifths above to all notes, or false if not
   */
  public void setAddFifth(boolean newValue) {
    mAddFifth = newValue;
    for (Drone drone : mDrones) {
      drone.setAddFifth(mAddFifth);
    }
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
    mHasNotificationUp = true;
  }

  /**
   * Removes the notification and updates bookkeeping.
   */
  public void stopNotification() {
    stopForeground(true);
    mHasNotificationUp = false;
  }

  public boolean isPlayingNote(Note note) {
    return getDrone(note).isRunning();
  }

  /**
   * Starts a drone playing the given note, if it is not already.
   * 
   * @param note Note to play.
   */
  public void startPlayingNote(Note note) {
    if (!isPlayingNote(note)) {
      mWakeLock.acquire();

      if (mAddFifth) {
        getDrone(note).playNoteWithFifth(note);
      }
      else {
        getDrone(note).playNote(note);
      }
    }
  }

  /**
   * Stops the drone for the given note, if it is playing.
   * 
   * @param note Note to stop playing.
   */
  public void stopPlayingNote(Note note) {
    if (isPlayingNote(note)) {
      getDrone(note).stop();
      releaseLockIfNecessary();
    }
  }

  /**
   * Starts playing note if it wasn't playing, or stops it if it was playing.
   * 
   * @param note Note to toggle playing
   * @return true if the note was turned on and is now playing, or false if it was turned off
   */
  public boolean togglePlayingNote(Note note) {
    if (isPlayingNote(note)) {
      stopPlayingNote(note);
      return false;
    }
    else {
      startPlayingNote(note);
      return true;
    }
  }

  /**
   * Returns true if at least one note is playing.
   */
  public boolean isPlayingSomething() {
    for (Drone drone : mDrones) {
      if (drone.isRunning()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Stops any running drones.
   */
  public void stopPlayingAllNotes() {
    for (Drone drone : mDrones) {
      drone.stop();
    }
    releaseLockIfNecessary();
  }

  /**
   * Returns the drone associated with the given note.
   */
  private Drone getDrone(Note note) {
    return mNotesToDrones.get(note);
  }

  /**
   * If a wake lock is no longer needed, release it.
   */
  private void releaseLockIfNecessary() {
    if (mWakeLock.isHeld() && !isPlayingSomething()) {
      mWakeLock.release();
    }
  }
  
  /**
   * Shuts down the service by stopping any playing notes, stopping the notification, and will
   * destroy the service as long as no activity is still bound to it.
   */
  private void shutdownService() {
    stopPlayingAllNotes();
    stopNotification();
    stopSelf();
  }
}
