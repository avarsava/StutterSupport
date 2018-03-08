package com.avarsava.stuttersupport;

import android.content.Context;
import android.support.constraint.ConstraintLayout;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Allows the user to input thoughts and see today's thoughts already entered.
 * Part of the Thought Tracker activity.
 */

public class ThoughtTracker_TodayFragment extends ThoughtTracker_Fragment {

    protected class NewThoughtView extends ConstraintLayout{

        public NewThoughtView(Context context) {
            super(context);
        }
    }

    protected class TodaysThoughtsView extends ConstraintLayout{

        public TodaysThoughtsView(Context context) {
            super(context);
        }
    }
}
