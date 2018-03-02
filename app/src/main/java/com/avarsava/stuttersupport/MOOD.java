package com.avarsava.stuttersupport;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Details the moods possible to enter in the Thought Tracker activity.
 */

public enum MOOD {
    //Moods
    HAPPY, SAD, ANGRY, TIRED, EMBARRASSED, EXCITED, NERVOUS,

    //Rating of one to ten
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN;

    public String toString(){
        return this.name();
    }
}
