package com.avarsava.stuttersupport;

/**
 * @author Alexis Varsava <av11sl@brocku.ca>
 * @version 1.6
 * @since   1.6
 *
 * List of valid activity names.
 *
 * TODO: change all ACTIVITY_NAME constants to these
 */

public enum ACTIVITY {
    DEEP_BREATHE, BASKETBALL, SCRIPT_READING, TRAIN_GAME, THOUGHT_TRACKER;

    public String toString(){
        return name();
    }
}
