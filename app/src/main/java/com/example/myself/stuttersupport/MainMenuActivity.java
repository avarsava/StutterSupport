package com.example.myself.stuttersupport;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends FragmentActivity {
    /**
     * Handles animation, allows swiping
     */
    private ViewPager mPager;

    /**
     * Provides pages to view pager widget
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Instantiate a ViewPager and a PagerAdapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getFragments());
        mPager.setAdapter(mPagerAdapter);
    }

    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<>();

        fList.add(SettingsScreenFragment.newInstance("Settings"));
        fList.add(CarGameMenuFragment.newInstance("Car Game"));
        fList.add(TrainGameMenuFragment.newInstance("Train Game"));
        fList.add(DeepBreatheMenuFragment.newInstance("Deep Breathe"));
        fList.add(TrackerFragment.newInstance("Tracker"));

        return fList;
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

