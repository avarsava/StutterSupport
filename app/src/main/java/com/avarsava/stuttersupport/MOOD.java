package com.avarsava.stuttersupport;

import android.graphics.Color;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.5
 * @since   1.1
 *
 * Details the moods possible to enter in the Thought Tracker activity.
 * ONE should be understood to indicate lowest SEVERITY (best mood)
 */

public enum MOOD {
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN;

    /**
     * Converts integer to Mood object
     *
     * @param i Integer from 1-10 to return as Mood
     * @return Mood object for value 1-10, null if param is <1 or >10
     */
    public static MOOD valueOf(int i){
        switch(i){
            case 1: return ONE;
            case 2: return TWO;
            case 3: return THREE;
            case 4: return FOUR;
            case 5: return FIVE;
            case 6: return SIX;
            case 7: return SEVEN;
            case 8: return EIGHT;
            case 9: return NINE;
            case 10: return TEN;
        }
        return null;
    }

    /**
     * Converts Mood to String
     *
     * @return name of Mood as String
     */
    public String toString(){
        return name();
    }

    /**
     * Returns integer value of Mood
     *
     * @return Int value of Mood if between 1-10. Else, returns 0
     */
    public int getIntValue(){
        switch(this){
            case ONE: return 1;
            case TWO: return 2;
            case THREE: return 3;
            case FOUR: return 4;
            case FIVE: return 5;
            case SIX: return 6;
            case SEVEN: return 7;
            case EIGHT: return 8;
            case NINE: return 9;
            case TEN: return 10;
        }
        return 0;
    }

    /**
     * Produces a colour representing the mood on a scale from blue-green to orange.
     *
     * @return integer representing Java Color
     */
    public int getMoodColor(){
        if (this == null) return Color.WHITE;

        double percentage = getIntValue() * 0.1;

        return Color.rgb((int)(255 * percentage), 100, 100);
    }
}
