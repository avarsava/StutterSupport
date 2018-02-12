package com.avarsava.stuttersupport;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.util.Arrays;

import edu.cmu.pocketsphinx.Hypothesis;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * An activity in which the player is tasked, without timing or pressure, to read aloud a script
 * taken from an XML file. The words of the script are shown on the screen and the words are
 * highlighted as they are read.
 */

public class ScriptReadingActivity extends GameActivity {
    public static final String ACTIVITY_NAME = "ScriptReading";
    /**
     * Tag for debug logs.
     */
    private final String TAG = "ScriptReading";

    /**
     * The starting point to identify potential scripts to feed to the user.
     */
    private final int MIN_SCRIPT = 1;

    /**
     * The ending point to identify potential scripts to feed to the user.
     */
    private final int MAX_SCRIPT = 4;

    /**
     * The current script which the user must read out. Drawn from XML.
     */
    private String currentScript;

    /**
     * The part of the script which the user has already read. Updated by voice recognition logic.
     */
    private String highlightScript;

    /**
     * currentScript as a String array, to get next word easier.
     */
    private String[] scriptWords;

    /**
     * Describes whether or not the voice recognition engine has been initialized.
     */
    private boolean recognizerInitialized;

    /**
     * Set up objects necessary for gameplay. Called automatically when Activity starts
     *
     * @param savedInstanceState used for internal Android communication
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        currentScript = getScriptFromResources();
        scriptWords = currentScript.split(" ");
        highlightScript = "";
        recognizerInitialized = false;
        maxCycles = scriptWords.length;
        cycleCount = 0;

        screen = new ScriptReadingView(this, this);
        screen.setBackgroundImage(((ScriptReadingView)screen).getInstructionBg());
        setContentView(screen);

        String[] words = getScriptWords();
        runRecognizerSetup(words, words);
    }

    /**
     * When the Start Button is pressed, starts the activity
     */
    @Override
    protected void startButtonPressed() {
        recognizerInitialized = true;
        screen.setBackgroundImage(((ScriptReadingView)screen).getGameBg());
    }

    /**
     * When a word has been recognized, if the time is appropriate and the word is the current word
     * in the script, change the highlighting on the screen and reset the recognizer to anticipate
     * the next word in the script.
     *
     * If there are no more words in the script to be read, exit the game with a successful status.
     *
     * @param hypothesis PocketSphinx's best guess about what was said.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis){
        if (hypothesis == null){
            return;
        }

        Log.d(TAG, "hypothesis == " + hypothesis.getHypstr());
        //If timing is appropriate
        if(recognizerInitialized){
            //get the current word
            String currentWord = scriptWords[0];

            //If first word in hypothesis is current word in script
            if(hypothesis.getHypstr().split(" ")[0]
                    .equals(currentWord
                            .toLowerCase().replaceAll("[^a-zA-Z ]", ""))){

                //add this word to the highlighted script
                highlightScript += currentWord + " ";

                //remove this word from the unhighlighted script
                //(add +1 to account for space)
                try {
                    currentScript = currentScript.substring(currentWord.length() + 1);
                }catch (StringIndexOutOfBoundsException e){
                    currentScript = "";
                }

                cycleCount++;

                //if there is a word after this one
                Log.d(TAG, "scriptWords.length == " + scriptWords.length);
                if(!currentScript.equals("")) {
                    //reset the recognizer with the next word
                    scriptWords = Arrays.copyOfRange(scriptWords, 1, scriptWords.length);

                //If there are no more words, exit with successful status
                } else {
                    killIfCountHigh(ACTIVITY_NAME, RESULT_OK,1);
                }
            }
            resetRecognizer();
        }
    }

    /**
     * Resets the voice recognition engine.
     */
    private void resetRecognizer() {
        recognizer.stop();
        recognizer.startListening(scriptWords[0].toLowerCase().replaceAll("[^a-zA-Z ]", ""));
    }


