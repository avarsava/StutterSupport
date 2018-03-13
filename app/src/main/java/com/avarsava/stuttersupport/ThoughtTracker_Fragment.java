package com.avarsava.stuttersupport;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Parent class for Fragments in Thought Tracker.
 */

public class ThoughtTracker_Fragment extends Fragment {
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
     * List at top of Today view
     */
    ListView today_thoughtList;

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
        View rootView = (View) inflater.inflate(layoutId, container, false);

        switch(layoutId){
            case R.layout.fragment_thought_tracker_today:
                TextView dateDisplay = (TextView)rootView.findViewById(R.id.date);
                dateDisplay.setText(DbDate.getDayMonthAndYear(getActivity()));
                today_thoughtList = (ListView)getActivity().findViewById(R.id.todaysThoughts);
                updateThoughtList();
                break;
        }

        return rootView;
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.submitThought:
                String newThought = ((EditText)getActivity().findViewById(R.id.thoughtInput))
                        .getText()
                        .toString();
                MOOD newMood = MOOD.valueOf(
                        ((Spinner)(getActivity().findViewById(R.id.moodSelect)))
                        .getSelectedItem()
                        .toString());

                thoughtDbHelper.addToDb(newThought, newMood);
                updateThoughtList();
        }
    }

    private void updateThoughtList(){
        List<ThoughtDbHelper.DBEntry> list = thoughtDbHelper.getTodaysThoughts();

        //today_thoughtList.setAdapter(new ThoughtListAdapter(getActivity(),
        //        (ThoughtDbHelper.DBEntry[])list.toArray()));
    }

    private class ThoughtListAdapter extends ArrayAdapter<ThoughtDbHelper.DBEntry>{
        ThoughtDbHelper.DBEntry[] entries;

        public ThoughtListAdapter(Context context, ThoughtDbHelper.DBEntry[] list){
            super(context, -1, -1, list);
            entries = list;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View newView = new ThoughtView(getContext(), entries[position].getThought(),
                    entries[position].getMood());
            return newView;
        }
    }
}
