package com.example.myself.stuttersupport;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class TrainGameActivity extends AppCompatActivity implements RecognitionListener{
    private final int MIN_PAIR = 1;
    private final int MAX_PAIR = 1;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private enum STATE {CALL, WAIT, RESP};

    private SharedPreferences prefs;
    private DrawView screen;
    private SpeechRecognizer recognizer;
    private int maxCycles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        maxCycles = Integer.valueOf(prefs.getString("noOfPairs", "1"));
        screen = new TrainGameView(this);
        setContentView(screen);

        //Speech recognizer permission setup
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        //set up speech recognition
        runRecognizerSetup();
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
        if(recognizer != null){
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        //nothing
    }

    @Override
    public void onEndOfSpeech() {
        //nothing
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null){
            return;
        }

        //TODO: Have this affect the game
        String text = hypothesis.getHypstr();
        if(text.equals("hello")){
            Toast.makeText(getApplicationContext(), "Hello!", Toast.LENGTH_SHORT).show();
            resetRecognizer();
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        //nothing
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeout() {
        //nothing
    }

    private void runRecognizerSetup() {
        new AsyncTask<Void, Void, Exception>() {
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(TrainGameActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            protected void onPostExecute(Exception result){
                if (result != null){
                    Toast.makeText(getApplicationContext(),
                            "Failed to init recognizer!", Toast.LENGTH_SHORT).show();
                } else {
                    recognizer.startListening("kws");
                }
            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) throws IOException{
        //Set up recognizer
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir)
                .getRecognizer();
        recognizer.addListener(this);

        //Switch to keyword search
        //TODO: get the word we're looking for from the pair
        recognizer.addKeyphraseSearch("kws","hello");
    }

    private void resetRecognizer() {
        recognizer.stop();
        recognizer.startListening("kws");
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

    protected class TrainGameView extends DrawView {
        SharedPreferences prefs;
        private STATE currentState;
        private int cycleCount;
        private final long CALL_DURATION = 3000L;
        private final long WAIT_DURATION;
        private final long RESP_DURATION = 3000L;
        private final long CANCEL_DURATION = 3000L;
        private String[] currentPair;
        private String currentString;
        private Paint blackPaint, whitePaint;
        private boolean successful = false;


        public TrainGameView(Context context) {
            super(context);
            setUpPaints();
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            WAIT_DURATION = Long.valueOf(prefs.getString("waitTime", "10"))*1000;
            currentState = STATE.CALL;
            currentPair = getPair();
            currentString = currentPair[0];
            cycleCount = 0;
        }

        public STATE getCurrentState(){
            return currentState;
        }

        public void setCurrentString(String newString){
            currentString = newString;
        }

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
                    if(getElapsedTime()/WAIT_DURATION >= 1.0){
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

        private void setUpPaints(){
            blackPaint = new Paint();
            whitePaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            whitePaint.setColor(Color.WHITE);
        }

        public void cancelCycle() {
            resetTimer();
            while(getElapsedTime()/CANCEL_DURATION < 1.0){
                //TODO: Is there a cleaner way to wait?
            }
            setResult(RESULT_CANCELED);
            finish();
        }

        public void markSuccessful() {
            successful = true;
        }
    }

    private class TrainTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TrainGameView tgv = (TrainGameView) v;
            STATE currentState = tgv.getCurrentState();

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(currentState == STATE.CALL || currentState == STATE.WAIT){
                    tgv.setCurrentString("HOLD UR HORSES");
                    tgv.cancelCycle();
                } else {
                    tgv.setCurrentString("GOOD JOB!!");
                    tgv.markSuccessful();
                }
                return true;
            }

            return false;
        }
    }
}
