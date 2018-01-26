package com.avarsava.stuttersupport;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import edu.cmu.pocketsphinx.Hypothesis;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * A game in which a man prompts you to repeat after him, then you are interrupted by a passing
 * train. The idea is to take a deep breath while the train is passing by, rather than rushing
 * into speech, which can cause muscles to tense and trigger stuttering. After the train has passed
 * the PocketSphinx voice recognition engine is used to identify whether the user has said the
 * correct word at the right time.
 */
public class TrainGameActivity extends GameActivity{
    /**
     * Tag for debug logs
     */
    private final String TAG = "TrainGame";

    /**
     * How long the 'call' phase of the cycle should last.
     */
    private final long CALL_DURATION = 3000L;

    /**
     * How long the 'response' phase of the cycle should last.
     */
    private final long RESP_DURATION = 3000L;

    /**
     * How long to wait before exiting the activity when the activity is cancelled.
     */
    private final long CANCEL_DURATION = 3000L;

    /**
     * The starting point to identify potential words in the internal word list. Retrieved from
     * the Difficulty object once the preferences have been read.
     */
    private int minPair;

    /**
     * The ending point to identify potential words in the internal word list. Effectively, how
     * many words there are in the word list.
     */
    private int maxPair;

    /**
     * The possible states in the game.
     *
     * NOTREADY - The voice recognition engine has not yet initialized, and the Start Button has
     * not been pressed.
     * CALL - The character opposite you calls out a word which the user must repeat later
     * WAIT - The train is passing by, the user must wait
     * RESP - The train has passed and the character is listening for your response.
     */
    private enum STATE {NOTREADY, CALL, WAIT, RESP};

    /**
     * The current string from the word list which the voice recognizer is listening for.
     */
    private String currentString;

    /**
     * A list of the previously called words, to avoid calling the same word twice.
     */
    private String[] usedStrings;

    /**
     * How long the train should pass by for, gotten from preferences.
     */
    private long waitDuration;

    /**
     * How many of the cycles the user has successfully responded.
     */
    private int passed = 0;

    /**
     * Whether or not the user successfully responded this cycle
     */
    private boolean successful = false;

    /**
     * The current state of gameplay.
     */
    private STATE currentState;

    /**
     * Called on the creation of the Activity. Sets up the values needed for the initial state
     * of the game, and launches the setup of voice recognition.
     *
     * @param savedInstanceState Bundle used for communication within Android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int difficulty = Integer.valueOf(prefs.getString("tg_Difficulty", "1"));
        minPair = Difficulty.getMinForLevel(difficulty);
        maxPair = Difficulty.getMaxForLevel(difficulty);
        maxCycles = Integer.valueOf(prefs.getString("tg_noOfWords", "3"));
        usedStrings = new String[maxCycles];
        waitDuration = Long.valueOf(prefs.getString("tg_waitTime", "10"))*1000;
        currentState = STATE.NOTREADY;
        currentString = getString();
        screen = new TrainGameView(this, this);
        screen.setBackgroundImage(((TrainGameView)screen).getInstructionBg());
        setContentView(screen);

        //set up speech recognition
        runRecognizerSetup(currentString, getResources().getStringArray(R.array.calls));
    }

    /**
     * On recognizing speech, processes the speech if the hypothesis matches the word called for.
     *
     * @param hypothesis PocketSphinx's best guess about what is being said.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null){
            return;
        }

        String text = hypothesis.getHypstr().split(" ")[0];
        Log.d(TAG, "text = " + text + " currentString = " + currentString);
        if(text.equals(currentString)){
            processSpeech();
        }
        resetRecognizer();
    }

    /**
     * When the Start Button is pressed, sets the game up to run by switching the current state to
     * CALL.
     */
    @Override
    protected void startButtonPressed(){
        currentState = STATE.CALL;
        screen.setBackgroundImage(((TrainGameView)screen).getGameBg());
        resetTimer();
    }

