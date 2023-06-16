package br.dev.gabrielrocha.pomodoro;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import br.dev.gabrielrocha.pomodoro.controller.ClockController;
import br.dev.gabrielrocha.pomodoro.model.Mode;

public class MainActivity extends Activity {
    private static final int[] MODE_SEQUENCE = new int[]{1, 2, 1, 2, 1, 3};
    private int[] currentModeSequence = MODE_SEQUENCE;

    private ScheduledFuture<?> scheduledFutureVibration, scheduledFutureFlashingOff, scheduledFutureFlashingOn;
    private View.OnClickListener advanceClickListener;
    private View.OnClickListener pauseClickListener;
    private View.OnClickListener startClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("CURRENT_MODE_SEQUENCE")) {
                currentModeSequence = savedInstanceState.getIntArray("CURRENT_MODE_SEQUENCE");
            }
        }

        int theme = currentModeSequence[0];
        Mode mode = Mode.FOCUS;
        int modeStringRes = R.string.label_focus;

        switch (theme) {
            case 1:
                getTheme().applyStyle(R.style.Theme_Pomodoro_Red, true);
                break;
            case 2:
                mode = Mode.SHORT_BREAK;
                modeStringRes = R.string.label_short_break;
                getTheme().applyStyle(R.style.Theme_Pomodoro_Green, true);
                break;
            case 3:
                mode = Mode.LONG_BREAK;
                modeStringRes = R.string.label_long_break;
                getTheme().applyStyle(R.style.Theme_Pomodoro_Blue, true);
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton imageButtonFastForward = findViewById(R.id.image_button_fast_forward);
        ImageButton imageButtonOptions = findViewById(R.id.image_button_options);
        ImageButton imageButtonStartStop = findViewById(R.id.image_button_start_stop);
        TextView textViewMode = findViewById(R.id.text_view_mode);
        TextView textViewTime = findViewById(R.id.text_view_time);

        ClockController.setPauseCallback(() -> imageButtonStartStop.setImageResource(R.drawable.ic_play_fill));
        ClockController.setStartCallback(() -> imageButtonStartStop.setImageResource(R.drawable.ic_pause_fill));

        View.OnClickListener fastForwardClickListener = v -> {
            ClockController.stop();
            shiftModeSequence(currentModeSequence);
            imageButtonStartStop.setOnClickListener(startClickListener);
            recreate();
        };

        advanceClickListener = v -> {
            scheduledFutureVibration.cancel(false);
            scheduledFutureFlashingOff.cancel(false);
            scheduledFutureFlashingOn.cancel(false);
            shiftModeSequence(currentModeSequence);
            imageButtonStartStop.setOnClickListener(startClickListener);
            recreate();
        };
        pauseClickListener = v -> {
            ClockController.stop();
            imageButtonStartStop.setOnClickListener(startClickListener);
        };
        startClickListener = v -> {
            ClockController.start((MyApplication) getApplication(), () -> {
                runOnUiThread(() -> textViewTime.setText(timerText()));
                if (ClockController.getFinished()) {
                    MyApplication myApplication = (MyApplication) getApplication();
                    scheduledFutureVibration = myApplication.getScheduledExecutorService().scheduleAtFixedRate(() -> {
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        }
                    }, 0, 1, TimeUnit.SECONDS);
                    scheduledFutureFlashingOff = myApplication.getScheduledExecutorService().scheduleAtFixedRate(() -> runOnUiThread(() -> textViewTime.setVisibility(View.INVISIBLE)), 500, 1000, TimeUnit.MILLISECONDS);
                    scheduledFutureFlashingOn = myApplication.getScheduledExecutorService().scheduleAtFixedRate(() -> runOnUiThread(() -> textViewTime.setVisibility(View.VISIBLE)), 0, 1000, TimeUnit.MILLISECONDS);
                    myApplication.getScheduledExecutorService().schedule(() -> {
                        scheduledFutureVibration.cancel(false);
                        scheduledFutureFlashingOff.cancel(false);
                        scheduledFutureFlashingOn.cancel(false);
                    }, 10, TimeUnit.SECONDS);
                    imageButtonStartStop.setOnClickListener(advanceClickListener);
                }
            });
            imageButtonStartStop.setOnClickListener(pauseClickListener);
        };

        textViewMode.setText(modeStringRes);
        ClockController.prepare(mode);

        textViewTime.setText(timerText());
        imageButtonFastForward.setOnClickListener(fastForwardClickListener);
        imageButtonOptions.setOnClickListener(v -> Toast.makeText(this, R.string.label_unavailable, Toast.LENGTH_SHORT).show());
        imageButtonStartStop.setOnClickListener(startClickListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("CURRENT_MODE_SEQUENCE", currentModeSequence);
    }

    private void shiftModeSequence(int[] modeSequence) {
        int aux = modeSequence[0];
        for (int i = 0; i < modeSequence.length - 1; i++) {
            modeSequence[i] = modeSequence[i + 1];
        }
        modeSequence[modeSequence.length - 1] = aux;
    }

    private String timerText() {
        int[] minuteSecondPair = ClockController.timeToMinuteSecondPair();
        return String.format(Locale.getDefault(), "%02d\n%02d", minuteSecondPair[0], minuteSecondPair[1]);
    }
}
