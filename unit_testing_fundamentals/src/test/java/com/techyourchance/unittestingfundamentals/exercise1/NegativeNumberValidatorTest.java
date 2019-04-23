package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NegativeNumberValidatorTest {

    private NegativeNumberValidator validator;

    @Before
    public void setup(){
        validator = new NegativeNumberValidator();
    }

    @Test
    public void testPostitiveNumber(){
        boolean result = validator.isNegative(10);
        assertFalse(result);
    }

    @Test
    public void testNegativeNumber(){
        boolean result = validator.isNegative(-10);
        assertTrue(result);
    }

    @Test
    public void testZero() {
        boolean result = validator.isNegative(0);
        assertFalse(result);
    }
}