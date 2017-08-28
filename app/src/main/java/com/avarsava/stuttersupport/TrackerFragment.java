package com.avarsava.stuttersupport;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Displays the TrackerCalendar and the StreakDisplayer as a Fragment in the ViewPager of the
 * Main Menu.
 */
public class TrackerFragment extends Fragment {
    /**
     * Used for accessing the tracker database.
     */
    private TrackerDbHelper trackerDbHelper;

    /**
     * Used for accessing the streak database.
     */
    private StreakDbHelper streakDbHelper;

    /**
     * Used for displaying the calendar with highlighted dates.
     */
    public static TrackerCalendar cal;

    /**
     * Used for displaying the streak information as text on screen.
     */
    public static StreakDisplayer streak;

    /**
     * Creates a new TrackerFragment and saves database helpers as local variables.
     *
     * @param tdbh Tracker Database Helper for accessing the tracker database.
     * @param sdbh Streak Database Helper for accessing the streak database.
     * @return new instance of TrackerFragment
     */
    public static final TrackerFragment newInstance(TrackerDbHelper tdbh,
                                                    StreakDbHelper sdbh){
        TrackerFragment f = new TrackerFragment();
        f.trackerDbHelper = tdbh;
        f.streakDbHelper = sdbh;
        return f;
    }

    /**
     * Called when the Fragment is created, not called explicitly.
     * Inflates the layout from XML, updates the calendar and streak displayer with information
     * from the databases.
     *
     * @param inflater Used to inflate layout from XML
     * @param container ViewGroup which contains this Fragment
     * @param savedInstanceState Bundle used for communication within Android
     * @return Completed View for this Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_tracker_menu,
                container, false);

        cal = ((TrackerCalendar)rootView.findViewById(R.id.tracker_calendar));
        streak = ((StreakDisplayer)rootView.findViewById(R.id.streak_displayer));
        refreshCalendar(trackerDbHelper);
        refreshStreak(trackerDbHelper, streakDbHelper);

        return rootView;
    }

    /**
     * Refreshes the calendar to reflect a change in dates on which an activity as been completed.
     *
     * @param tdbh Tracker Database helper used to get dates to highlight on Calendar
     */
    public void refreshCalendar(TrackerDbHelper tdbh){
        cal.updateCalendar(tdbh.getDates());
    }

    /**
     * Refreshes the streak displayer to reflect a change in the streak information.
     *
     * @param tdbh Tracker Database helper used to get dates to count as a streak
     * @param sdbh Streak Database helper used to get information about the current and best streak
     */
    public void refreshStreak(TrackerDbHelper tdbh, StreakDbHelper sdbh) {
        streak.update(tdbh, sdbh);
    }
}
