package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringDuplicatorTest {

    private StringDuplicator duplicator;

    @Before
    public void setUp() throws Exception {
        duplicator = new StringDuplicator();
    }

    @Test
    public void stringDuplicator_emptyString_emptyStringReturned() {
        String result = duplicator.duplicate("");
        assertEquals("", result);
    }

    @Test
    public void stringDuplicator_singleChar_twoCharsReturned() {
        String result = duplicator.duplicate("a");
        assertEquals("aa", result);
    }

    @Test
    public void stringDuplicator_longStr_duplicatedStrReturned(){
        String result = duplicator.duplicate("AssertionError");
        assertEquals("AssertionErrorAssertionError", result);
    }
}