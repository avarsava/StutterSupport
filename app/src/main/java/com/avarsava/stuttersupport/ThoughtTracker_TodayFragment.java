package com.avarsava.stuttersupport;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Allows the user to input thoughts and see today's thoughts already entered.
 * Part of the Thought Tracker activity.
 */

public class ThoughtTracker_TodayFragment extends ThoughtTracker_Fragment {

    /**
     * Defines actions for button presses on this Fragment.
     * @param view Android uses this to convey information about the current view.
     */
    public void buttonClick(View view){
        String newThought = ((EditText)view.findViewById(R.id.thoughtInput)).getText().toString();
        MOOD newMood = MOOD.valueOf(
                ((Spinner)view.findViewById(R.id.moodSelect)).getSelectedItem().toString());

        thoughtDbHelper.addToDb(newThought, newMood);
    }
}
