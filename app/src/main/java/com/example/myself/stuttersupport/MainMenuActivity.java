package com.example.myself.stuttersupport;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainMenuActivity extends FragmentActivity {
    /**
     * Handles animation, allows swiping
     */
    private ViewPager mPager;

    /**
     * Provides pages to view pager widget
     */
    private PagerAdapter mPagerAdapter;

    /**
     * Tracks completed Activities
     */
    protected TrackerDbHelper trackerDbHelper;

    /**
     * Tracks historical Activity completion
     */
    protected StreakDbHelper streakDbHelper;

    private TrackerFragment trackerPage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Get the Tracker & Streak DB Helpers
        trackerDbHelper = new TrackerDbHelper(this);
        streakDbHelper = new StreakDbHelper(this);

        //Instantiate a ViewPager and a PagerAdapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getFragments());
        mPager.setAdapter(mPagerAdapter);
    }

    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<>();

        fList.add(SettingsScreenFragment.newInstance("Settings"));
        fList.add(GameStarterMenuFragment.newInstance("Car Game", CarGameActivity.class));
        fList.add(GameStarterMenuFragment.newInstance("Train Game", TrainGameActivity.class));
        fList.add(GameStarterMenuFragment.newInstance("Deep Breathe", DeepBreatheActivity.class));
        fList.add(trackerPage = TrackerFragment.newInstance(trackerDbHelper, streakDbHelper));

        return fList;
    }

    public void buttonClick(View view){
        //Get the fragment currently on screen so we know which game to launch
        GameStarterMenuFragment currentFragment =
                (GameStarterMenuFragment) mPagerAdapter.instantiateItem(mPager,
                        mPager.getCurrentItem());
        Intent intent = new Intent(this, currentFragment.getAttachedClass());
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            trackerDbHelper.addDateToDb(streakDbHelper);
            trackerPage.refreshCalendar(trackerDbHelper);
            trackerPage.refreshStreak(trackerDbHelper, streakDbHelper);
        }
    }



    /**
     * Pager adapter that represents 5 SettingsScreenFragment objects,
     * in sequence
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> frags){
            super(fm);
            fragments = frags;
        }

        @Override
        public Fragment getItem(int position){
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}

