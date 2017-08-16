package com.example.myself.stuttersupport;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * An activity in which the player is tasked, without timing or pressure, to read aloud a script
 * taken from an XML file. The words of the script are shown on the screen and the words are
 * highlighted as they are read.
 */

public class ScriptReadingActivity extends GameActivity {
    /**
     * The starting point to identify potential scripts to feed to the user.
     */
    private final int MIN_SCRIPT = 1;

    /**
     * The ending point to identify potential scripts to feed to the user.
     */
    private final int MAX_SCRIPT = 1;

    /**
     * The current script which the user must read out. Drawn from XML.
     */
    private String currentScript;

    /**
     * The part of the script which the user has already read. Updated by voice recognition logic.
     */
    private String highlightScript = "";

    /**
     * Set up objects necessary for gameplay. Called automatically when Activity starts
     *
     * @param savedInstanceState used for internal Android communication
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        currentScript = getScriptFromResources();

        screen = new ScriptReadingView(this, this);
        setContentView(screen);

        runRecognizerSetup(null, null);
    }

    /**
     * When the Start Button is pressed, starts the activity
     */
    @Override
    protected void startButtonPressed() {

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

    private class ScriptReadingView extends DrawView{
        /**
         * Used to wrap the text to the size of the device screen and update screen with new text
         */
        private DynamicLayout textWrapper;

        /**
         * For styling text on the canvas
         */
        private TextPaint textPaint;

        /**
         * Script to display in DynamicLayout. Needs to be SpannableStringBuilder
         * for proper use of DynamicLayout. Unclear why.
         */
        private SpannableStringBuilder scriptText;

        /**
         * White paint for drawing background
         */
        private Paint whitePaint;

        /**
         * Sets up graphics-related objects.
         *
         * @param context The application context
         * @param ga      Associated Game Activity, used to access timer methods.
         */
        public ScriptReadingView(Context context, GameActivity ga) {
            super(context, ga);

            textPaint = new TextPaint();
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(50);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setAntiAlias(true);
            whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);

            scriptText = new SpannableStringBuilder(currentScript);

            textWrapper = new DynamicLayout(scriptText,
                    textPaint,
                    getScreenWidth() - getPaddingLeft() - getPaddingRight(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1,
                    0,
                    false);

        }

        @Override
        protected void doDrawing() {
            //Draw background
            canvas.drawRect(0, 0, getScreenWidth(), getScreenHeight(), whitePaint);

            //Update the script highlighting based on voice recognition
            updateScript();

            //Draw text
            canvas.save();
            canvas.translate(textWrapper.getWidth()/2, textWrapper.getHeight()/2);
            textWrapper.draw(canvas);
            canvas.restore();
        }

        /**
         * Update the visible script based on the voice recognition's progress
         */
        protected void updateScript(){
            scriptText.clear();
            scriptText.append(highlightScript + currentScript);
        }
    }
}
