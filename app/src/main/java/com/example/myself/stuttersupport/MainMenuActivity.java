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

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Uses a ViewPager to scroll multiple Fragments horizontally, providing a menu. The Fragments
 * include a GameStarterMenuFragment for each game, plus a TrackerFragment to display the Tracker.
 */
public class MainMenuActivity extends FragmentActivity {
    /**
     * Important milestones in the streak count, in days.
     */
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

    /**
     * Fragment which displays the Tracker calendar and Streak counter.
     */
    private TrackerFragment trackerPage;

    /**
     * Creates new Activity and displays the main menu layout. Initializes the tracker and streak
     * database helpers and the tracker fragment. Creates a new ViewPager and PagerAdapter to
     * handle scrolling Fragments.
     *
     * @param savedInstanceState Used for internal Android communication
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Get the Tracker & Streak DB Helpers
        trackerDbHelper = new TrackerDbHelper(this);
        streakDbHelper = new StreakDbHelper(this);
        trackerPage = TrackerFragment.newInstance(trackerDbHelper, streakDbHelper);

        //Instantiate a ViewPager and a PagerAdapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getFragments());
        mPager.setAdapter(mPagerAdapter);
    }

    /**
     * Creates a list of GameStarterMenuFragments and one TrackerFragment which can be scrolled
     * through on the menu.
     *
     * @return List of Fragments in menu
     */
    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<>();

        fList.add(trackerPage);
        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_temp_menu,
                CarGameActivity.class, R.xml.car_game_prefs));
        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_train_game_splash,
                TrainGameActivity.class, R.xml.train_game_prefs));
        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_deep_breathe_splash,
                DeepBreatheActivity.class, R.xml.deep_breathe_prefs));

        return fList;
    }

    /**
     * Handles clicking on both the game start and settings buttons. Starts game if game start
     * button is pressed, opens game settings if settings button is pressed.
     *
     * @param view View on which click took place.
     */
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

    /**
     * Executed when game activities return a result. If RESULT_OK is received, indicating a
     * successful activity, then encouragement is shown, the streak is updated, and a social media
     * prompt may be displayed to the user.
     *
     * @param requestCode ID of the request
     * @param resultCode integer indicating whether the result is a success
     * @param data The Intent associated with the result
     */
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

    /**
     * Shows a popup dialog with words of encouragement.
     */
    private void showEncouragement() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You're doing great! Keep up the good work!");
        builder.setNeutralButton("Thanks!", null);
        builder.show();
    }

    /**
     * Calculates whether streak counts as a large milestone value.
     *
     * Streak counts as large if it is larger than 100 and is divisible by 50.
     *
     * @param currentStreak Current streak count
     * @return true if large milestone, false otherwise
     */
    private boolean isLargeMilestone(int currentStreak) {
        return (currentStreak > 100) && (currentStreak % 50 == 0);
    }

    /**
     * Shows a dialog with a message, and 'OK' and 'Cancel' buttons. If the user presses 'OK',
     * creates an intent which the OS uses to post a message to whatever social media app installed
     * on the device the user chooses.
     *
     * based on https://stackoverflow.com/questions/8227820/alert-dialog-two-buttons
     * @param activity This activity
     * @param title Title for the dialog
     * @param message Message to display on dialog
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

    /**
     * Creates an implicit Intent which the OS uses to send a message to any social media app
     * that the user may have on their device.
     */
    private void createSocialIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, generateShareMessage());
        startActivity(Intent.createChooser(shareIntent, "Share..."));
    }

    /**
     * Generates a message, to be posted on social media, based on current streak count.
     *
     * @return message intended for social media
     */
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
        /**
         * List of fragments which will be paged through on menu
         */
        private List<Fragment> fragments;

        /**
         * Constructs a new screen slide menu
         * @param fm Fragment Manager to manage fragments
         * @param frags list of fragments to be displayed in menu
         */
        public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> frags){
            super(fm);
            fragments = frags;
        }

        /**
         * Gets the Fragment at a specific position in the list
         *
         * @param position position of the Fragment to be returned
         * @return Fragment at position
         */
        @Override
        public Fragment getItem(int position){
            return fragments.get(position);
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
    }
}

