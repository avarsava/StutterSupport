package com.avarsava.stuttersupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.concurrent.Callable;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Helper class for displaying dialogs to the screen.
 */

public class Dialog {

    /**
     * Shows a dialog with a message, and 'OK' and 'Cancel' buttons. If the user presses 'OK',
     * restores the default settings for all activities in the app.
     *
     * based on https://stackoverflow.com/questions/8227820/alert-dialog-two-buttons
     * @param activity This activity
     * @param title Title for the dialog
     * @param message Message to display on dialog
     */
    public static void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    /**
     * Shows a dialog with a message, and 'OK' and 'Cancel' buttons. If the user presses 'OK',
     * restores the default settings for all activities in the app.
     *
     *
     * based on https://stackoverflow.com/questions/8227820/alert-dialog-two-buttons
     * @param activity This activity
     * @param title Title for the dialog
     * @param message Message to display on dialog
     * @param listenerFunc Method to call on press of OK button.
     *                     boolean only because it won't accept void
     */
    public static void showDialogWithPosListener
    (Activity activity, String title, CharSequence message, final Callable<Boolean> listenerFunc ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    listenerFunc.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("OK", positiveListener);
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
