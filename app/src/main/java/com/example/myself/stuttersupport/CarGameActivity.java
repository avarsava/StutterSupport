package com.example.myself.stuttersupport;

import android.os.Bundle;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Runs the Car Game, in which the player must hold sounds for as long as they're on the screen.
 */
public class CarGameActivity extends GameActivity {

    /**
     * Creates the Activity and sets up the initial values of the variables.
     *
     * @param savedInstanceState used internally for Android communication
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_game);
    }

    /**
     * Describes the action to take when the start button is pressed.
     */
    @Override
    protected void startButtonPressed(){
        //TODO: Implement me
    }
}
