package com.example.myself.stuttersupport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CarGameActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_game);
    }

    @Override
    protected void recognizerReady() {
        //TODO: Implement
    }
}
