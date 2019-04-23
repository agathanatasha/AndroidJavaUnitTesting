package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntervalsAdjacencyDetectorTest {

    private IntervalsAdjacencyDetector detector;

    @Before
    public void setUp() throws Exception {
        detector = new IntervalsAdjacencyDetector();
    }

    @Test
    public void isAdjacent_interval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(-9, 4);
        Interval interval2 = new Interval(6, 11);
        Boolean result = detector.isAdjacent(interval1, interval2);

        assertFalse(result);
    }

    @Test
    public void isAdjacent_interval1AdjacentBeforeInterval2_trueReturned() {
        Interval interval1 = new Interval(-9, 4);
        Interval interval2 = new Interval(4, 11);
        Boolean result = detector.isAdjacent(interval1, interval2);

        assertTrue(result);
    }

    @Test
    public void isAdjacent_interval1OverlapsInterval2AtStart_falseReturned() {
        Interval interval1 = new Interval(-9, 4);
        Interval interval2 = new Interval(2, 11);
        Boolean result = detector.isAdjacent(interval1, interval2);

        assertFalse(result);
    }

    @Test
    public void isAdjacent_interval1EqualsInterval2_falseReturned() {
        Interval interval1 = new Interval(-9, 4);
        Interval interval2 = new Interval(-9, 4);
        Boolean result = detector.isAdjacent(interval1, interval2);

        assertFalse(result);
    }

    @Test
    public void isAdjacent_interval1ContainsInterval2WithSameStart_falseReturned() {
        Interval interval1 = new Interval(-9, 11);
        Interval interval2 = new Interval(-9, 4);
        Boolean result = detector.isAdjacent(interval1, interval2);

        assertFalse(result);
    }

    @Test
    public void isAdjacent_interval1OverlapsInterval2AtEnd_falseReturned() {
        Interval interval1 = new Interval(1, 10);
        Interval interval2 = new Interval(-9, 4);
        Boolean result = detector.isAdjacent(interval1, interval2);

        assertFalse(result);
    }

    @Test
    public void isAdjacent_interval1ContainsInterval2WithSameEnd_false() {
        Interval interval1 = new Interval(-9, 11);
        Interval interval2 = new Interval(6, 11);
        Boolean result = detector.isAdjacent(interval1, interval2);

        assertFalse(result);
    }

    @Test
    public void isAdjacent_interval1AdjacentAfterInterval2_trueReturned() {
        Interval interval1 = new Interval(6, 11);
        Interval interval2 = new Interval(0, 6);
        Boolean result = detector.isAdjacent(interval1, interval2);

        assertTrue(result);
    }

    @Test
    public void isAdjacent_interval1AfterInterval2_falseReturned() {
        Interval interval1 = new Interval(10, 11);
        Interval interval2 = new Interval(0, 6);
        Boolean result = detector.isAdjacent(interval1, interval2);

        assertFalse(result);
    }
}