    /**
     * Gets a random script from the XML file containing all possible scripts.
     *
     * @return String containing randomly selected script
     */
    private String getScriptFromResources(){
        String newScript = "";
        String[] allScripts = getResources().getStringArray(R.array.scripts);
        int randId;

        randId = Numbers.randInt(MIN_SCRIPT, MAX_SCRIPT) - 1;
        newScript = allScripts[randId];

        return newScript;
    }

    /**
     * Converts all the words in the script to lowercase for the sake of the voice engine.
     *
     * @return All words in script, in lowercase
     */
    private String[] getScriptWords(){
        String[] allWords = currentScript.split(" ");
        for(int i = 0; i < allWords.length; i++){
            allWords[i] = allWords[i].toLowerCase().replaceAll("[^a-zA-Z ]", "");
        }
        return allWords;
    }

    /**
     * Drawing class responsible for drawing text with highlighting to screen. Updates as new words
     * become highlighted by the voice recognition.
     */
    private class ScriptReadingView extends DrawView{
        /**
         * How far down to push the text from the top of the screen.
         */
        private final int TOP_OFFSET = getScaled(100);

        /**
         * Used to wrap the text to the size of the device screen and update screen with new text
         */
        private DynamicLayout textWrapper;

        /**
         * For styling text on the canvas
         */
        private TextPaint textPaint, highlightPaint;

        /**
         * Script to display in DynamicLayout. Needs to be SpannableStringBuilder
         * for proper use of DynamicLayout. Unclear why.
         */
        private SpannableStringBuilder scriptText;

        /**
         * Instruction card to display while waiting for recognizer to initialize
         */
        private Drawable instructionBg;

        /**
         * Background image to display during gameplay.
         */
        private Drawable gameBg;

        /**
         * Sets up graphics-related objects.
         *
         * @param context The application context
         * @param ga      Associated Game Activity, used to access timer methods.
         */
        public ScriptReadingView(Context context, GameActivity ga) {
            super(context, ga);

            setUpPaints();

            instructionBg = getResources().getDrawable(R.drawable.ic_script_instructions);
            gameBg = getResources().getDrawable(R.drawable.ic_script_bg);

            scriptText = new SpannableStringBuilder(currentScript);

            textWrapper = new DynamicLayout(scriptText,
                    textPaint,
                    getScreenWidth() - getPaddingLeft() - getPaddingRight(),
                    Layout.Alignment.ALIGN_CENTER,
                    1,
                    0,
                    false);

        }

        /**
         * Draws game-specific graphics to the screen
         */
        @Override
        protected void doDrawing() {
            if(recognizerInitialized) {
                //Update the script highlighting based on voice recognition
                updateScript();

                //Draw text
                canvas.save();
                canvas.translate(0, (textWrapper.getHeight() / 2) + TOP_OFFSET);
                textWrapper.draw(canvas);
                canvas.restore();
            }
        }

        /**
         * Returns the instruction card to display while waiting for start button press.
         *
         * @return Instruction card
         */
        public Drawable getInstructionBg(){
            return instructionBg;
        }

        /**
         * Returns the background to display during gameplay.
         *
         * @return gameplay background
         */
        public Drawable getGameBg(){
            return gameBg;
        }

        /**
         * Sets up paints for use in drawing to canvas
         */
        private void setUpPaints(){
            textPaint = new TextPaint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(70);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setAntiAlias(true);
            highlightPaint = new TextPaint();
            highlightPaint.setColor(Color.RED);
            highlightPaint.setTextSize(50);
            highlightPaint.setTextAlign(Paint.Align.CENTER);
            highlightPaint.setAntiAlias(true);
        }

        /**
         * Update the visible script based on the voice recognition's progress
         */
        protected void updateScript(){
            scriptText.clear();
            scriptText.append(highlightScript);
            scriptText.setSpan(new ForegroundColorSpan(Color.RED),
                    0, highlightScript.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            scriptText.append(currentScript);
        }
    }
}
