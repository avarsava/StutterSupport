package com.example.myself.stuttersupport;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class DeepBreatheActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private enum STATE {INHALE, EXHALE}
    private DrawView screen;
    private int maxCycles;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        maxCycles = Integer.valueOf(prefs.getString("noOfBreaths", "1"));
        screen = new DeepBreatheView(this);
        setContentView(screen);
    }

    @Override
    public void onResume(){
        super.onResume();
        screen.resume();
    }

    @Override
    public void onPause(){
        super.onPause();
        screen.pause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        screen.pause();
        screen = null;
    }

    protected void killIfCountHigh(int cycleCount) {
        if(cycleCount >= maxCycles) {
            setResult(RESULT_OK);
            finish();
        }
    }


    private class DeepBreatheView extends DrawView{
        SharedPreferences prefs;
        private final long INHALE_DURATION;
        private final long EXHALE_DURATION;
        private Paint circlePaint, textPaint;
        private STATE currentState;
        private float circleHeight, circleWidth, maxRadius, minRadius;
        private int cycleCount;
        private Drawable background;
        private final Drawable INHALE_BG =
                getResources().getDrawable(R.drawable.ic_deep_breathe_inhale);
        private final Drawable EXHALE_BG =
                getResources().getDrawable(R.drawable.ic_deep_breathe_exhale);

        public DeepBreatheView(Context context) {
            super(context);
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            INHALE_DURATION = Long.valueOf(prefs.getString("inhaleLength", "7"))*1000;
            EXHALE_DURATION = Long.valueOf(prefs.getString("exhaleLength", "11"))*1000;
            currentState = STATE.INHALE;
            setUpPaints();
            float width = getScreenWidth();
            circleHeight = getScreenHeight()*0.75f;
            circleWidth = width/2;
            minRadius = width*0.1f; //These will likely get changed later
            maxRadius = width*0.3f;
            cycleCount = 0;
        }

        private void setUpPaints() {
            circlePaint = new Paint();
            textPaint = new Paint();
            circlePaint.setColor(Color.MAGENTA);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(50f);
            textPaint.setTextAlign(Paint.Align.CENTER);
            background = INHALE_BG;
        }

        @Override
        protected void doDrawing(){
            //Draw the background
            background.setBounds(0, 0,
                    getScreenWidth(), getScreenHeight());
            background.draw(canvas);

            //Draw a white circle
            canvas.drawCircle(circleWidth, circleHeight, animatedRadius(), circlePaint);

            //Draw text over circle
            canvas.drawText(getInstructions(), circleWidth, circleHeight, textPaint);

            switchStateIfNecessary();
            killIfCountHigh(cycleCount);
        }

        private String getInstructions() {
            String instruction = "";
            switch(currentState){
                case INHALE:
                    instruction = "Breathe in...";
                    break;
                case EXHALE:
                    instruction = "...Breathe out";
                    break;
            }
            return instruction;
        }

        //TODO: Probably clean this up
        private float animatedRadius() {
            float newRadius = 0f;

            switch(currentState){
                case INHALE:
                    newRadius = ((maxRadius - minRadius)*getElapsedTime()/INHALE_DURATION)
                            + minRadius;
                    if (newRadius > maxRadius) newRadius = maxRadius;
                    break;
                case EXHALE:
                    newRadius = ((minRadius - maxRadius)*getElapsedTime()/EXHALE_DURATION)
                            + maxRadius;
                    if (newRadius < minRadius) newRadius = minRadius;
                    break;
            }

            return newRadius;
        }

        //TODO: There's probably a cleaner way to do this
        private void switchStateIfNecessary(){
            switch(currentState){
                case INHALE:
                    if (getElapsedTime()/INHALE_DURATION >= 1.0){
                        currentState = STATE.EXHALE;
                        background = EXHALE_BG;
                        resetTimer();
                    }
                    break;
                case EXHALE:
                    if(getElapsedTime()/EXHALE_DURATION >= 1.0){
                        currentState = STATE.INHALE;
                        background = INHALE_BG;
                        resetTimer();
                        cycleCount++;
                    }
                    break;
            }
        }
    }
}


