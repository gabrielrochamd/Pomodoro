package br.dev.gabrielrocha.pomodoro;

import android.app.Application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MyApplication extends Application {
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}
