package br.dev.gabrielrocha.pomodoro.controller;

import android.app.Activity;
import android.content.Intent;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import br.dev.gabrielrocha.pomodoro.MyApplication;
import br.dev.gabrielrocha.pomodoro.model.Mode;
import br.dev.gabrielrocha.pomodoro.service.ClockService;

public abstract class ClockController {
    public static final int MINUTE_IN_SECONDS = 60;

    private static boolean finished = false;
    private static Runnable pauseCallback;
    private static ScheduledFuture<?> scheduledFuture;
    private static Runnable startCallback;
    private static State state = State.FINISHED;
    private static int time;

    public static boolean getFinished() {
        return finished;
    }

    public static State getState() {
        return state;
    }

    public static void setPauseCallback(Runnable pauseCallback) {
        ClockController.pauseCallback = pauseCallback;
    }

    public static void setStartCallback(Runnable startCallback) {
        ClockController.startCallback = startCallback;
    }

    public static void resetTime() {
        time = 0;
    }

    public static void prepare(Mode mode) {
        switch (mode) {
            case FOCUS:
                time = 25 * MINUTE_IN_SECONDS;
                break;
            case SHORT_BREAK:
                time = 5 * MINUTE_IN_SECONDS;
                break;
            case LONG_BREAK:
                time = 15 * MINUTE_IN_SECONDS;
                break;
        }
    }

    public static void start(MyApplication myApplication, Runnable callback) {
        state = State.RUNNING;
        finished = false;
        Intent startIntent = new Intent(myApplication, ClockService.class);
        Intent stopIntent = new Intent(myApplication, ClockService.class);
        startIntent.setAction(ClockService.Actions.START.toString());
        stopIntent.setAction(ClockService.Actions.STOP.toString());
        myApplication.startService(startIntent);
        if (scheduledFuture == null || scheduledFuture.isCancelled()) {
            scheduledFuture = myApplication.getScheduledExecutorService().scheduleAtFixedRate(() -> {
                time--;
                myApplication.startService(startIntent);
                if (time <= 0) {
                    stop();
                    finished = true;
                    myApplication.startService(stopIntent);
                }
                callback.run();
            }, 1, 1, TimeUnit.SECONDS);
        }
        startCallback.run();
    }

    public static void stop() {
        if (time == 0) {
            state = State.FINISHED;
        } else {
            state = State.PAUSED;
        }
        if (scheduledFuture != null) scheduledFuture.cancel(false);
        pauseCallback.run();
    }

    public static int[] timeToMinuteSecondPair() {
        int minutes = time / 60;
        int seconds = time - (minutes * 60);
        return new int[]{minutes, seconds};
    }

    public enum State {FINISHED, PAUSED, PREPARED, RUNNING}
}
