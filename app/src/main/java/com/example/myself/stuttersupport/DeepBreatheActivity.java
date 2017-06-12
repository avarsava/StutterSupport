package com.example.myself.stuttersupport;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DeepBreatheActivity extends AppCompatActivity {
    private enum STATE {INHALE, EXHALE}
    private final Long BREATHE_IN_TIME = 7000L;
    private final Long BREATHE_OUT_TIME = 11000L;
    private DrawView screen;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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

    private class DeepBreatheView extends DrawView{
        private final long INHALE_DURATION = 7000l;
        private final long EXHALE_DURATION = 11000l;
        private Paint whitePaint;
        private STATE currentState;
        private float circleHeight, circleWidth, maxRadius, minRadius;

        public DeepBreatheView(Context context) {
            super(context);
            currentState = STATE.INHALE;
            whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);
            circleHeight = getScreenHeight()/2;
            circleWidth = getScreenWidth()/2;
            minRadius = 50f; //These will likely get changed later
            maxRadius = 200f;
        }

        @Override
        protected void doDrawing(){
            //Draw a blue background
            canvas.drawColor(Color.BLUE);

            //Draw a white circle
            canvas.drawCircle(circleWidth, circleHeight, animatedRadius(), whitePaint);

            switchStateIfNecessary();
        }

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
                        resetTimer();
                    }
                    break;
                case EXHALE:
                    if(getElapsedTime()/EXHALE_DURATION >= 1.0){
                        currentState = STATE.INHALE;
                        resetTimer();
                    }
                    break;
            }
        }
    }
}


