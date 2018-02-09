package com.avarsava.stuttersupport;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Registers daily notifications with the Android OS.
 */

public class NotificationRegistrator {
    /**
     * Tag to identify logging.
     */
    final String TAG = "DailyNotification";

    /**
     * Whether or not to override the indication from the alarm service that an alarm already
     * exists.
     */
    private boolean overrideService = false;

    /**
     * Creates a new NotificationRegistrator.
     *
     * @param overrideService Whether the alarm should always be created anew.
     */
    public NotificationRegistrator(boolean overrideService) {
        this.overrideService = overrideService;
    }

    /**
     * Registers alarm to go off at 4PM every day with Android OS. When alarm sets off,
     * alarm is heard by IntentHandler and a notification is created.
     *
     * @param ctx Context of caller
     * TODO: Make so it pulls the time
     */
    public void register(Context ctx)  {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (calendar.get(Calendar.HOUR_OF_DAY) >= 16) calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.SECOND, 2);

        Log.d(TAG, "Setting alarm to go off at " + calendar.getTime());
        Log.d(TAG, "Alarm time in milliseconds: " + calendar.getTimeInMillis());
        Log.d(TAG, "System time in milliseconds: " + System.currentTimeMillis());

        Intent intentToLaunch = new Intent(ctx, IntentHandler.class).addFlags(
            Intent.FLAG_DEBUG_LOG_RESOLUTION | Intent.FLAG_FROM_BACKGROUND
        );

        if (shouldRegisterAlarm(ctx, intentToLaunch)) {
            PendingIntent pendingIntent = PendingIntent.getService(
                    ctx, 40, intentToLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager =
                    (AlarmManager)ctx.getSystemService(MainActivity.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * Whether a new alarm should be registered with Android.
     *
     * @param ctx Context of the caller.
     * @param intent Intent to create notification
     * @return true if new alarm should be registered.
     */
    private boolean shouldRegisterAlarm(Context ctx, Intent intent) {
        if (overrideService) return true;

        //getService with FLAG_NO_CREATE returns null if it cannot find an existing alarm
        return PendingIntent.getService(ctx, 40, intent, PendingIntent.FLAG_NO_CREATE) == null;
    }

    public void deleteAlarm(){
        //TODO: Implement deleteAlarm()
    }

    public boolean alarmExists(){
        //TODO: Implement alarmExists()
        return false;
    }
}
