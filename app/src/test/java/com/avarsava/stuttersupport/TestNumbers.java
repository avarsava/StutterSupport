package com.avarsava.stuttersupport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author  Aditi Trivedi <at15gp@brocku.ca
 * @version 1.5
 * @since   1.1
 *
 * Tests validity of the Numbers class using localized testing and reflection.
 */
public class TestNumbers {
    /**
     * Minimum randomly generated value should be
     */
    public final int TEST_MIN_VAL = 5;

    /**
     * Maximum randomly generated value should be
     */
    public final int TEST_MAX_VAL = 10;

    //TODO: Is this necessary?
    @Before
    public void setUp() throws Exception {
        //nothing to setup
    }

    //TODO: Is this necessary?
    @After
    public void tearDown() throws Exception {
        //nothing to teardown
    }

    /**
     * Generates a random integer between 5 and 10 using the Numbers class.
     * Ensures that randomly generated int is between 5 and 10.
     *
     * @throws Exception if something fails
     */
    @Test
    public void testRandInt() throws Exception {
       int randInt = Numbers.randInt(TEST_MIN_VAL, TEST_MAX_VAL);
       assertTrue(TEST_MIN_VAL <= randInt && randInt <= TEST_MAX_VAL);
    }

}