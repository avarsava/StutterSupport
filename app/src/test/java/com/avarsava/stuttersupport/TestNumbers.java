package com.avarsava.stuttersupport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author  Aditi Trivedi <at15gp@brocku.ca
 * @version 1.1
 * @since   1.1
 *
 * Tests validity of the Numbers class using localized testing and reflection.
 */
public class TestNumbers {

    public final int TEST_MIN_VAL = 5;
    public final int TEST_MAX_VAL = 10;

    @Before
    public void setUp() throws Exception {
        //nothing to setup
    }

    @After
    public void tearDown() throws Exception {
        //nothing to teardown
    }

    @Test
    public void testRandInt() throws Exception {
       int randInt = Numbers.randInt(TEST_MIN_VAL, TEST_MAX_VAL);
       assertTrue(TEST_MIN_VAL <= randInt && randInt <= TEST_MAX_VAL);
    }

}