package br.dev.gabrielrocha.pomodoro.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Locale;

import br.dev.gabrielrocha.pomodoro.R;
import br.dev.gabrielrocha.pomodoro.controller.ClockController;

public class ClockService extends Service {
    private final Binder clockBinder = new Binder();

    public Runnable updateRunnable = () -> {};

    @Override
    public IBinder onBind(Intent intent) {
        return clockBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case "START":
                    start();
                    break;
                case "STOP":
                    stopSelf();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void start() {
        int[] minuteSecondPair = ClockController.timeToMinuteSecondPair();
        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setContentText(String.format(Locale.getDefault(), "%02d:%02d", minuteSecondPair[0], minuteSecondPair[1]))
                .setContentTitle(getString(R.string.app_name))
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId("clock_channel");
        }
        startForeground(1, notificationBuilder.build());
        updateRunnable.run();
    }

    public enum Actions { START, STOP }

    public class Binder extends android.os.Binder {
        public ClockService getService() {
            return ClockService.this;
        }
    }
}
