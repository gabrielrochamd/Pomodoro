package br.dev.gabrielrocha.pomodoro;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import br.dev.gabrielrocha.pomodoro.controller.ClockController;
import br.dev.gabrielrocha.pomodoro.model.Mode;

public class MainActivity extends Activity {
    private View.OnClickListener pauseClickListener, startClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mode mode = Mode.FOCUS;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton imageButtonStartStop = findViewById(R.id.image_button_start_stop);
        LinearLayout linearLayoutChip = findViewById(R.id.chip);
        TextView textViewMode = findViewById(R.id.text_view_mode);
        TextView textViewTime = findViewById(R.id.text_view_time);

        ClockController.setPauseCallback(() -> imageButtonStartStop.setImageResource(R.drawable.ic_play_fill));
        ClockController.setStartCallback(() -> imageButtonStartStop.setImageResource(R.drawable.ic_pause_fill));

        pauseClickListener = v -> {
            ClockController.stop();
            imageButtonStartStop.setOnClickListener(startClickListener);
        };
        startClickListener = v -> {
            ClockController.start((MyApplication) getApplication(), () -> runOnUiThread(() -> textViewTime.setText(timerText())));
            imageButtonStartStop.setOnClickListener(pauseClickListener);
        };

        ClockController.prepare(mode);

        textViewTime.setText(timerText());
        imageButtonStartStop.setOnClickListener(startClickListener);
    }

    private String timerText() {
        int[] minuteSecondPair = ClockController.timeToMinuteSecondPair();
        return String.format(Locale.getDefault(), "%02d\n%02d", minuteSecondPair[0], minuteSecondPair[1]);
    }
}
