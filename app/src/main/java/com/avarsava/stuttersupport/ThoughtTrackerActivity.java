package com.avarsava.stuttersupport;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Allows the user to track their thoughts or self-talk through an interactive database frontend.
 * Users can see today's thoughts and add thoughts with attached generalized moods,
 * swipe left to see past thoughts, or swipe right to see a summary of the past 30 days.
 *
 * This activity is rooted in Cognitive Behavioural Therapy and is used to identify common patterns
 * of negative thinking in order to target and reduce that thought pattern. This helps make the
 * user proactive against anxiety- or depression-triggering thought patterns.
 */

public class ThoughtTrackerActivity extends FragmentActivity {
    /**
     * Handles animation, allows swiping
     */
    private ViewPager mPager;

    /**
     * Provides pages to view pager widget
     */
    private PagerAdapter mPagerAdapter;

    /**
     * Tracks thoughts entered by the user.
     */
    protected ThoughtDbHelper thoughtDbHelper;

    /**
     * Creates new Activity and displays the layout. Initializes the database helper
     * and puts the Fragments into the ViewPager.
     *
     * @param savedInstanceState Used for internal Android communication
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thought_tracker);
        thoughtDbHelper = new ThoughtDbHelper(this);

        mPager = (ViewPager) findViewById(R.id.thought_pager);
        mPagerAdapter =
                new ThoughtTrackerPagerAdapter(getSupportFragmentManager(),
                        getFragments());
        mPager.setAdapter(mPagerAdapter);
    }

    /**
     * Creates a List containing the 3 Fragments relevant to this Activity.
     *
     * @return ordered List of Thought Tracker Fragments
     */
    public List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();

        fList.add(ThoughtTracker_Fragment.newInstance(R.layout.fragment_thought_tracker_past,
                thoughtDbHelper));
        fList.add(ThoughtTracker_Fragment.newInstance(R.layout.fragment_thought_tracker_today,
                thoughtDbHelper));
        fList.add(ThoughtTracker_Fragment.newInstance
                (R.layout.fragment_thought_tracker_summary,
                thoughtDbHelper));

        return fList;
    }

    public void buttonClick(View view){
        ThoughtTracker_Fragment currentFragment = (ThoughtTracker_Fragment)mPagerAdapter
                .instantiateItem(mPager, mPager.getCurrentItem());

        //Handle each button
        currentFragment.onClick(view);
    }


    /**
     * Pager adapter that represents 3 Thought Tracker related Fragments
     * in sequence.
     *
     * TODO: Refactor this and ScreenSlidePagerAdapter to reduce code repetition
     */
    private class ThoughtTrackerPagerAdapter extends FragmentStatePagerAdapter {
        /**
         * List of Fragments which will be paged through
         */
        private List<Fragment> fragments;


        public ThoughtTrackerPagerAdapter(FragmentManager fm, List<Fragment> frags){
            super(fm);
            fragments = frags;
        }
        /**
         * Gets the number of Fragments in the menu
         *
         * @return number of Fragments in the menu
         */
        @Override
        public int getCount() {
            return fragments.size();
        }

        /**
         * Gets the Fragment at a specific position in the list
         *
         * @param position position of the Fragment to be returned
         * @return Fragment at position
         */
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }
}
