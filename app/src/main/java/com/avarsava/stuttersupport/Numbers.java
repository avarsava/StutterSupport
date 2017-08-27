package com.avarsava.stuttersupport;

import java.util.Random;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Provides numeric functions.
 */

public class Numbers {
    /**
     * Generates a random integer within a specified range.
     *
     * @param min Smallest integer result can be.
     * @param max Largest integer result can be.
     * @return Randomly generated integer
     */
    public static int randInt(int min, int max) {
        int randomInt;
        Random random = new Random();

        randomInt = random.nextInt((max - min) + 1) + min;;

        return randomInt;
    }
}
