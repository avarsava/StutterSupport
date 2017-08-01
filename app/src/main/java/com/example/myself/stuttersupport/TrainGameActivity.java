package com.example.myself.stuttersupport;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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

    private String currentString;
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
        screen = new TrainGameView(this, this);
        screen.setBackgroundImage(((TrainGameView)screen).getInstructionBg());
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
        screen.setBackgroundImage(((TrainGameView)screen).getGameBg());
        resetTimer();
    }

    private String getString(){
        String potentialString;
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
            successful = true;
            passed++;
        } else {
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
                    resetTimer();
                }
                break;
            case WAIT:
                if(getElapsedTime()/ waitDuration >= 1.0){
                    currentState = STATE.RESP;
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
                    resetRecognizer();
                    successful = false;
                }
                break;
        }
    }

    protected class TrainGameView extends DrawView {
        private Paint blackPaint, whitePaint;
        private Drawable instructionBg, gameBg, gameFg;
        private Drawable bgBalloon, fgBalloon, checkmark, qmark, car, happy, sad;

        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();

        public TrainGameView(Context context, GameActivity ga) {
            super(context, ga);
            setUpPaints();
            getDrawables();
        }

        @Override
        protected void doDrawing(){

            if(currentState == STATE.CALL){
                bgBalloon.draw(canvas);
                canvas.drawText(currentString,
                        getScaled(120),
                        getScaled(125),
                        blackPaint);
                happy.draw(canvas);
                gameFg.draw(canvas);

            }else if (currentState == STATE.WAIT){
                car.draw(canvas);
                gameFg.draw(canvas);

            } else if (currentState == STATE.RESP){
                gameFg.draw(canvas);
                bgBalloon.draw(canvas);
                if(successful){
                    fgBalloon.draw(canvas);
                    canvas.drawText(currentString,
                            screenWidth - getScaled(90),
                            screenHeight - getScaled(140),
                            blackPaint);
                    happy.draw(canvas);
                    checkmark.draw(canvas);
                }else{
                    sad.draw(canvas);
                    qmark.draw(canvas);
                }

            }

            //cycle end logic
            switchStateIfNecessary();
            killIfCountHigh(calculateSuccess());
        }

        public Drawable getInstructionBg(){
            return instructionBg;
        }

        public Drawable getGameBg(){
            return gameBg;
        }

        private void setUpPaints(){
            blackPaint = new Paint();
            whitePaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            whitePaint.setColor(Color.WHITE);
            blackPaint.setTextAlign(Paint.Align.CENTER);
            blackPaint.setTextSize(100);
        }

        private void getDrawables(){
            //Get Resources
            Resources resources = getResources();

            //Get drawables from Resources
            instructionBg = resources.getDrawable(R.drawable.ic_train_game_instructions);
            gameBg = resources.getDrawable(R.drawable.ic_train_game_1);
            gameFg = resources.getDrawable(R.drawable.ic_train_game_foreground);
            bgBalloon = resources.getDrawable(R.drawable.ic_bg_balloon);
            fgBalloon = resources.getDrawable(R.drawable.ic_fg_balloon);
            checkmark = resources.getDrawable(R.drawable.ic_checkmar);
            qmark = resources.getDrawable(R.drawable.ic_q_mark);
            car = resources.getDrawable(R.drawable.ic_train_car);
            happy = resources.getDrawable(R.drawable.ic_train_game_happy_face);
            sad = resources.getDrawable(R.drawable.ic_train_game_sad_face);

            //Set boundaries for drawings
            gameFg.setBounds(0, 0, screenWidth, screenHeight);
            bgBalloon.setBounds(0, 0,
                    screenWidth - getScaled(100),
                    screenHeight - getScaled(400));
            fgBalloon.setBounds(screenWidth - getScaled(175),
                    screenHeight - getScaled(300),
                    screenWidth,
                    screenHeight);
            happy.setBounds(screenWidth - getScaled(100),
                    getScaled(80),
                    screenWidth - getScaled(30),
                    getScaled(180));
            sad.setBounds(screenWidth - getScaled(100),
                    getScaled(80),
                    screenWidth - getScaled(30),
                    getScaled(180));
            car.setBounds(0, 0, screenWidth, screenHeight - getScaled(120));
            checkmark.setBounds(getScaled(75), getScaled(50),
                    getScaled(175), getScaled(155));
            qmark.setBounds(getScaled(75), getScaled(50),
                    getScaled(175), getScaled(155));
        }
    }
}
