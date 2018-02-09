package com.avarsava.stuttersupport;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
     * Preferences file, for getting timing information
     */
    private SharedPreferences prefs;

    /**
     * To eliminate asking for it in every method
     */
    private Context context;

    /**
     * Creates a new NotificationRegistrator.
     *
     * @param overrideService Whether the alarm should always be created anew.
     * @param cxt Context of caller
     */
    public NotificationRegistrator(boolean overrideService, Context cxt) {
        this.overrideService = overrideService;
        this.context = cxt;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Registers alarm to go off at 4PM every day with Android OS. When alarm sets off,
     * alarm is heard by IntentHandler and a notification is created.
     *
     * TODO: Make so it pulls the time
     */
    public void register()  {
        int hour, minute;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        hour = Integer.valueOf(prefs.getString("notificationCustomHour", "16"));
        minute = Integer.valueOf(prefs.getString("notificationCustomMinute", "00"));

        if (calendar.get(Calendar.HOUR_OF_DAY) >= hour) calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.SECOND, 2);

        Log.d(TAG, "Setting alarm to go off at " + calendar.getTime());
        Log.d(TAG, "Alarm time in milliseconds: " + calendar.getTimeInMillis());
        Log.d(TAG, "System time in milliseconds: " + System.currentTimeMillis());

        Intent intentToLaunch = new Intent(context, IntentHandler.class).addFlags(
            Intent.FLAG_DEBUG_LOG_RESOLUTION | Intent.FLAG_FROM_BACKGROUND
        );

        if (shouldRegisterAlarm()) {
            PendingIntent pendingIntent = PendingIntent.getService(
                    context, 40, intentToLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager =
                    (AlarmManager)context.getSystemService(MainActivity.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * Whether a new alarm should be registered with Android.
     * @return true if new alarm should be registered.
     */
    private boolean shouldRegisterAlarm() {
        if (overrideService) return true;

        return alarmExists();
    }

    public void deleteAlarm(){
        //TODO: Implement deleteAlarm()
    }

    public boolean alarmExists(){
        Intent intentToLaunch = new Intent(context, IntentHandler.class).addFlags(
                Intent.FLAG_DEBUG_LOG_RESOLUTION | Intent.FLAG_FROM_BACKGROUND
        );

        //getService with FLAG_NO_CREATE returns null if it cannot find an existing alarm
        return PendingIntent.getService(context, 40, intentToLaunch,
                PendingIntent.FLAG_NO_CREATE) == null;
    }
}
