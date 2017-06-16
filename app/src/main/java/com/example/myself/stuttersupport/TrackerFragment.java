package com.example.myself.stuttersupport;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;

public class TrackerFragment extends Fragment {

    public static final TrackerFragment newInstance(){
        TrackerFragment f = new TrackerFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = (View) inflater.inflate(R.layout.fragment_tracker_menu,
                container, false);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        long endOfMonth = cal.getTimeInMillis();
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        long startOfMonth = cal.getTimeInMillis();
        CalendarView calView = (CalendarView) rootView.findViewById(R.id.trackerCalendar);
        calView.setMaxDate(endOfMonth);
        calView.setMinDate(startOfMonth);
        return rootView;
    }
}
