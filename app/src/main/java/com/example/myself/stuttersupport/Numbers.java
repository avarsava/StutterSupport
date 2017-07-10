package com.example.myself.stuttersupport;

import java.util.Random;

/**
 * Created by Myself on 7/10/2017.
 */

public class Numbers {
    public static int randInt(int min, int max) {
        int randomInt;
        Random random = new Random();

        randomInt = random.nextInt((max - min) + 1) + min;;

        return randomInt;
    }
}
