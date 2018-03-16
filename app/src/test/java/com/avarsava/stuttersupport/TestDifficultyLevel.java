package com.avarsava.stuttersupport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.avarsava.stuttersupport.Difficulty;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
/**
 * @author  Aditi Trivedi <at15gp@brocku.ca
 * @version 1.1
 * @since   1.1
 *
 * Tests validity of the Difficulty class using localized testing and reflection.
 */
public class TestDifficultyLevel {

    Difficulty difficulty;
    public int TEST_MIN_VAL_PARAM = 2;
    public int TEST_MIN_VAL;
    public int TEST_MAX_VAL_PARAM = 3;
    public int TEST_MAX_VAL;
    public int TEST_LARGE_VALUE = 100;

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

    @After
    public void tearDown() {
        if (difficulty != null) {
            difficulty = null;
        }
    }

    @Test
    public void testGetMinForLevel() {
        int minVal = Difficulty.getMinForLevel(TEST_MIN_VAL_PARAM);
        assertEquals(TEST_MIN_VAL, minVal);
    }

    @Test
    public void testGetMaxForLevel() {
        int maxVal = Difficulty.getMaxForLevel(TEST_MAX_VAL_PARAM);
        assertEquals(TEST_MAX_VAL, maxVal);
    }

    @Test
    public void testInvalidValues() {
        int minVal = Difficulty.getMinForLevel(TEST_LARGE_VALUE);
        assertEquals(Integer.MAX_VALUE, minVal);
        int maxVal = Difficulty.getMaxForLevel(TEST_LARGE_VALUE);
        assertEquals(Integer.MAX_VALUE, maxVal);
    }


}