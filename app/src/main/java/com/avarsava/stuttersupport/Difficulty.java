package com.avarsava.stuttersupport;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Reports on word list boundaries for different difficulty levels.
 */

public class Difficulty {
    /**
     * First word in list for Difficulty Level 1
     */
    private static final int LEVEL_ONE_MIN = 1;

    /**
     * Last word in list for Difficulty Level 1
     */
    private static final int LEVEL_ONE_MAX = 52;

    /**
     * First word in list for Difficulty Level 2
     */
    private static final int LEVEL_TWO_MIN = 53;

    /**
     * Last word in list for Difficulty Level 2
     */
    private static final int LEVEL_TWO_MAX = 198;

    /**
     * First word in list for Difficulty Level 3
     */
    private static final int LEVEL_THREE_MIN = 199;

    /**
     * Last word in list for Difficulty Level 3
     */
    private static final int LEVEL_THREE_MAX = 330;

    /**
     * Gets the position of the first word for the requested Difficulty level. The Difficulty level
     * should be pulled from the SharedPreferences which the user can set.
     *
     * @param level Integer representing the requested difficulty level.
     * @return Position of the first word in the list if level is valid, Integer.MAX_VALUE otherwise
     */
    public static int getMinForLevel(int level){
        switch(level){
            case 1:
                return LEVEL_ONE_MIN;
            case 2:
                return LEVEL_TWO_MIN;
            case 3:
                return LEVEL_THREE_MIN;
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Gets the position of the last word for the requested Difficulty level. The Difficulty level
     * should be pulled from the SharedPreferences which the user can set.
     *
     * @param level Integer representing the requested difficulty level.
     * @return Position of the last word in the list if level is valid, Integer.MAX_VALUE otherwise
     */
    public static int getMaxForLevel(int level){
        switch(level){
            case 1:
                return LEVEL_ONE_MAX;
            case 2:
                return LEVEL_TWO_MAX;
            case 3:
                return LEVEL_THREE_MAX;
        }
        return Integer.MAX_VALUE;
    }
}
