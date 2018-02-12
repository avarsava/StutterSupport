package com.avarsava.stuttersupport;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Parent class for the games. Handles time-based tasks and provides a simplified interface for
 * interacting with the speech recognition engine.
 */
public abstract class GameActivity extends AppCompatActivity implements RecognitionListener {
    /**
     * Status that should be obtained for permission for the app to record audio.
     */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    /**
     * Defines time since the timer has been reset.
     */
    private long startTime;

    /**
     * How many 'cycles' of gameplay should be run before determining whether the game is
     * successful
     */
    protected int maxCycles;

    /**
     * How many 'cycles' of gameplay have been executed so far.
     */
    protected int cycleCount;

    /**
     * PocketSphinx speech recognition engine.
     */
    protected SpeechRecognizer recognizer = null;

    /**
     * Preferences pulled from game's associated settings file.
     */
    protected SharedPreferences prefs;

    /**
     * Screen to be drawn to animate the game.
     */
    protected DrawView screen;

    /**
     * Called automatically when Game Activity is created. Tells the system to keep the screen lit
     * at all times when a game is being played, this is necessary because there are no touch
     * screen events to keep the system active. Sets gameplay cycle count to 0 and ensures
     * appropriate permissions have been acquired for the speech recognition engine.
     *
     * @param savedStateInstance Internal component used for Android communications.
     */
    @Override
    protected void onCreate(Bundle savedStateInstance){
        super.onCreate(savedStateInstance);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cycleCount = 0;

        //Speech recognizer permission setup
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
    }

    /**
     * Called when activity is resumed. Resumes drawing the screen if there is one defined.
     */
    @Override
    public void onResume(){
        super.onResume();
        if (screen != null) screen.resume();
    }

    /**
     * Called when activity is paused. Pauses drawing the screen is there is one defined.
     */
    @Override
    public void onPause(){
        super.onPause();
        if (screen != null) screen.pause();
    }

    /**
     * Called when the activity is destroyed, usually by the system. Pauses, then nulls out the
     * screen if it exists, and cancels and shuts down the speech recognition engine if it exists.
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (screen != null) screen.pause();
        screen = null;
        if(recognizer != null){
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * Defines behaviour for when speech is detected to start. Currently no behaviour defined.
     */
    @Override
    public void onBeginningOfSpeech() {
        //nothing
    }

    /**
     * Defines behaviour for when speech is detected to end. Current no behaviour defined.
     */
    @Override
    public void onEndOfSpeech() {
        //nothing
    }

    /**
     * Defines behaviour for the middle of speech recognition. This is only possible in certain
     * listening modes. Gets a hypothesis about what is being said from PocketSphinx and may
     * act based on it. Currently no default behaviour defined.
     *
     * @param hypothesis PocketSphinx's best guess about what is being said.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        //nothing
    }

    /**
     * Defines behaviour for when a finished hypothesis is formed about speech. Valid for all
     * listening modes. Gets a hypothesis about what was said from PocketSphinx and may act based
     * on it. Currently no default behaviour defined.
     *
     * @param hypothesis PocketSphinx's best guess about what was said.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        //nothing
    }

    /**
     * When an error occurs in speech recognition, display a Toast containing the error message
     * and log the stack trace.
     *
     * @param e the Exception which has occurred.
     */
    @Override
    public void onError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    /**
     * Defines behaviour for a timeout in speech recognition. Currently no behaviour defined.
     */
    @Override
    public void onTimeout() {
        //nothing
    }

    /**
     * Children of GameActivity must define what actions to take (eg state transitions) when the
     * Start Button is pressed at the beginning of the activity.
     */
    protected abstract void startButtonPressed();

    /**
     * Gets the difference between the current time and the time the timer was started
     * in milliseconds
     *
     * @return how much time has elapsed since the timer was last reset.
     */
    public long getElapsedTime(){
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Game logic used to end the game when enough cycles of gameplay have been completed.
     *
     * @param name Name of the activity that finished
     * @param performance Result code of activity
     * @param difficulty Difficulty that the activity is set to
     */
    protected void killIfCountHigh(String name, int performance, int difficulty) {
        if(cycleCount >= maxCycles) {
            Intent intent = getIntent();
            intent.putExtra("activityName", name);
            intent.putExtra("activityPerformance", performance);
            intent.putExtra("activityDifficulty", difficulty);
            setResult(performance, intent);
            finish();
        }
    }

    /**
     * When the recognizer is ready, toggle the Start Button to show on screen.
     */
    protected void recognizerReady(){
        screen.toggleButton();
    }

    /**
     * Resets the start time to be the current time, effectively restarting the timer.
     */
    public void resetTimer(){
        startTime = System.currentTimeMillis();
    }

    /**
     * Sets up the speech recognition engine for just one keyword.
     *
     * @param keyword The word which the recognizer will be listening for.
     * @param dictionary The words which the recognizer should be prepared to listen for. This
     *                   allows for faster switching between keywords.
     */
    protected void runRecognizerSetup(final String keyword, final String[] dictionary){
        if(keyword == null){
            recognizerReady();
            return;
        }

        String[]  newArray = {keyword};

        runRecognizerSetup(newArray, dictionary);
    }

    /**
     * Sets up the speech recognition engine in a background thread for speed and to not tie up
     * the UI. If no recognizer is required, a null keyword will cancel the action and toggle the
     * Start Button.
     *
     * @param keywords The words which the recognizer will be listening for.
     * @param dictionary The words which the recognizer should be prepared to listen for. This
     *                   allows for faster switching between keywords.
     */
    protected void runRecognizerSetup(final String[] keywords, final String[] dictionary) {
        if(keywords == null){
            recognizerReady();
            return;
        }

        new AsyncTask<Void, Void, Exception>() {
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(GameActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir, dictionary);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            protected void onPostExecute(Exception result){
                if (result != null){
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.recognizer_error),
                            Toast.LENGTH_SHORT).show();
                } else {
                    recognizerReady();
                    for (String keyword : keywords) {
                        recognizer.startListening(keyword);
                    }
                }
            }
        }.execute();
    }

    /**
     * Sets the recognizer to listen for English words. Prepares the recognizer to be ready to
     * listen for the words defined in the dictionary passed in.
     *
     * @param assetsDir Directory containing the PocketSphinx listening files.
     * @param keywords Dictionary of words we want PocketSphinx to listen for.
     * @throws IOException Throws IOException if PocketSphinx cannot access the listening files.
     */
    protected void setupRecognizer(File assetsDir, String[] keywords) throws IOException{
        //Set up recognizer
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir)
                .getRecognizer();
        recognizer.addListener(this);

        //Prepare words for listening
        for(String keyword : keywords){
            recognizer.addKeyphraseSearch(keyword, keyword);
        }
    }
}
