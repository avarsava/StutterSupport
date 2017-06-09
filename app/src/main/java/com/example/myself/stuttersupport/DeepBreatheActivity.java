package com.example.myself.stuttersupport;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DeepBreatheActivity extends AppCompatActivity {
    private enum STATES {BREATHEIN, BREATHEOUT}
    private final Long BREATHE_IN_TIME = 7000L;
    private final Long BREATHE_OUT_TIME = 11000L;
    private DrawView drawView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        drawView = new DrawView(this);
        setContentView(drawView);
    }

    @Override
    public void onResume(){
        super.onResume();
        drawView.resume();
    }

    @Override
    public void onPause(){
        super.onPause();
        drawView.pause();
    }
}


