package com.avarsava.stuttersupport;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cmu.pocketsphinx.Hypothesis;

/**
 * @author  Tyler Crane (02/08/2018)
 * @version 1.0
 * @since   0.1
 *
 * A game in a man is holding a basketball, and a word is displayed at the top of the screen. When
 * the countdown finishes, the man will begin dribbling the ball. Each time the ball connects with
 * the ground, the human user is to pronounce the first syllable in the word displayed. After 3
 * bounces, the man will shoot the basketball. When this happens, the user is to fully pronounce the
 * word displayed at the top of the screen. If successful, the ball will go into the net, otherwise
 * the ball will fumble or miss the net. The PocketSphinx voice recognition engine is used to
 * identify whether the word spoken was the correct syllable/word.
 */

public class BasketballGameActivity extends GameActivity {

    /**
     * Tag for debug logs
     */
    private final String TAG = "Basketball";

    /**
     * The possible states in the game.
     *
     * NOTREADY - The voice recognition engine has not yet initialized, and the Start Button has
     * not been pressed.
     * COUNTDOWN - The timer is counting down to start a new cycle
     * DRIBBLE_1 - The person is starting to dribble the ball, but the ball is not connected to the ground
     *     - Syllables spoken during this state will cause an incorrect cycle
     * DRIBBLE_2 - The person is still dribbling the ball, and the ball is now connected to the ground
     *     - Syllables are to be said during this state
     * SHOOTING - The person is shooting the ball
     *     - The full word displayed is to be spoken during this state
     * RESETTING - The shot is complete, and either the shot went in if correct, or missed if incorrect
     */
    private enum STATE {NOTREADY, COUNTDOWN, DRIBBLE_1, DRIBBLE_2, SHOOTING, RESETTING};

    /**
     * The difficulty of the game, either 1, 2 or 3
     */
    private int difficulty;

    /**
     * The current state that the game is in (see enum STATE)
     */
    private STATE currentState;

    /**
     * The current string that the user will try to say for the game
     */
    private String currentString;

    /**
     * The current string that the user will try to say for the game
     */
    private String currentSyllable;

    /**
     * The states store additional information as an int when needed
     */
    private int stateInfo = 0;

    /**
     * Similar to stateInfo, this variable stores extra info for the resetting state only
     */
    private int resetInfo = 0;

    /**
     * Used for Pocketsphinx methods to flag an error when the user makes a mistake
     */
    private boolean errorMade = false;

    /**
     * A list of the previously called words, to avoid calling the same word twice.
     */
    private String[] usedStrings;

    /**
     * An integer used to determine how fast the game is played
     */
    private int gameSpeed;

    /**
     * True if the currentString has changed, false otherwise. Used for speed increase
     */
    private boolean textChanged = true;

    /**
     * Counts how many successful cycles were passed for the database
     */
    private int successfulCycles = 0;

