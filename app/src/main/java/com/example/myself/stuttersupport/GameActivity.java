package com.example.myself.stuttersupport;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * Created by Myself on 7/25/2017.
 */

public abstract class GameActivity extends AppCompatActivity implements RecognitionListener {
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private final String KEYPHRASE_SEARCH = "kws";

    private long startTime;

    protected int maxCycles;
    protected int cycleCount;
    protected SpeechRecognizer recognizer = null;
    protected SharedPreferences prefs;
    protected DrawView screen;

    @Override
    protected void onCreate(Bundle savedStateInstance){
        super.onCreate(savedStateInstance);
        cycleCount = 0;

        //Speech recognizer permission setup
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (screen != null) screen.resume();
    }

    @Override
    public void onPause(){
        super.onPause();
        if (screen != null) screen.pause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (screen != null) screen.pause();
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
        //nothing
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        //nothing
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    @Override
    public void onTimeout() {
        //nothing
    }

    protected abstract void startButtonPressed();

    public long getElapsedTime(){
        return System.currentTimeMillis() - startTime;
    }

    protected void killIfCountHigh(int cycleCount) {
        if(cycleCount >= maxCycles) {
            setResult(RESULT_OK);
            finish();
        }
    }

    protected void recognizerReady(){
        screen.toggleButton();
    }

    public void resetTimer(){
        startTime = System.currentTimeMillis();
    }

    protected void runRecognizerSetup(final String keyword) {
        if(keyword == null){
            recognizerReady();
            return;
        }

        new AsyncTask<Void, Void, Exception>() {
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(GameActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir, keyword);
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
                    recognizerReady();
                    recognizer.startListening(KEYPHRASE_SEARCH);
                }
            }
        }.execute();
    }

    protected void setupRecognizer(File assetsDir, String keyword) throws IOException{
        //Set up recognizer
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir)
                .getRecognizer();
        recognizer.addListener(this);

        //Switch to keyword search
        //TODO: This means it won't detect any other words. Figure out how to have that happen
        recognizer.addKeyphraseSearch(KEYPHRASE_SEARCH, keyword);
    }
}
