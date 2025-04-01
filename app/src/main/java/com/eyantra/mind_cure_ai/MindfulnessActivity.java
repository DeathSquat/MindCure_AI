package com.eyantra.mind_cure_ai;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MindfulnessActivity extends AppCompatActivity {
    private TextView timerText;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 300000; // 5 minutes in milliseconds
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mindfulness);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mindfulness Meditation");
        }

        // Initialize views
        timerText = findViewById(R.id.timerText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);

        // Set initial button states
        updateButtonStates();

        // Setup button click listeners
        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer());
        resetButton.setOnClickListener(v -> resetTimer());

        // Update timer text initially
        updateTimerText();
    }

    private void startTimer() {
        if (!isTimerRunning) {
            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimerText();
                }

                @Override
                public void onFinish() {
                    isTimerRunning = false;
                    updateButtonStates();
                    // You can add a sound or notification here
                    Toast.makeText(MindfulnessActivity.this, "Meditation completed", Toast.LENGTH_SHORT).show();
                }
            }.start();

            isTimerRunning = true;
            updateButtonStates();
        }
    }

    private void pauseTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
            updateButtonStates();
            Toast.makeText(this, "Meditation paused", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetTimer() {
        countDownTimer.cancel();
        timeLeftInMillis = 300000; // Reset to 5 minutes
        isTimerRunning = false;
        updateTimerText();
        updateButtonStates();
        Toast.makeText(this, "Meditation reset", Toast.LENGTH_SHORT).show();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftText = String.format("%02d:%02d", minutes, seconds);
        timerText.setText(timeLeftText);
    }

    private void updateButtonStates() {
        startButton.setEnabled(!isTimerRunning);
        pauseButton.setEnabled(isTimerRunning);
        resetButton.setEnabled(!isTimerRunning);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
} 