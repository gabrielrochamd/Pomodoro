package br.dev.gabrielrocha.pomodoro.util;

public abstract class TimeUtils {
    public static int[] millisecondsToSecondMinutePair(Long millis) {
        int minutes = (int) (millis / 1000 / 60);
        int seconds = (int) ((millis - minutes * 60000) / 1000);
        return new int[]{minutes, seconds};
    }
}
