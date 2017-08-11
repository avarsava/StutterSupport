package com.example.myself.stuttersupport;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import edu.cmu.pocketsphinx.Hypothesis;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Runs the Car Game, in which the player must hold sounds for as long as they're on the screen.
 */
public class CarGameActivity extends GameActivity {
    /**
     * The starting point to identify potential words in the internal word list. Should probably
     * always be 1.
     */
    private final int MIN_PAIR = 1;

    /**
     * The ending point to identify potential words in the internal word list. Effectively, how
     * many words there are in the word list.
     */
    private final int MAX_PAIR = 5;
    /**
     * The possible states in the game.
     *
     * NOTREADY - The voice recognition engine has not yet initialized, and the Start Button
     * has not been pressed.
     * WAIT - Waiting for a word to appear
     * HOLD - Holding the word as it shows on the screen
     * GOOD - Player is holding the current word
     * BAD - Player has dropped the word early
     */
    private enum STATE {NOTREADY, WAIT, HOLD, GOOD, BAD};

    /**
     * The current state of gameplay.
     */
    private STATE currentState;

    /**
     * The current string from the word list which the voice recognizer is listening for.
     */
    private String currentString, displayString;

    /**
     * A list of the previously called words, to avoid calling the same word twice.
     */
    private String[] usedStrings;

    /**
     * How long the game should wait between words, gotten from preferences.
     */
    private long waitDuration;

    /**
     * How long the game should hold each word for, gotten from preferences
     */
    private long holdDuration;

    /**
     * How many of the cycles the user has successfully responded.
     */
    private int passed = 0;

    /**
     * Whether or not the user successfully responded this cycle
     */
    private boolean successful = false;

    /**
     * Creates the Activity and sets up the initial values of the variables.
     *
     * @param savedInstanceState used internally for Android communication
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        maxCycles = Integer.valueOf(prefs.getString("cg_noOfRounds", "3"));
        usedStrings = new String[maxCycles];
        waitDuration = Long.valueOf(prefs.getString("cg_waitTime", "3"))*1000;
        holdDuration = Long.valueOf(prefs.getString("cg_holdTime", "5"))*1000;
        currentState = STATE.NOTREADY;
        currentString = getString();
        displayString = currentString;
        screen = new CarGameView(this, this);
        setContentView(screen);

        //set up speech recognition
        runRecognizerSetup(currentString, getResources().getStringArray(R.array.calls));
    }

    /**
     * Describes the action to take when the start button is pressed.
     */
    @Override
    protected void startButtonPressed(){
        currentState = STATE.WAIT;
        resetTimer();
    }

    /**
     * On recognizing speech, processes the speech if the hypothesis matches the word called for
     *
     * @param hypothesis PocketSphinx's best guess about what is being said
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis){
        if(hypothesis == null || currentState == STATE.WAIT || currentState == STATE.BAD){
            return;
        }

        String text = hypothesis.getHypstr();
        if(text.equals(currentString)){
            currentState = STATE.GOOD;
            displayString = "Good!";
        } else{
            currentState = STATE.BAD;
            displayString = "Bad!";
        }
    }


    /**
     * When speech is finished, processes the speech if the hypothesis matches the world
     * called for.
     *
     * @param hypothesis PocketSphinx's best guess about what was said.
     */
    @Override
    public void onResult(Hypothesis hypothesis){
        if(hypothesis == null || currentState != STATE.WAIT){
            currentState = STATE.BAD;
            return;
        }

        //TODO: Do I need to check again if the word is the right one?
    }

    /**
     * Gets a random word from all the possible words, excluding the ones that have already been
     * picked.
     *
     * TODO: Should I use a different set of words for this game?
     *
     * @return new, not-yet-picked word as String
     */
    private String getString(){
        String potentialString;
        String[] allWords = getResources().getStringArray(R.array.calls);
        List<String> usedStringsList = Arrays.asList(usedStrings);
        int randId;

        do {
            randId = Numbers.randInt(MIN_PAIR, MAX_PAIR) - 1;
            potentialString = allWords[randId];
        } while (usedStringsList.contains(potentialString));

        usedStrings[cycleCount] = potentialString;
        return potentialString;
    }

    /**
     * Handles state switch timing and setting up for the next state of gameplay.
     */
    private void switchStateIfNecessary(){
        switch(currentState){
            case WAIT:
                if(getElapsedTime()/waitDuration >= 1.0){
                    currentState = STATE.HOLD;
                    resetTimer();
                }
                break;
            case GOOD:
                if(getElapsedTime()/holdDuration >= 1.0){
                    passed++;
                }
            case BAD:
            case HOLD:
                if(getElapsedTime()/holdDuration >= 1.0){
                    currentState = STATE.WAIT;
                    resetTimer();
                    cycleCount++;
                    if(cycleCount != maxCycles) {
                        currentString = getString();
                        displayString = currentString;
                        resetRecognizer();
                    }
                }
                break;
        }
    }

    /**
     * Resets the voice recognition engine.
     */
    private void resetRecognizer() {
        recognizer.stop();
        recognizer.startListening(currentString);
    }

    /**
     * Determines whether the user passed all the cycles of gameplay and returns the appropriate
     * result code for Android.
     *
     * @return RESULT_OK if the user has successfully completed the Activity, else RESULT_CANCELED
     */
    private int calculateSuccess(){
        if (passed == maxCycles){
            return RESULT_OK;
        } else {
            return RESULT_CANCELED;
        }
    }

    /**
     * Handles drawing the game to the screen.
     */
    protected class CarGameView extends DrawView {
        /**
         * Paint used to render the text
         */
        private Paint blackPaint;

        /**
         * Paint used to render the temporary background
         * TODO: Remove this once graphics are in place
         */
        private Paint whitePaint;

        /**
         * The width of the screen of the user's device.
         */
        int screenWidth = getScreenWidth();

        /**
         * The height of the screen of the user's device.
         */
        int screenHeight = getScreenHeight();

        /**
         * Creates a new CarGameView.
         *
         * @param context The application context
         * @param ga      Associated Game Activity, used to access timer methods.
         */
        public CarGameView(Context context, GameActivity ga) {
            super(context, ga);
            setUpPaints();
        }

        @Override
        protected void doDrawing() {
            //Draw background
            //TODO: remove when adding graphics
            canvas.drawRect(0, 0, screenWidth, screenHeight, whitePaint);

            //Draw text
            switch(currentState){
                case GOOD:
                case BAD:
                case HOLD:
                    canvas.drawText(displayString,
                            screenWidth/2,
                            screenHeight/2,
                            blackPaint);
                    break;
            }

            //cycle end logic
            switchStateIfNecessary();
            killIfCountHigh(calculateSuccess());

        }

        private void setUpPaints(){
            blackPaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            blackPaint.setTextAlign(Paint.Align.CENTER);
            blackPaint.setTextSize(100);

            //TODO: Remove when adding graphics
            whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);
        }
    }
}
