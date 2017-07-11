package com.example.myself.stuttersupport;

import android.content.Context;
import android.content.SharedPreferences;
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
        int pairId = Numbers.randInt(MIN_PAIR, MAX_PAIR);

        pair[0] = getResources().getStringArray(R.array.calls)[pairId];
        pair[1] = getResources().getStringArray(R.array.resps)[pairId];

        return pair;
    }

    private class TrainGameView extends DrawView {
        public TrainGameView(Context context) {
            super(context);
        }

        protected void doDrawing(){
            
        }
    }
}
