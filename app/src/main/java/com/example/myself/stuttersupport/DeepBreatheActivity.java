package com.example.myself.stuttersupport;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DeepBreatheActivity extends AppCompatActivity {
    private enum STATES {BREATHEIN, BREATHEOUT}
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
        private Paint whitePaint;

        public DeepBreatheView(Context context) {
            super(context);
            whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);
        }

        @Override
        protected void doDrawing(){
            //Draw a blue background
            canvas.drawColor(Color.BLUE);

            //Draw a white circle
            canvas.drawCircle(200f, 200f, frame*10, whitePaint);
        }
    }
}


