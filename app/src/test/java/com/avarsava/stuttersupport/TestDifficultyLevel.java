package com.avarsava.stuttersupport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
/**
 * @author  Aditi Trivedi <at15gp@brocku.ca
 * @version 1.5
 * @since   1.1
 *
 * Tests validity of the Difficulty class using localized testing and reflection.
 */
public class TestDifficultyLevel {

    /**
     * Difficulty object from which to get fields
     */
    Difficulty difficulty;

    /**
     * Parameter to ensure we get the minimum value for difficulty 1
     */
    public int TEST_MIN_VAL_PARAM = 1;

    /**
     * Minimum value retrieved from field
     */
    public int TEST_MIN_VAL;

    /**
     * Parameter to ensure we get the maximum value for difficulty 3
     */
    public int TEST_MAX_VAL_PARAM = 3;

    /**
     * Maximum value retrieved from field
     */
    public int TEST_MAX_VAL;

    /**
     * Large value to ensure providing invalid data returns correct result
     */
    public int TEST_LARGE_VALUE = 100;

    /**
     * Before each test, create new Difficulty object, get values from that object
     *
     * @throws Exception if something goes wrong
     */
    @Before
    public void setUp() throws Exception {
        difficulty = new Difficulty();
        Field privateMinLevel = difficulty.getClass().getDeclaredField("LEVEL_ONE_MIN");
        privateMinLevel.setAccessible(true);
        TEST_MIN_VAL = (int) privateMinLevel.get(difficulty);

        Field privateMaxLevel = difficulty.getClass().getDeclaredField("LEVEL_THREE_MAX");
        privateMaxLevel.setAccessible(true);
        TEST_MAX_VAL = (int) privateMaxLevel.get(difficulty);
    }

    /**
     * Clear values after test
     */
    @After
    public void tearDown() {
        if (difficulty != null) {
            difficulty = null;
        }
    }

    /**
     * Ensure minimum value for Difficulty 1 is correct
     */
    @Test
    public void testGetMinForLevel() {
        int minVal = Difficulty.getMinForLevel(TEST_MIN_VAL_PARAM);
        assertEquals(TEST_MIN_VAL, minVal);
    }

    /**
     * Ensure maximum value for Difficulty 3 is correct
     */
    @Test
    public void testGetMaxForLevel() {
        int maxVal = Difficulty.getMaxForLevel(TEST_MAX_VAL_PARAM);
        assertEquals(TEST_MAX_VAL, maxVal);
    }

    /**
     * Ensure that inputting invalid value returns correct value
     */
    @Test
    public void testInvalidValues() {
        int minVal = Difficulty.getMinForLevel(TEST_LARGE_VALUE);
        assertEquals(Integer.MAX_VALUE, minVal);
        int maxVal = Difficulty.getMaxForLevel(TEST_LARGE_VALUE);
        assertEquals(Integer.MAX_VALUE, maxVal);
    }


}