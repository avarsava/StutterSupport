package com.example.myself.stuttersupport;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class DeepBreatheActivity extends GameActivity {
    private Drawable inhaleBg;
    private Drawable exhaleBg;

    private enum STATE {INHALE, EXHALE}

    private long inhaleDuration;
    private long exhaleDuration;
    private STATE currentState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentState = STATE.INHALE;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        maxCycles = Integer.valueOf(prefs.getString("noOfBreaths", "1"));
        inhaleDuration = Long.valueOf(prefs.getString("inhaleLength", "7")) * 1000;
        exhaleDuration = Long.valueOf(prefs.getString("exhaleLength", "11")) * 1000;
        inhaleBg =
                getResources().getDrawable(R.drawable.ic_deep_breathe_inhale);
        exhaleBg =
                getResources().getDrawable(R.drawable.ic_deep_breathe_exhale);
        screen = new DeepBreatheView(this, this);
        setContentView(screen);
    }

    private String getInstructions() {
        String instruction = "";
        switch (currentState) {
            case INHALE:
                instruction = "Breathe in...";
                break;
            case EXHALE:
                instruction = "...Breathe out";
                break;
        }
        return instruction;
    }

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

    private class DeepBreatheView extends DrawView {
        private Paint circlePaint, textPaint;
        private float circleHeight, circleWidth, maxRadius, minRadius;

        public DeepBreatheView(Context context, GameActivity ga) {
            super(context, ga);

            setUpPaints();
            float width = getScreenWidth();
            circleHeight = getScreenHeight() * 0.75f;
            circleWidth = width / 2;
            minRadius = width * 0.1f;
            maxRadius = width * 0.3f;
        }

        private void setUpPaints() {
            circlePaint = new Paint();
            textPaint = new Paint();
            circlePaint.setColor(Color.MAGENTA);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(50f);
            textPaint.setTextAlign(Paint.Align.CENTER);
            background = inhaleBg;
        }

        @Override
        protected void doDrawing() {
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


