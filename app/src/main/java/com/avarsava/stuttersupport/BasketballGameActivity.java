package com.avarsava.stuttersupport;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

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

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

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
     * The current state that the game is in (see enum STATE)
     */
    private STATE currentState;

    /**
     * The current string that the user will try to say for the game
     */
    private String currentString;

    /**
     * A list of the previously called words, to avoid calling the same word twice.
     */
    private String[] usedStrings;

    /**
     * An integer used to determine how fast the game is played
     */
    private int gameSpeed;

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
		int difficulty = Integer.valueOf(prefs.getString("bb_difficulty", "1"));
        minPair = Difficulty.getMinForLevel(difficulty);
        maxPair = Difficulty.getMaxForLevel(difficulty);
        maxCycles = Integer.valueOf(prefs.getString("bb_max_cycles", "3"));
        usedStrings = new String[maxCycles];
        gameSpeed = Integer.valueOf(prefs.getString("bb_speed", "5"));
		screen = new BasketballGameView(this, this);
        screen.setBackgroundImage(((BasketballGameView)screen).getInstructionBg());
        setContentView(screen);
        screen.toggleButton();
    }

    /**
     * Gets a string from all the possible words, excluding ones that have already been picked
     *
     * @return a new string that has not been picked yet
     */
    private String getString () {
        return "";
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

    @Override
    public void onBeginningOfSpeech () {

    }

    @Override
    public void onEndOfSpeech () {

    }

    /**
     * On recognizing speech, processes the speech if the hypothesis matches the word called for.
     *
     * @param hypothesis PocketSphinx's best guess about what is being said.
     */
    @Override
    public void onResult (Hypothesis hypothesis) {

    }

    /**
     * On recognizing speech, processes the speech if the hypothesis matches the word called for.
     *
     * @param hypothesis PocketSphinx's best guess about what is being said.
     */
    @Override
    public void onPartialResult (Hypothesis hypothesis) {

    }

    /**
     * Handles drawing the game to the screen.
     */
	protected class BasketballGameView extends DrawView {

		/**
         * Paint used to render the text
         */
        private Paint blackPaint;
		
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
            switch(currentState){
                case COUNTDOWN:
                    gameBg.draw(canvas);
                    hold_ball.draw(canvas);
                    net.draw(canvas);
                    happy.draw(canvas);
                    //Draw word and countdown text
                    break;
                case DRIBBLE_1:
                    gameBg.draw(canvas);
                    dribble_ball_1.draw(canvas);
                    concentrate.draw(canvas);
                    net.draw(canvas);
                    //Change location of the ball
                    ball.draw(canvas);
                    break;
                case DRIBBLE_2:
                    gameBg.draw(canvas);
                    dribble_ball_2.draw(canvas);
                    concentrate.draw(canvas);
                    net.draw(canvas);
                    //Change location of the ball
                    ball.draw(canvas);
                    break;
                case SHOOTING:
                    gameBg.draw(canvas);
                    shoot_ball.draw(canvas);
                    concentrate.draw(canvas);
                    net.draw(canvas);
                    //May need a picture of the net with the ball going through the basket
                    //Change location of the ball
                    ball.draw(canvas);
                    break;
                case RESETTING:
                    gameBg.draw(canvas);
                    //Need a way to know if ball is:
                    //  - fumbled (during dribble_1 state)
                    //  - missed net (during shooting state)
                    //  - went in net (during shooting state)
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

            //ball.setBounds(0, 0, screenWidth, screenHeight); //Will be constantly changing

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
                    screenWidth - getScaled(30),
                    screenHeight - getScaled(270));

            hold_ball.setBounds(screenWidth - getScaled(150),
                    screenHeight - getScaled(330),
                    screenWidth - getScaled(20),
                    screenHeight - getScaled(60));
            dribble_ball_1.setBounds(screenWidth - getScaled(150),
                    screenHeight - getScaled(330),
                    screenWidth - getScaled(20),
                    screenHeight - getScaled(60));
            dribble_ball_2.setBounds(screenWidth - getScaled(150),
                    screenHeight - getScaled(330),
                    screenWidth - getScaled(20),
                    screenHeight - getScaled(60));
            shoot_ball.setBounds(screenWidth - getScaled(150),
                    screenHeight - getScaled(330),
                    screenWidth - getScaled(20),
                    screenHeight - getScaled(60));
        }
		
		/**
         * Sets properties of black paint used to render text in-game.
         */
        private void setUpPaints(){
            blackPaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            blackPaint.setTextAlign(Paint.Align.CENTER);
            blackPaint.setTextSize(getScaled(100));
        }
    }
}