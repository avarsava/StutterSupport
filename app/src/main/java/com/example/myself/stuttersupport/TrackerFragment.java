package com.example.myself.stuttersupport;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TrackerFragment extends Fragment {
    private TrackerDbHelper trackerDbHelper;
    private StreakDbHelper streakDbHelper;

    public static TrackerCalendar cal;
    public static StreakDisplayer streak;

    public static final TrackerFragment newInstance(TrackerDbHelper tdbh,
                                                    StreakDbHelper sdbh){
        TrackerFragment f = new TrackerFragment();
        f.trackerDbHelper = tdbh;
        f.streakDbHelper = sdbh;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = (View) inflater.inflate(R.layout.fragment_tracker_menu,
                container, false);

        cal = ((TrackerCalendar)rootView.findViewById(R.id.tracker_calendar));
        streak = ((StreakDisplayer)rootView.findViewById(R.id.streak_displayer));
        refreshCalendar(trackerDbHelper);
        refreshStreak(trackerDbHelper, streakDbHelper);

        return rootView;
    }

    public void refreshCalendar(TrackerDbHelper tdbh){
        cal.updateCalendar(tdbh.getDates());
    }

    public void refreshStreak(TrackerDbHelper tdbh, StreakDbHelper sdbh) {
        streak.update(tdbh, sdbh);
    }
}
