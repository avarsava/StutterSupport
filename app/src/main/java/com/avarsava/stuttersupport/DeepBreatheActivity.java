package com.avarsava.stuttersupport;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Activity responsible for running the Deep Breathe activity. When the start button is pressed,
 * an animation of a man breathing in and out with a pulsating circle and instructions to breathe
 * in and out. The length of these breaths can be adjusted in the Settings associated with the
 * game.
 */
public class DeepBreatheActivity extends GameActivity {
    public static final String ACTIVITY_NAME = "DeepBreathe";

    /**
     * A picture of a man breathing in.
     */
    private Drawable inhaleBg;

    /**
     * A picture of a man breathing out.
     */
    private Drawable exhaleBg;

    /**
     * The possible states the game can be in. Used to control animation.
     *
     * NOTREADY - Game start button has not yet been pushed.
     * INHALE - Display breathing in.
     * EXHALE - Display breathing out.
     */
    private enum STATE {NOTREADY, INHALE, EXHALE}

    /**
     * Contains the breathe in and breathe out strings from Resources
     */
    private String breatheIn, breatheOut;

    /**
     * How long in milliseconds each inhale should last. Defined in seconds in the Settings.
     */
    private long inhaleDuration;

    /**
     * How long in milliseconds each exhale should last. Defined in seconds in the Settings.
     */
    private long exhaleDuration;

    /**
     * Which STATE the activity is currently in.
     */
    private STATE currentState;

    /**
     * Called automatically when Activity is created. Gets preferences from Settings file and
     * assigns variables to values gotten from settings. Saves drawables from Resources for later
     * use. Creates a new DeepBreatheView and sets that to draw to the screen. Since this game does
     * not use voice recognition, runs the recognizer setup with 'null' to trigger the Start Button
     * appearing.
     *
     * @see DeepBreatheView for information on screen
     * @param savedInstanceState used for Android internal communication
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentState = STATE.NOTREADY;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        maxCycles = Integer.valueOf(prefs.getString("db_noOfBreaths", "3"));
        inhaleDuration = Long.valueOf(prefs.getString("db_inhaleLength", "7")) * 1000;
        exhaleDuration = Long.valueOf(prefs.getString("db_exhaleLength", "11")) * 1000;
        inhaleBg =
                getResources().getDrawable(R.drawable.ic_deep_breathe_inhale);
        exhaleBg =
                getResources().getDrawable(R.drawable.ic_deep_breathe_exhale);
        breatheIn = getResources().getString(R.string.breathe_in);
        breatheOut = getResources().getString(R.string.breathe_out);
        screen = new DeepBreatheView(this, this);
        screen.setBackgroundImage(getResources().getDrawable(
                R.drawable.ic_deep_breathe_instructions));
        setContentView(screen);

        //dummy speech recognizer just to roll the ball
        runRecognizerSetup((String)null, null);
    }

    /**
     * When the start button is pressed, set the current state to Inhale and start the timer
     * from 0.
     */
    @Override
    protected void startButtonPressed(){
        currentState = STATE.INHALE;
        screen.setBackgroundImage(inhaleBg);
        resetTimer();
    }

    /**
     * Gets the appropriate instruction to breathe in or breathe out based on the current state.
     *
     * @return an instruction, either 'breathe in' or 'breathe out', based on current state
     */
    private String getInstructions() {
        String instruction = "";
        switch (currentState) {
            case INHALE:
                instruction = breatheIn;
                break;
            case EXHALE:
                instruction = breatheOut;
                break;
        }
        return instruction;
    }

    /**
     * Compares the current elapsed time to the settings-defined duration that the current state
     * should last. If the current elapsed time is greater than or equal to that value, switches
     * to the next logical state, and takes appropriate actions to set up for the next state.
     */
    private void switchStateIfNecessary() {
        switch (currentState) {
            case INHALE:
                if (getElapsedTime() / inhaleDuration >= 1.0) {
                    currentState = STATE.EXHALE;
                    screen.setBackgroundImage(exhaleBg);
                    resetTimer();
                }
                break;
            case EXHALE:
                if (getElapsedTime() / exhaleDuration >= 1.0) {
                    currentState = STATE.INHALE;
                    screen.setBackgroundImage(inhaleBg);
                    resetTimer();
                    cycleCount++;
                }
                break;
        }
    }

    /**
     * View responsible for drawing the screen of DeepBreathe.
     */
    private class DeepBreatheView extends DrawView {
        /**
         * Paints used for drawing the circle and the text, respectively. Used internally.
         */
        private Paint circlePaint, textPaint;

        /**
         * Float values to define position and min and max size of breathing circle.
         * Used internally.
         */
        private float circleHeight, circleWidth, maxRadius, minRadius;

        /**
         * Creates a new screen and defines the position of the circle and the tools used to
         * draw it.
         *
         * @param context The application context
         * @param ga the current activity, in this case DeepBreatheActivity. Provides access to
         *           the timer-related methods.
         */
        public DeepBreatheView(Context context, GameActivity ga) {
            super(context, ga);

            setUpPaints();
            float width = getScreenWidth();
            circleHeight = getScreenHeight() * 0.75f;
            circleWidth = width / 2;
            minRadius = width * 0.1f;
            maxRadius = width * 0.3f;
        }

        /**
         * Creates Paints to draw circle and text with. Sets background to inhale background.
         */
        private void setUpPaints() {
            circlePaint = new Paint();
            textPaint = new Paint();
            circlePaint.setColor(Color.MAGENTA);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(50f);
            textPaint.setTextAlign(Paint.Align.CENTER);
            background = inhaleBg;
        }

        /**
         * Defines how to draw the screen.
         *
         * Draws the current background, a circle of animated radius, and text on top of that.
         * Since this method is in the game loop, calls some game logic held by the activity.
         */
        @Override
        protected void doDrawing() {
            //Draw a pink circle
            canvas.drawCircle(circleWidth, circleHeight, animatedRadius(), circlePaint);

            //Draw text over circle
            canvas.drawText(getInstructions(), circleWidth, circleHeight, textPaint);

            //Do some game logic
            switchStateIfNecessary();
            killIfCountHigh(ACTIVITY_NAME, RESULT_OK,1);
        }

        /**
         * Calculates the radius the breathing circle ought to be given the current state and
         * how long that state has existed.
         *
         * During the inhale state, the circle expands from the minimum to maximum radius over the
         * period of time the inhale takes.
         * During the exhale state, the circle shrinks from the maximum to minimum radius over the
         * period of time the exhale takes.
         *
         * @return float value of the radius at the millisecond this function was called
         */
        private float animatedRadius() {
            float newRadius = 0f;

            switch (currentState) {
                case INHALE:
                    newRadius = ((maxRadius - minRadius) * getElapsedTime() / inhaleDuration)
                            + minRadius;
                    if (newRadius > maxRadius) newRadius = maxRadius;
                    break;
                case EXHALE:
                    newRadius = ((minRadius - maxRadius) * getElapsedTime() / exhaleDuration)
                            + maxRadius;
                    if (newRadius < minRadius) newRadius = minRadius;
                    break;
            }

            return newRadius;
        }
    }
}


