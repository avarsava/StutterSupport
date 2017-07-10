package com.example.myself.stuttersupport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TrainGameActivity extends AppCompatActivity {
    private final int MIN_PAIR = 1;
    private final int MAX_PAIR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_game);
    }

    private String[] getPair(){
        String[] pair = new String[2];
        int pairId = Numbers.randomInt(MIN_PAIR, MAX_PAIR);

        pair[0] = getResources().getStringArray(R.array.calls)[pairId];
        pair[1] = getResources().getStringArray(R.array.resps)[pairId];

        return pair;
    }
}
