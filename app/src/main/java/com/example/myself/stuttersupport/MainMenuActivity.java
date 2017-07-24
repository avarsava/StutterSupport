package com.example.myself.stuttersupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainMenuActivity extends FragmentActivity {
    private final List<Integer> MILESTONES = new LinkedList<>(Arrays.asList(1, 7, 31, 50, 75, 100));
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

        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_temp_menu,
                CarGameActivity.class, R.xml.car_game_prefs));
        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_temp_menu,
                TrainGameActivity.class, R.xml.train_game_prefs));
        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_deep_breathe_splash,
                DeepBreatheActivity.class, R.xml.deep_breathe_prefs));
        fList.add(trackerPage = TrackerFragment.newInstance(trackerDbHelper, streakDbHelper));

        return fList;
    }

    public void buttonClick(View view) {
        //Get the fragment currently on screen so we know which game to launch
        GameStarterMenuFragment currentFragment =
                (GameStarterMenuFragment) mPagerAdapter.instantiateItem(mPager,
                        mPager.getCurrentItem());

        //Handle each type of button
        switch (view.getId()) {
            case R.id.startButton:
                Intent gameIntent = new Intent(this, currentFragment.getAttachedClass());
                startActivityForResult(gameIntent, 1);
                break;
            case R.id.settingsButton:
                Intent settingsIntent = new Intent(this,
                        SettingsScreenActivity.class);
                settingsIntent.putExtra("prefs", currentFragment.getAttachedSettingsFile());
                startActivity(settingsIntent);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            //Show encouragement
            showEncouragement();

            //Update streak
            int currentStreak = streakDbHelper.getCurrent();
            trackerDbHelper.addDateToDb(streakDbHelper);
            trackerPage.refreshCalendar(trackerDbHelper);
            trackerPage.refreshStreak(trackerDbHelper, streakDbHelper);

            //Show dialog if milestone is hit
            if(MILESTONES.contains(currentStreak) || isLargeMilestone(currentStreak)) {
                showDialog(this, "Congratulations!", "You've hit a new milestone! " +
                        "Want to share it with the world?");
            }
        }
    }

    private void showEncouragement() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You're doing great! Keep up the good work!");
        builder.setNeutralButton("Thanks!", null);
        builder.show();
    }

    private boolean isLargeMilestone(int currentStreak) {
        return (currentStreak > 100) && (currentStreak % 50 == 0);
    }

    /**
     * based on https://stackoverflow.com/questions/8227820/alert-dialog-two-buttons
     * @param activity
     * @param title
     * @param message
     */
    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createSocialIntent();
            }
        };

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("OK", positiveListener);
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void createSocialIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, generateShareMessage());
        startActivity(Intent.createChooser(shareIntent, "Share..."));
    }

    private String generateShareMessage() {
        int streak = streakDbHelper.getCurrent();
        if(streak == 1) return "I just started practicing my speech using Stutter Support!";
        return "I've practiced my speech for " + streak + " days using Stutter Support!";
    }

    /**
     * Pager adapter that represents 5 SettingsScreenActivity objects,
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

