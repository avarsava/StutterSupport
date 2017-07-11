package com.example.myself.stuttersupport;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TrainGameActivity extends AppCompatActivity {
    private final int MIN_PAIR = 1;
    private final int MAX_PAIR = 1;
    private enum STATE {CALL, WAIT, RESP};

    private SharedPreferences prefs;
    private DrawView screen;
    private int maxCycles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        maxCycles = Integer.valueOf(prefs.getString("noOfPairs", "1"));
        screen = new TrainGameView(this);
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

    private String[] getPair(){
        String[] pair = new String[2];
        int pairId = Numbers.randInt(MIN_PAIR, MAX_PAIR) - 1;

        pair[0] = getResources().getStringArray(R.array.calls)[pairId];
        pair[1] = getResources().getStringArray(R.array.resps)[pairId];

        return pair;
    }

    private class TrainGameView extends DrawView {
        SharedPreferences prefs;
        private STATE currentState;
        private int cycleCount;
        private final long WAIT_DURATION;
        private String[] currentPair;
        private String currentString;
        private Paint blackPaint, whitePaint;


        public TrainGameView(Context context) {
            super(context);
            setUpPaints();
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            WAIT_DURATION = Long.valueOf(prefs.getString("waitTime", "1"))*1000;
            currentState = STATE.CALL;
            currentPair = getPair();
            currentString = currentPair[0];
            cycleCount = 0;
        }

        protected void doDrawing(){
            //draw bg
            canvas.drawRect(0,
                    0,
                    getScreenWidth(),
                    getScreenHeight(),
                    whitePaint);

            //Write current String
            canvas.drawText(currentString,
                    getScreenWidth()/2,
                    getScreenHeight()/2,
                    blackPaint);

            switchStateIfNecessary();
            killIfCountHigh(cycleCount);
        }

        private void switchStateIfNecessary(){
            switch(currentState){
                case CALL:
                    if(true){ //TODO: Add this ones own wait
                        currentState = STATE.WAIT;
                        resetTimer();
                    }
                    break;
                case WAIT:
                    if(getElapsedTime()/WAIT_DURATION >= 1.0){
                        currentState = STATE.RESP;
                        currentString = currentPair[1];
                    }
                    break;
                case RESP:
                    break;
            }
        }

        private void setUpPaints(){
            blackPaint = new Paint();
            whitePaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            whitePaint.setColor(Color.WHITE);
        }
    }
}
