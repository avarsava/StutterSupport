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

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.5
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


    /**
     * Handles on-screen taps. When submitThought button is tapped by the user, get the entered
     * String and MOOD, and inputs into database. Refreshes the calendar if it has been rendered.
     *
     * @param view Button which was tapped.
     */
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

                // This is the strangest syntax I've ever seen,
                // But apparently this is how this is done when your class isn't static.
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

    /**
     * Provides a list of DBEntry objects, properly formatted into Thought and Mood views
     * in each list item.
     */
    private class ThoughtListAdapter extends ArrayAdapter<ThoughtDbHelper.DBEntry>{
        /**
         * Constructor. Just calls super, nothing fancy
         *
         * @param context Activity context
         * @param list ArrayList of DBEntry with which to populate list
         */
        public ThoughtListAdapter(Context context, ArrayList<ThoughtDbHelper.DBEntry> list){
            super(context, 0, list);
        }

        /**
         * Inflates list entry XML and creates a single item for List.
         *
         * @param position Numerical position within list of this entry
         * @param convertView View provided by OS to convert to our format
         * @param parent ViewGroup provided by OS. Unused
         * @return convertView formatted as DBEntry list item
         */
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
     * Provides a list of the most common thoughts in the database.
     * Not even sure this is truly necessary, but here ya go
     */
    private class CommonListAdapter extends ArrayAdapter<String>{
        /**
         * Constructor.
         *
         * @param context Activity context
         * @param list ArrayList of Strings representing entered Thoughts
         */
        public CommonListAdapter(Context context, ArrayList<String> list) {
            super(context, 0, list);
        }

        /**
         * Puts the thought in the list.
         *
         * @param position Numerical position within list of this entry
         * @param convertView View provided by OS to convert to our format
         * @param parent ViewGroup provided by OS. Unused
         * @return View containing a string
         */
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

    /**
     * Listener for clicks on List items in Common Thoughts
     */
    private class CommonThoughtsClickListener implements AdapterView.OnItemClickListener{

        /**
         * Handles clicks on list items. Puts the text of the selected item into the
         * Thought entry text box.
         *
         * @param adapterView self, i think.
         * @param view Item object which was tapped
         * @param position numerical position of selected item in list
         * @param l ???
         */
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String value = (String)adapterView.getItemAtPosition(position);
            EditText field = (EditText)getActivity().findViewById(R.id.thoughtInput);
            field.setText(value);
        }
    }
}
