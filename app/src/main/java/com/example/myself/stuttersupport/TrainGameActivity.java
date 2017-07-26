package com.example.myself.stuttersupport;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.os.Bundle;
import edu.cmu.pocketsphinx.Hypothesis;

public class TrainGameActivity extends GameActivity{
    private final long CALL_DURATION = 3000L;
    private final long RESP_DURATION = 3000L;
    private final long CANCEL_DURATION = 3000L;
    private final int MIN_PAIR = 1;
    private final int MAX_PAIR = 1;

    private enum STATE {NOTREADY, CALL, WAIT, RESP};

    private String[] currentPair;
    private String currentString;
    private long waitDuration;
    private boolean successful = false;
    private STATE currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        maxCycles = Integer.valueOf(prefs.getString("noOfPairs", "1"));
        waitDuration = Long.valueOf(prefs.getString("waitTime", "10"))*1000;
        currentState = STATE.NOTREADY;
        currentPair = getPair();
        currentString = "Please wait...";
        screen = new TrainGameView(this, this);
        setContentView(screen);

        //set up speech recognition
        runRecognizerSetup(currentPair[1]);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null){
            return;
        }

        String text = hypothesis.getHypstr();
        if(text.equals(currentPair[1])){
            processSpeech();
        }
        resetRecognizer();
    }

    @Override
    protected void recognizerReady(){
        currentState = STATE.CALL;
        currentString = currentPair[0];
    }

    private String[] getPair(){
        String[] pair = new String[2];
        int pairId = Numbers.randInt(MIN_PAIR, MAX_PAIR) - 1;

        pair[0] = getResources().getStringArray(R.array.calls)[pairId];
        pair[1] = getResources().getStringArray(R.array.resps)[pairId];

        return pair;
    }

    public void cancelCycle() {
        resetTimer();
        while(getElapsedTime()/CANCEL_DURATION < 1.0){
            //TODO: Is there a cleaner way to wait?
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    private void processSpeech(){
        if(currentState == STATE.RESP){
            currentString = "Good Job!";
            successful = true;
        } else {
            currentString = "Hold on there bucko!";
            cancelCycle();
        }
    }

    private void resetRecognizer() {
        recognizer.stop();
        recognizer.startListening("kws");
    }

    private void switchStateIfNecessary(){
        switch(currentState){
            case CALL:
                if(getElapsedTime()/CALL_DURATION >= 1.0){
                    currentState = STATE.WAIT;
                    currentString = "";
                    resetTimer();
                }
                break;
            case WAIT:
                if(getElapsedTime()/ waitDuration >= 1.0){
                    currentState = STATE.RESP;
                    currentString = currentPair[1];
                    resetTimer();
                }
                break;
            case RESP:
                if(getElapsedTime()/RESP_DURATION >= 1.0){
                    if(!successful){
                        cancelCycle();
                    }
                    currentState = STATE.CALL;
                    resetTimer();
                    cycleCount++;
                    successful = false;
                }
                break;
        }
    }

    protected class TrainGameView extends DrawView {
        private Paint blackPaint, whitePaint;

        public TrainGameView(Context context, GameActivity ga) {
            super(context, ga);
            setUpPaints();
        }

        @Override
        protected void doDrawing(){
            //draw bg
            canvas.drawRect(0,
                    0,
                    getScreenWidth(),
                    getScreenHeight(),
                    whitePaint);

            //Write current String
            //TODO: This should be more drawing stuff
            canvas.drawText(currentString,
                    getScreenWidth()/2,
                    getScreenHeight()/2,
                    blackPaint);

            //cycle end logic
            switchStateIfNecessary();
            killIfCountHigh(cycleCount);
        }

        private void setUpPaints(){
            blackPaint = new Paint();
            whitePaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            whitePaint.setColor(Color.WHITE);
        }
    }
}
