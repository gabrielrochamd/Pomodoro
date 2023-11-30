package br.dev.gabrielrocha.pomodoro;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MyApplication extends Application {
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "clock_channel",
                    "Clock",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
