package com.avarsava.stuttersupport;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Listens for the intent fired by the alarm, and registers a notification.
 */
public class IntentHandler extends IntentService {
    /**
     * Tag for logging identification
     */
    String TAG = "DailyNotification";

    /**
     * Creates a new IntentHandler.
     */
    public IntentHandler() { super("IntentHandler"); }

    /**
     * Creates a new IntentHandler.
     *
     * @param name Name to pass to parent IntentService
     */
    public IntentHandler(String name) { super(name); }

    /**
     * Defines behaviour for when an intent is caught. Not called explicitly.
     * Fires the daily reminder notification.
     * Registers a new notification, as Android currently cannot be trusted to handle its own
     * repeating notifications.
     *
     * @param intent Intent to be handled.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Handling a caught intent, registering new notification...");

        if (intent != null) {
            Log.d(TAG, "Intent was not null, firing notification");
            Uri soundUri = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.reminder_text))
                    .setContentIntent(
                            PendingIntent.getActivity(this, 0, new Intent(this,
                                            MainActivity.class),
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSound(soundUri).setSmallIcon(R.drawable.ic_logo)
                    .setAutoCancel(true)
                    .build();
            NotificationManagerCompat.from(this).notify(0, notification);
        }

        NotificationRegistrator register
                = new NotificationRegistrator(true, this);
        register.register();
    }
}