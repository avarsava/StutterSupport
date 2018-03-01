package com.avarsava.stuttersupport;

import android.support.v4.app.Fragment;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Parent class for Fragments in Thought Tracker.
 */

public class ThoughtTracker_Fragment extends Fragment {
    private ThoughtDbHelper thoughtDbHelper;
    /**
     * Creates a new TrackerFragment and saves database helpers as local variables.
     *
     * @param tdbh Thought Database Helper for accessing the thought database.
     * @return new instance of TrackerFragment
     */
    public static final ThoughtTracker_Fragment newInstance(ThoughtDbHelper tdbh){
        ThoughtTracker_Fragment f = new ThoughtTracker_Fragment();
        f.thoughtDbHelper = tdbh;
        return f;
    }
}
