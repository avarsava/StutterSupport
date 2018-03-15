package com.avarsava.stuttersupport;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Details the moods possible to enter in the Thought Tracker activity.
 */

public enum MOOD {
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN;

    public String toString(){
        return name();
    }

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
}