    /**
     * Gets a random word from all the possible words, excluding the ones that have already been
     * picked.
     *
     * @return new, not-yet-picked word as String
     */
    private String getString(){
        String potentialString;
        String[] allWords = getResources().getStringArray(R.array.calls);
        List<String> usedStringsList = Arrays.asList(usedStrings);
        int randId;

        do {
            randId = Numbers.randInt(minPair, maxPair) - 1;
            potentialString = allWords[randId];
        } while (usedStringsList.contains(potentialString));

        Log.d(TAG, "cycleCount = " + cycleCount);
        usedStrings[cycleCount] = potentialString;
        Log.d(TAG, "usedStrings = " + usedStrings.toString());
        return potentialString;
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
     * Cancels the cycle, causing the game to wait for the specified duration.
     */
    public void cancelCycle() {
        resetTimer();
        currentState = STATE.RESP;
        successful = false;
        while(getElapsedTime()/CANCEL_DURATION < 1.0){
            //waste time
        }

    }

    /**
     * Determines how to respond to speech based on the current state of gameplay. If the game is
     * currently in the RESP state, the speech is appropriately timed and the user has passed
     * the round.
     * Otherwise, the cycle is canceled.
     */
    private void processSpeech(){

        if(currentState == STATE.RESP){
            successful = true;
            passed++;
            Log.d(TAG, "passed = " + passed + ", successful should now be true");
        } else {
            if(currentState == STATE.NOTREADY){
                return;
            }else {
                cancelCycle();
            }
        }
    }

    /**
     * Resets the voice recognition engine.
     */
    private void resetRecognizer() {
        recognizer.stop();
        Log.i(TAG, "Resetting recognizer to listen for " + currentString);
        recognizer.startListening(currentString);
    }

    /**
     * Handles state switch timing and setting up for the next state of gameplay.
     */
    private void switchStateIfNecessary(){
        switch(currentState){
            case CALL:
                if(getElapsedTime()/CALL_DURATION >= 1.0){
                    currentState = STATE.WAIT;
                    resetTimer();
                }
                break;
            case WAIT:
                if(getElapsedTime()/ waitDuration >= 1.0){
                    currentState = STATE.RESP;
                    resetTimer();
                }
                break;
            case RESP:
                if(getElapsedTime()/RESP_DURATION >= 1.0){
                    if(!successful){
                        cancelCycle();
                    }
                    currentState = STATE.CALL;
                    resetTimer();
                    cycleCount++;
                    Log.d(TAG, "Cycle count  = " + cycleCount);
                    if (cycleCount != maxCycles) currentString = getString();
                    resetRecognizer();
                    successful = false;
                }
                break;
        }
    }

    /**
     * Handles drawing the game to the screen.
     */
    protected class TrainGameView extends DrawView {
        /**
         * Paint used to render the text
         */
        private Paint blackPaint;

        /**
         * Game scenery
         */
        private Drawable instructionBg, gameBg, gameFg;

        /**
         * Game objects used to denote gameplay progression.
         */
        private Drawable bgBalloon, fgBalloon, checkmark, qmark, car, happy, sad;

        /**
         * The width of the screen of the user's device.
         */
        int screenWidth = getScreenWidth();

        /**
         * The height of the screen of the user's device.
         */
        int screenHeight = getScreenHeight();

        /**
         * Sets up the graphics-related objects.
         *
         * @param context The application context
         * @param ga The parent game activity, used for accessing timer functions
         */
        public TrainGameView(Context context, GameActivity ga) {
            super(context, ga);
            setUpPaints();
            getDrawables();
        }

        /**
         * Draws objects to screen based on the current state of gameplay.
         */
        @Override
        protected void doDrawing(){
            switch(currentState){
                case CALL:
                    bgBalloon.draw(canvas);
                    canvas.drawText(currentString,
                            getScaled(120),
                            getScaled(125),
                            blackPaint);
                    happy.draw(canvas);
                    gameFg.draw(canvas);
                    break;
                case WAIT:
                    car.draw(canvas);
                    gameFg.draw(canvas);
                    break;
                case RESP:
                    gameFg.draw(canvas);
                    bgBalloon.draw(canvas);
                    if(successful){
                        Log.d(TAG, "successful is true, draw good result");
                        fgBalloon.draw(canvas);
                        canvas.drawText(currentString,
                                screenWidth - getScaled(90),
                                screenHeight - getScaled(140),
                                blackPaint);
                        happy.draw(canvas);
                        checkmark.draw(canvas);
                    }else{
                        sad.draw(canvas);
                        qmark.draw(canvas);
                    }
                    break;
            }

            //cycle end logic
            switchStateIfNecessary();
            killIfCountHigh(calculateSuccess());
        }

        /**
         * Getter for instruction card for beginning of game.
         *
         * @return Instruction card
         */
        public Drawable getInstructionBg(){
            return instructionBg;
        }

        /**
         * Getter for game background, used so TrainGameActivity can transition to launching game
         *
         * @return Game background
         */
        public Drawable getGameBg(){
            return gameBg;
        }

        /**
         * Sets properties of black paint used to render text in-game.
         */
        private void setUpPaints(){
            blackPaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            blackPaint.setTextAlign(Paint.Align.CENTER);
            blackPaint.setTextSize(100);
        }

        /**
         * Gets drawable objects from resources and sets their places on the screen.
         */
        private void getDrawables(){
            //Get Resources
            Resources resources = getResources();

            //Get drawables from Resources
            instructionBg = resources.getDrawable(R.drawable.ic_train_game_instructions);
            gameBg = resources.getDrawable(R.drawable.ic_train_game_1);
            gameFg = resources.getDrawable(R.drawable.ic_train_game_foreground);
            bgBalloon = resources.getDrawable(R.drawable.ic_bg_balloon);
            fgBalloon = resources.getDrawable(R.drawable.ic_fg_balloon);
            checkmark = resources.getDrawable(R.drawable.ic_checkmar);
            qmark = resources.getDrawable(R.drawable.ic_q_mark);
            car = resources.getDrawable(R.drawable.ic_train_car);
            happy = resources.getDrawable(R.drawable.ic_train_game_happy_face);
            sad = resources.getDrawable(R.drawable.ic_train_game_sad_face);

            //Set boundaries for drawings
            gameFg.setBounds(0, 0, screenWidth, screenHeight);
            bgBalloon.setBounds(0, 0,
                    screenWidth - getScaled(100),
                    screenHeight - getScaled(400));
            fgBalloon.setBounds(screenWidth - getScaled(175),
                    screenHeight - getScaled(300),
                    screenWidth,
                    screenHeight);
            happy.setBounds(screenWidth - getScaled(100),
                    getScaled(80),
                    screenWidth - getScaled(30),
                    getScaled(180));
            sad.setBounds(screenWidth - getScaled(100),
                    getScaled(80),
                    screenWidth - getScaled(30),
                    getScaled(180));
            car.setBounds(0, 0, screenWidth, screenHeight - getScaled(120));
            checkmark.setBounds(getScaled(75), getScaled(50),
                    getScaled(175), getScaled(155));
            qmark.setBounds(getScaled(75), getScaled(50),
                    getScaled(175), getScaled(155));
        }
    }
}
