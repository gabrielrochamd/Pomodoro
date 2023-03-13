package br.dev.gabrielrocha.pomodoro.util;

import junit.framework.TestCase;

import org.junit.Assert;

public class TimeUtilsTest extends TestCase {

    public void testMillisecondsToSecondMinutePair() {
        int[] actualPair = TimeUtils.millisecondsToSecondMinutePair(1499000L);
        int[] expectedPair = new int[]{24, 59};
        Assert.assertArrayEquals(expectedPair, actualPair);
    }
}