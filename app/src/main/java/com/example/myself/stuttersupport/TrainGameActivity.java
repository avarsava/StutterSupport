package com.example.myself.stuttersupport;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import edu.cmu.pocketsphinx.Hypothesis;

public class TrainGameActivity extends GameActivity{
    private final long CALL_DURATION = 3000L;
    private final long RESP_DURATION = 3000L;
    private final long CANCEL_DURATION = 3000L;
    private final int MIN_PAIR = 1;
    private final int MAX_PAIR = 5;

    private enum STATE {NOTREADY, CALL, WAIT, RESP};

    private String currentString, displayString;
    private String[] usedStrings;
    private long waitDuration;
    private int passed = 0;
    private boolean successful = false;
    private STATE currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        maxCycles = Integer.valueOf(prefs.getString("noOfPairs", "3"));
        usedStrings = new String[maxCycles];
        waitDuration = Long.valueOf(prefs.getString("waitTime", "10"))*1000;
        currentState = STATE.NOTREADY;
        currentString = getString();
        displayString = currentString;
        screen = new TrainGameView(this, this);
        setContentView(screen);

        //set up speech recognition
        runRecognizerSetup(currentString, getResources().getStringArray(R.array.calls));
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null){
            return;
        }

        String text = hypothesis.getHypstr();
        if(text.equals(currentString)){
            processSpeech();
        }
        resetRecognizer();
    }

    @Override
    protected void startButtonPressed(){
        currentState = STATE.CALL;
        resetTimer();
    }

    private String getString(){
        String potentialString = "";
        List<String> usedStringsList = Arrays.asList(usedStrings);
        int randId;

        do {
            randId = Numbers.randInt(MIN_PAIR, MAX_PAIR) - 1;
            potentialString = getResources().getStringArray(R.array.calls)[randId];
        } while (usedStringsList.contains(potentialString));

        usedStrings[cycleCount] = potentialString;
        return potentialString;
    }

    private int calculateSuccess(){
        if (passed == maxCycles){
            return RESULT_OK;
        } else {
            return RESULT_CANCELED;
        }
    }

    public void cancelCycle() {
        resetTimer();
        while(getElapsedTime()/CANCEL_DURATION < 1.0){
            //TODO: Is there a cleaner way to wait?
        }

    }

    private void processSpeech(){
        if(currentState == STATE.RESP){
            displayString = "Good Job!";
            successful = true;
            passed++;
        } else {
            displayString = "Hold on there bucko!";
            cancelCycle();
        }
    }

    private void resetRecognizer() {
        recognizer.stop();
        recognizer.startListening(currentString);
    }

    private void switchStateIfNecessary(){
        switch(currentState){
            case CALL:
                if(getElapsedTime()/CALL_DURATION >= 1.0){
                    currentState = STATE.WAIT;
                    displayString = "";
                    resetTimer();
                }
                break;
            case WAIT:
                if(getElapsedTime()/ waitDuration >= 1.0){
                    currentState = STATE.RESP;
                    displayString = currentString;
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
                    if (cycleCount != maxCycles) currentString = getString();
                    displayString = currentString;
                    resetRecognizer();
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
            canvas.drawText(displayString,
                    getScreenWidth()/2,
                    getScreenHeight()/2,
                    blackPaint);

            //cycle end logic
            switchStateIfNecessary();
            killIfCountHigh(calculateSuccess());
        }

        private void setUpPaints(){
            blackPaint = new Paint();
            whitePaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            whitePaint.setColor(Color.WHITE);
        }
    }
}
