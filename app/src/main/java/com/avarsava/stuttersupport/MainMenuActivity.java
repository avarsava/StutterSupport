package com.avarsava.stuttersupport;
;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import java.util.concurrent.Callable;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.5
 * @since   0.1
 *
 * Uses a ViewPager to scroll multiple Fragments horizontally, providing a menu. The Fragments
 * include a GameStarterMenuFragment for each game, plus a TrackerFragment to display the Tracker.
 */
public class MainMenuActivity extends FragmentActivity {
    /**
     * Important milestones in the streak count, in days.
     */
    private final List<Integer> MILESTONES = new LinkedList<>
            (Arrays.asList(1, 7, 31, 50, 75, 100));

    /**
     * Age at which social media begins to be appropriate.
     */
    private final int SOCIAL_MEDIA_AGE = 13;

    /**
     * Records whether the social media share message has been offered once already.
     */
    private boolean socialMediaOffered;

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
        socialMediaOffered = false;

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
        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_train_game_splash,
                TrainGameActivity.class, R.xml.train_game_prefs));
        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_deep_breathe_splash,
                DeepBreatheActivity.class, R.xml.deep_breathe_prefs));
        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_script_splash,
                ScriptReadingActivity.class, R.xml.script_reading_prefs));
        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_basketball_splash,
                BasketballGameActivity.class, R.xml.basketball_prefs));
        fList.add(GameStarterMenuFragment.newInstance(R.drawable.ic_thought_tracker_menu,
                ThoughtTrackerActivity.class, R.xml.thought_tracker_prefs));

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
     * Executed when game activities return a result. If a non-zero score is received, indicating a
     * successful activity, then encouragement is shown, the streak is updated, and a social media
     * prompt may be displayed to the user.
     *
     * @param requestCode ID of the request
     * @param resultCode integer indicating whether the result is a success
     * @param data The Intent associated with the result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(data == null || data.getStringExtra("activityName") == null) {
            return;
        }
        String activityName = data.getStringExtra("activityName");
        int activityPerformance = data.getIntExtra("activityPerformance", -1);
        int activityDifficulty = data.getIntExtra("activityDifficulty", 1);

        if(resultCode >= 1){
            //Show encouragement
            showEncouragement();

            //Update streak
            trackerDbHelper.addToDb(activityName, activityPerformance, activityDifficulty, streakDbHelper);
            trackerPage.refreshCalendar(trackerDbHelper);
            trackerPage.refreshStreak(trackerDbHelper, streakDbHelper);
            int currentStreak = streakDbHelper.getCurrent();

            //Show dialog if milestone is hit and social media offer hasn't been made yet
            if((MILESTONES.contains(currentStreak) || isLargeMilestone(currentStreak))
                    && socialMediaAppropriate()) {
                Dialog.showDialogWithPosListener(this, getString(R.string.milestone_header),
                        getString(R.string.milestone_prompt),
                        new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return createSocialIntent();
                            }
                        });
                socialMediaOffered = true;
            }
        }
    }

    /**
     * Determines whether it is appropriate to trigger the social media prompt.
     *
     * @return true if user is of age, social media has not been disabled, and the social media
     *          prompt has not yet been shown today. False otherwise.
     */
    private boolean socialMediaAppropriate(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int userAge = Integer.valueOf(prefs.getString("pti_userAge", "13"));
        boolean socialMediaEnabled = prefs.getBoolean("pti_socialMediaIntegration", true);

        return (userAge >= SOCIAL_MEDIA_AGE)
                && socialMediaEnabled
                && !socialMediaOffered;
    }

    /**
     * Shows a popup dialog with words of encouragement.
     */
    private void showEncouragement() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.encouragement_message));
        builder.setNeutralButton(getString(R.string.encouragement_button), null);
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
     * Creates an implicit Intent which the OS uses to send a message to any social media app
     * that the user may have on their device.
     *
     * @return true, because necessary for use as Callable
     */
    private boolean createSocialIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, generateShareMessage());
        startActivity(Intent.createChooser(shareIntent, "Share..."));
        return true;
    }

    /**
     * Generates a message, to be posted on social media, based on current streak count.
     *
     * @return message intended for social media
     */
    private String generateShareMessage() {
        int streak = streakDbHelper.getCurrent();
        if(streak == 1) return getString(R.string.firsttime_message);
        return getString(R.string.streak_pre_num_msg) + streak
                + getString(R.string.streak_post_num_msg);
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

