package com.example.myself.stuttersupport;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Receives broadcast from internal alarm system to create daily notification
 */
public class AlarmReceiver extends BroadcastReceiver {
    /**
     * Tag to identify logging
     */
    String TAG = "Alarm";

    /**
     * Defines behaviour when receiving broadcast. Not called explicitly
     *
     * @param context Context of originator of broadcast message
     * @param intent Intent from originator
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Handling intent");
        if (intent != null) {
            Log.d(TAG, "Intent was not null, firing notification");
            Uri soundUri = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.reminder_text))
                    .setContentIntent(
                            PendingIntent.getActivity(context, 0, new Intent(context,
                                            MainActivity.class),
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSound(soundUri).setSmallIcon(R.drawable.ic_logo)
                    .setAutoCancel(true)
                    .build();
            NotificationManagerCompat.from(context).notify(0, notification);
            }
        }
    }