    /**
     * Called on the creation of the Activity. Sets up the values needed for the initial state
     * of the game, and launches the setup of voice recognition.
     *
     * @param savedInstanceState Bundle used for communication within Android
     */
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentState = STATE.NOTREADY;
        difficulty = Integer.valueOf(prefs.getString("bb_difficulty", "1"));
        maxCycles = Integer.valueOf(prefs.getString("bb_max_cycles", "3"));
        usedStrings = new String[maxCycles];
        gameSpeed = Integer.valueOf(prefs.getString("bb_speed", "4"));
        gameSpeed = (1500 * (gameSpeed + 1) / 10) + 500; // [650, 2000] in milliseconds
        screen = new BasketballGameView(this, this);
        screen.setBackgroundImage(((BasketballGameView)screen).getInstructionBg());
        setContentView(screen);
        setNewString();
        //Changing the keywords may have an effect on the accuracy
        //  - Set the keywords to a list of all the possible words and syllables
        ArrayList<String> dict = new ArrayList(Arrays.asList(getResources().getStringArray(getResources().getIdentifier("basketball_words_" + difficulty, "array", getPackageName()))));
        dict.addAll(Arrays.asList(getResources().getStringArray(getResources().getIdentifier("basketball_syllables_" + difficulty, "array", getPackageName()))));
        runRecognizerSetup(currentSyllable, dict.toArray(new String[0]));
    }

    /**
     * Gets a string from all the possible words, excluding ones that have already been picked
     *
     * @return a new string that has not been picked yet
     */
    private void setNewString () {
        String potentialString;
        String[] allWords = getResources().getStringArray(getResources().getIdentifier("basketball_words_" + difficulty, "array", getPackageName()));
        String[] allSyll = getResources().getStringArray(getResources().getIdentifier("basketball_syllables_" + difficulty, "array", getPackageName()));
        List<String> usedStringsList = Arrays.asList(usedStrings);
        int randId;

        do {
            randId = Numbers.randInt(1, allWords.length) - 1;
            potentialString = allWords[randId];
        } while (usedStringsList.contains(potentialString));

        currentString = potentialString;
        currentSyllable = allSyll[randId];
        usedStrings[cycleCount] = currentString;
        textChanged = true;
    }

    /**
     * When the Start Button is pressed, sets the game up to run by switching the current state to
     * CALL.
     */
    @Override
    protected void startButtonPressed() {
        currentState = STATE.COUNTDOWN;
        screen.setBackgroundImage(((BasketballGameView)screen).getGameBg());
        resetTimer();
    }

    /**
     * On recognizing speech, processes the speech if the hypothesis matches the word called for.
     *
     * @param hypothesis PocketSphinx's best guess about what is being said.
     */
    @Override
    public void onPartialResult (Hypothesis hypothesis) {
        if (hypothesis == null){
            return;
        }

        String text = hypothesis.getHypstr().split(" ")[0];
        Log.d(TAG, "text = " + text + " State = " + currentState + " Word = " + currentString);

        if (currentState == STATE.SHOOTING) errorMade = !text.equals(currentString);
        else if (currentState == STATE.DRIBBLE_2) errorMade = !text.equals(currentSyllable);
    }

    /**
     * Resets the voice recognition engine.
     */
    private void resetRecognizer(String keyword) {
        recognizer.stop();
        Log.i(TAG, "Resetting recognizer to listen for " + keyword);
        recognizer.startListening(keyword);
    }

    /**
     * Progresses the state to the next state depending on the current status of the game
     */
    public void switchState () {
        if (currentState == STATE.NOTREADY) return;
        switch(currentState){
            case COUNTDOWN:
                if (stateInfo == 2) {
                    currentState = STATE.DRIBBLE_1;
                    stateInfo = 0;
                    textChanged = true;
                }
                else stateInfo++;
                break;
            case DRIBBLE_1:
                errorMade = true;
                if (stateInfo == 3) {
                    resetRecognizer(currentString);
                    currentState = STATE.SHOOTING;
                    stateInfo = 0;
                }
                else currentState = STATE.DRIBBLE_2;
                break;
            case DRIBBLE_2:
                if (errorMade) { //Syllable mistake, ball is fumbled
                    stateInfo = 0;
                    resetInfo = 1;
                    currentState = STATE.RESETTING;
                }
                else {
                    stateInfo++;
                    currentState = STATE.DRIBBLE_1;
                }
                break;
            case SHOOTING:
                if (errorMade) resetInfo = 2; //If word mistake
                else resetInfo = 3; //If no mistake
                if (stateInfo == 1) {
                    currentState = STATE.RESETTING;
                    stateInfo = 0;
                }
                else stateInfo++;
                break;
            case RESETTING:
                if (stateInfo == 2) {
                    cycleCount++;
                    if (resetInfo == 3) successfulCycles++;
                    killIfCountHigh(TAG, successfulCycles, difficulty);
                    if (cycleCount < maxCycles) {
                        setNewString();
                        resetRecognizer(currentSyllable);
                        stateInfo = 0;
                        currentState = STATE.COUNTDOWN;
                    }
                    else stateInfo++;
                }
                else stateInfo++;
        }
    }

    /**
     * Handles drawing the game to the screen.
     */
    protected class BasketballGameView extends DrawView {

        /**
         * Paint used to render the text
         */
        private Paint blackPaint, redPaint;

        /**
         * These drawables store the images to be drawn for the background and foreground (net and court)
         */
        private Drawable instructionBg, gameBg;

        /**
         * These drawables store the ball, net and facial expressions for the person shooting the ball
         */
        private Drawable ball, net, happy, concentrate, sad;

        /**
         * These drawables store the images for the person in different states
         */
        private Drawable hold_ball, dribble_ball_1, dribble_ball_2, shoot_ball;

        /**
         * The width of the screen of the user's device.
         */
        int screenWidth = getScreenWidth();

        /**
         * The height of the screen of the user's device.
         */
        int screenHeight = getScreenHeight();

        /**
         * Sets up the graphic related content for the Basketball game
         *
         * @param context The application context
         * @param ga      Associated Game Activity, used to access timer methods.
         */
        public BasketballGameView(Context context, GameActivity ga) {
            super(context, ga);
            setUpPaints();
            getDrawables();
        }

        /**
         * Returns the background image for the basketball game to display before the user clicks the start button
         *
         * @return Drawable instructionBg for the splash screen
         */
        protected Drawable getInstructionBg() {
            return instructionBg;
        }

        /**
         * Getter for game background, used so Basketball Game can transition to launching game
         *
         * @return Drawable gameBg for the background of the game
         */
        protected Drawable getGameBg(){
            return gameBg;
        }

        /**
         * Draws the objects to the screen based on the current state of the game
         */
        @Override
        protected void doDrawing() {
            if (getElapsedTime() >= gameSpeed) {
                switchState();
                resetTimer();
            }

            switch(currentState){
                case COUNTDOWN:
                    gameBg.draw(canvas);
                    hold_ball.draw(canvas);
                    net.draw(canvas);
                    if (cycleCount == 0 || resetInfo == 3) happy.draw(canvas);
                    else concentrate.draw(canvas);
                    drawWord();

                    String text = (3 - stateInfo) + "";
                    blackPaint.setTextSize(getScaled(92));
                    Rect temp = new Rect();
                    blackPaint.getTextBounds(text, 0, 1, temp);
                    canvas.drawText(text,
                            (screenWidth / 2) - (temp.width() / 2),
                            (screenHeight / 2) + (temp.height() / 2) + 10,
                            blackPaint);
                    break;
                case DRIBBLE_1:
                    gameBg.draw(canvas);
                    dribble_ball_1.draw(canvas);
                    concentrate.draw(canvas);
                    net.draw(canvas);
                    drawWord();
                    ball.setBounds(screenWidth - getScaled(170),
                            screenHeight - getScaled(250),
                            screenWidth - getScaled(122),
                            screenHeight - getScaled(190));
                    ball.draw(canvas);
                    break;
                case DRIBBLE_2:
                    gameBg.draw(canvas);
                    dribble_ball_2.draw(canvas);
                    concentrate.draw(canvas);
                    net.draw(canvas);
                    drawWord();
                    ball.setBounds(screenWidth - getScaled(170),
                            screenHeight - getScaled(120),
                            screenWidth - getScaled(122),
                            screenHeight - getScaled(60));
                    ball.draw(canvas);
                    break;
                case SHOOTING:
                    gameBg.draw(canvas);
                    shoot_ball.draw(canvas);
                    concentrate.draw(canvas);
                    net.draw(canvas);
                    drawWord();
                    if (stateInfo == 0) {
                        ball.setBounds(screenWidth - getScaled(160),
                                screenHeight - getScaled(375),
                                screenWidth - getScaled(112),
                                screenHeight - getScaled(315));
                    }
                    else {
                        ball.setBounds(screenWidth - getScaled(200),
                                screenHeight - getScaled(495),
                                screenWidth - getScaled(152),
                                screenHeight - getScaled(435));
                    }
                    ball.draw(canvas);
                    break;
                case RESETTING:
                    gameBg.draw(canvas);
                    net.draw(canvas);
                    dribble_ball_2.draw(canvas);
                    drawWord();
                    if (resetInfo == 1) { // fumbled (during dribble_1 state)
                        sad.draw(canvas);
                        if (stateInfo == 0) { //Ball bounces away from person
                            ball.setBounds(screenWidth - getScaled(230),
                                    screenHeight - getScaled(250),
                                    screenWidth - getScaled(182),
                                    screenHeight - getScaled(190));
                        }
                        else if (stateInfo == 1) { //Ball bounces off ground further
                            ball.setBounds(screenWidth - getScaled(280),
                                    screenHeight - getScaled(120),
                                    screenWidth - getScaled(232),
                                    screenHeight - getScaled(60));
                        }
                        else if (stateInfo == 2) { //Ball rolls on ground away
                            ball.setBounds(screenWidth - getScaled(320),
                                    screenHeight - getScaled(150),
                                    screenWidth - getScaled(272),
                                    screenHeight - getScaled(90));
                        }
                        ball.draw(canvas);
                    }
                    else if (resetInfo == 2) { // missed net (during shooting state)
                        sad.draw(canvas);
                        if (stateInfo == 0) { //Ball hits rim
                            ball.setBounds(screenWidth - getScaled(240),
                                    screenHeight - getScaled(465),
                                    screenWidth - getScaled(192),
                                    screenHeight - getScaled(405));
                        }
                        else if (stateInfo == 1) { //Ball high in air
                            ball.setBounds(screenWidth - getScaled(200),
                                    screenHeight - getScaled(500),
                                    screenWidth - getScaled(152),
                                    screenHeight - getScaled(440));
                        }
                        else if (stateInfo == 2) { //Ball going towards person
                            ball.setBounds(screenWidth - getScaled(180),
                                    screenHeight - getScaled(360),
                                    screenWidth - getScaled(132),
                                    screenHeight - getScaled(300));
                        }
                        ball.draw(canvas);
                    }
                    else if (resetInfo == 3) { // went in net (during shooting state)
                        happy.draw(canvas);
                        if (stateInfo == 0) { //Ball in net
                            ball.setBounds(screenWidth - getScaled(270),
                                    screenHeight - getScaled(475),
                                    screenWidth - getScaled(222),
                                    screenHeight - getScaled(415));
                        }
                        else if (stateInfo == 1) { //Ball below net
                            ball.setBounds(screenWidth - getScaled(260),
                                    screenHeight - getScaled(335),
                                    screenWidth - getScaled(212),
                                    screenHeight - getScaled(275));
                        }
                        else if (stateInfo == 2) { //Ball on ground
                            ball.setBounds(screenWidth - getScaled(260),
                                    screenHeight - getScaled(125),
                                    screenWidth - getScaled(212),
                                    screenHeight - getScaled(65));
                        }
                        ball.draw(canvas);
                    }
            }
        }

        /**
         * Draws the current string to the screen with appropriate highlighting
         */
        protected void drawWord () {
            String high = "";
            String text = "";

            if (currentState == STATE.DRIBBLE_2 || currentState == STATE.DRIBBLE_1) {
                for (int i = 0; i < 3; i++) {
                    if (i <= stateInfo) high += currentSyllable + " - ";
                    else text += currentSyllable + " - ";
                }

                text += currentString;

                if (textChanged) {
                    float textSize = getTextSizeForWidth(blackPaint, screenWidth - 30, high + text);
                    blackPaint.setTextSize(textSize);
                    redPaint.setTextSize(textSize);
                    textChanged = false;
                }
                Rect blackTemp = new Rect();
                Rect redTemp = new Rect();

                blackPaint.getTextBounds(high + text, 0, high.length() + text.length(), blackTemp);
                redPaint.getTextBounds(high, 0, high.length(), redTemp);

                canvas.drawText(high,
                        5,
                        blackTemp.height() + 10,
                        redPaint);

                canvas.drawText(text,
                        redTemp.width() + getScaled(18),
                        blackTemp.height() + 10,
                        blackPaint);
            }
            else if (currentState == STATE.SHOOTING) {
                text = currentSyllable + " - " + currentSyllable + " - " + currentSyllable + " - " + currentString;
                if (textChanged) {
                    redPaint.setTextSize(getTextSizeForWidth(blackPaint, screenWidth - 30, text));
                    textChanged = false;
                }
                Rect temp = new Rect();
                redPaint.getTextBounds(text, 0, text.length(), temp);
                canvas.drawText(text,
                        5,
                        temp.height() + 10,
                        redPaint);
            }
            else {
                text = currentSyllable + " - " + currentSyllable + " - " + currentSyllable + " - " + currentString;
                if (textChanged || currentState == STATE.COUNTDOWN) {
                    blackPaint.setTextSize(getTextSizeForWidth(blackPaint, screenWidth - 30, text));
                    textChanged = false;
                }
                Rect temp = new Rect();
                blackPaint.getTextBounds(text, 0, text.length(), temp);
                canvas.drawText(text,
                        5,
                        temp.height() + 10,
                        blackPaint);
            }
        }

        /**
         * Loads in the drawables from the resources for drawing
         */
        private void getDrawables () {
            Resources resources = getResources();

            //Get drawables from Resources
            instructionBg = resources.getDrawable(R.drawable.ic_basketball_instructions);
            gameBg = resources.getDrawable(R.drawable.ic_basketball_bg);
            ball = resources.getDrawable(R.drawable.ic_basketball);
            net = resources.getDrawable(R.drawable.ic_basketball_net);
            concentrate = resources.getDrawable(R.drawable.ic_basketball_concentrating_face);
            happy = resources.getDrawable(R.drawable.ic_basketball_happy_face);
            sad = resources.getDrawable(R.drawable.ic_basketball_sad_face);
            hold_ball = resources.getDrawable(R.drawable.ic_basketball_holding);
            dribble_ball_1 = resources.getDrawable(R.drawable.ic_basketball_dribbling_1);
            dribble_ball_2 = resources.getDrawable(R.drawable.ic_basketball_dribbling_2);
            shoot_ball = resources.getDrawable(R.drawable.ic_basketball_shooting);

            instructionBg.setBounds(0, 0, screenWidth, screenHeight);
            gameBg.setBounds(0, 0, screenWidth, screenHeight);

            net.setBounds(getScaled(25),
                    screenHeight - getScaled(500),
                    getScaled(200),
                    screenHeight - getScaled(100));
            concentrate.setBounds(screenWidth - getScaled(100),
                    screenHeight - getScaled(360),
                    screenWidth - getScaled(30),
                    screenHeight - getScaled(270));
            happy.setBounds(screenWidth - getScaled(100),
                    screenHeight - getScaled(360),
                    screenWidth - getScaled(30),
                    screenHeight - getScaled(270));
            sad.setBounds(screenWidth - getScaled(100),
                    screenHeight - getScaled(360),
                    screenWidth - getScaled(8),
                    screenHeight - getScaled(270));

            hold_ball.setBounds(screenWidth - getScaled(159),
                    screenHeight - getScaled(332),
                    screenWidth - getScaled(17),
                    screenHeight - getScaled(61));
            dribble_ball_1.setBounds(screenWidth - getScaled(158),
                    screenHeight - getScaled(335),
                    screenWidth - getScaled(20),
                    screenHeight - getScaled(67));
            dribble_ball_2.setBounds(screenWidth - getScaled(140),
                    screenHeight - getScaled(335),
                    screenWidth,
                    screenHeight - getScaled(68));
            shoot_ball.setBounds(screenWidth - getScaled(140),
                    screenHeight - getScaled(355),
                    screenWidth + getScaled(13),
                    screenHeight - getScaled(66));
        }

        /**
         * Sets properties of black paint used to render text in-game.
         */
        private void setUpPaints(){
            blackPaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            blackPaint.setTextAlign(Paint.Align.LEFT);
            redPaint = new Paint();
            redPaint.setColor(Color.RED);
            redPaint.setTextAlign(Paint.Align.LEFT);
        }
    }
}