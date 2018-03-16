package com.avarsava.stuttersupport;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Parent class for Fragments in Thought Tracker.
 */

public class ThoughtTracker_Fragment extends Fragment {
    /**
     * Tag for debug logs
     */
    private final String TAG = "ThoughtTracker_Fragment";

    /**
     * Connects to the Thought Tracker database for information storage.
     */
    protected ThoughtDbHelper thoughtDbHelper;

    /**
     * Used for some of its utility functions.
     */
    private static TrackerCalendar trackerCalendar;

    /**
     * Internal ID of the layout XML for this Fragment.
     */
    private int layoutId;

    /**
     * Lists in each view
     */
    ListView today_thoughtList, today_commonList, past_list;

    /**
     * Calendar for summary view
     */
    MoodCalendar cal;

    /**
     * Creates a new TrackerFragment and saves database helpers as local variables.
     *
     * @param tdbh Thought Database Helper for accessing the thought database.
     * @return new instance of TrackerFragment
     */
    public static final ThoughtTracker_Fragment newInstance(int layout_id, ThoughtDbHelper tdbh){
        ThoughtTracker_Fragment f = new ThoughtTracker_Fragment();
        f.layoutId = layout_id;
        f.thoughtDbHelper = tdbh;
        return f;
    }

    /**
     * Called automatically when Fragment is created. Inflates layout, draws background.
     *
     * @param inflater LayoutInflator to inflate the layout.
     * @param container ViewGroup containing this Fragment.
     * @param savedInstanceState Used for Android internal communication.
     * @return new View to populate Fragment with.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(layoutId, container, false);

        switch(layoutId){
            case R.layout.fragment_thought_tracker_today:
                TextView dateDisplay = (TextView)rootView.findViewById(R.id.date);
                dateDisplay.setText(DbDate.getDayMonthAndYear(getActivity()));

                today_thoughtList = (ListView)rootView.findViewById(R.id.todaysThoughts);
                today_commonList = (ListView)rootView.findViewById(R.id.commonThoughts);
                ArrayList<ThoughtDbHelper.DBEntry> tlist =
                        new ArrayList<>(Arrays.asList(thoughtDbHelper.getTodaysThoughts()));
                ArrayList<String> clist =
                        new ArrayList<>(thoughtDbHelper.mostCommonThoughts());
                today_thoughtList.setAdapter(new ThoughtListAdapter(getActivity(),
                        tlist));
                today_commonList.setAdapter(new CommonListAdapter(getActivity(),
                        clist));
                today_commonList.setOnItemClickListener(new CommonThoughtsClickListener());
                break;

            case R.layout.fragment_thought_tracker_past:
                ArrayList<ThoughtDbHelper.DBEntry> list = new ArrayList<>(
                        thoughtDbHelper.lastThirtyDaysThoughts());
                past_list = (ListView)rootView.findViewById(R.id.past_list);
                past_list.setAdapter(new ThoughtListAdapter(getActivity(), list));
                break;

            case R.layout.fragment_thought_tracker_summary:
                cal = (MoodCalendar)rootView.findViewById(R.id.mood_calendar);
                cal.updateCalendar();
                break;
        }

        return rootView;
    }



    public void onClick(View view){
        //TODO: Handle Summary view's buttons?
        switch(view.getId()){
            case R.id.submitThought:
                String newThought = ((EditText)getActivity().findViewById(R.id.thoughtInput))
                        .getText()
                        .toString();
                MOOD newMood = MOOD.valueOf(
                        ((Spinner)(getActivity().findViewById(R.id.moodSelect)))
                        .getSelectedItem()
                        .toString());

                // This is the strangest syntax I've ever seen,
                // But apparently this is how this is done.
                ThoughtDbHelper.DBEntry newEntry = thoughtDbHelper.new DBEntry(
                        newThought, newMood.toString());
                thoughtDbHelper.addToDb(newEntry);
                ((ThoughtListAdapter)today_thoughtList.getAdapter()).add(newEntry);

                if(cal != null){
                    cal.updateCalendar();
                }
                break;

        }
    }

    private class ThoughtListAdapter extends ArrayAdapter<ThoughtDbHelper.DBEntry>{

        public ThoughtListAdapter(Context context, ArrayList<ThoughtDbHelper.DBEntry> list){
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ThoughtDbHelper.DBEntry entry = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_thought,
                        parent, false);
            }

            TextView dateView = (TextView)convertView.findViewById(R.id.date);
            TextView thoughtView = (TextView)convertView.findViewById(R.id.thought);
            TextView moodView = (TextView)convertView.findViewById(R.id.mood);
            dateView.setText(entry.getDate());
            thoughtView.setText(entry.getThought());
            moodView.setText(entry.getMood());

            return convertView;
        }
    }

    /**
     * Not even sure this is truly necessary, but here ya go
     */
    private class CommonListAdapter extends ArrayAdapter<String>{
        public CommonListAdapter(Context context, ArrayList<String> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            String entry = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_thought,
                        parent, false);
            }
            TextView thoughtView = (TextView)convertView.findViewById(R.id.thought);
            thoughtView.setText(entry);

            return convertView;
        }
    }

    private class CommonThoughtsClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String value = (String)adapterView.getItemAtPosition(position);
            EditText field = (EditText)getActivity().findViewById(R.id.thoughtInput);
            field.setText(value);
        }
    }
}