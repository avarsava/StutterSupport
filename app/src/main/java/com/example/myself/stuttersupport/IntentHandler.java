package com.example.myself.stuttersupport;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Listens for the intent fired by the alarm, and registers a notification.
 */
public class IntentHandler extends IntentService {
    /**
     * Tag for logging identification
     */
    String TAG = "IntentHandler";

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
     * Registers a new notification.
     *
     * @param intent Intent to be handled.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Handling a caught intent, registering notification...");

        NotificationRegistrator register = new NotificationRegistrator(true);
        register.register(this);
    }
}