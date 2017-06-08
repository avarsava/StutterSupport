package com.example.myself.stuttersupport;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static android.transition.Fade.OUT;

public class DeepBreatheActivity extends AppCompatActivity {
    private enum STATES {BREATHEIN, BREATHEOUT}
    private final Long BREATHE_IN_TIME = 7000L;
    private final Long BREATHE_OUT_TIME = 11000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_breathe);
    }

    @Override
    protected void onResume(){
        super.onResume();
        doBreathing();
    }

    private void doBreathing(){
        DeepBreather db;
        for(int i = 0; i < 5; i++) {
            db = new DeepBreather();
            db.execute(BREATHE_IN_TIME, BREATHE_OUT_TIME);
        }
    }

    private class DeepBreather extends AsyncTask<Long, Void, Void>{
        private STATES state;
        private String bi, bo;
        private TextView textBox;

        @Override
        protected void onPreExecute(){
            state = STATES.BREATHEIN;
            bi = getResources().getString(R.string.breathe_in);
            bo = getResources().getString(R.string.breathe_out);
            textBox = (TextView) findViewById(R.id.textView2);
        }

        @Override
        protected Void doInBackground(Long... params) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if (textBox.getText().equals(bi)){
                textBox.setText(bo);
            } else {
                textBox.setText(bi);
            }
        }
    }
}


