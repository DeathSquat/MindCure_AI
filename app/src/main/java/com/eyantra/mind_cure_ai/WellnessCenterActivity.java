package com.eyantra.mind_cure_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

public class WellnessCenterActivity extends AppCompatActivity {
    private LinearLayout optionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellness_center);

        // Override default activity transition
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle("Wellness Center");
        }

        optionsContainer = findViewById(R.id.optionsContainer);
        showOptions();
    }

    private void showOptions() {
        String[][] options = {
            {"Let's Chat", "Start a conversation with our AI to detect your emotions and provide personalized support"},
            {"Breathing Exercise", "Learn and practice breathing techniques for stress relief and relaxation"},
            {"Mindfulness Activity", "Engage in mindfulness exercises to improve focus and reduce anxiety"},
            {"Learning Activity", "Access educational content about mental health and well-being"},
            {"Goal Setting", "Set and track your personal development and wellness goals"},
            {"Tips", "Access helpful tips and strategies for maintaining mental well-being"}
        };

        for (String[] option : options) {
            LinearLayout optionLayout = new LinearLayout(this);
            optionLayout.setOrientation(LinearLayout.VERTICAL);
            optionLayout.setBackgroundResource(R.drawable.option_button_bg);
            optionLayout.setPadding(24, 24, 24, 24);
            optionLayout.setClickable(true);
            optionLayout.setFocusable(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16, 16, 16, 16);
            optionLayout.setLayoutParams(params);

            TextView titleText = new TextView(this);
            titleText.setText(option[0]);
            titleText.setTextSize(22);
            titleText.setTextColor(Color.WHITE);
            titleText.setGravity(Gravity.START);
            titleText.setPadding(0, 0, 0, 12);

            TextView descText = new TextView(this);
            descText.setText(option[1]);
            descText.setTextSize(16);
            descText.setTextColor(Color.WHITE);
            descText.setAlpha(0.9f);
            descText.setGravity(Gravity.START);

            optionLayout.addView(titleText);
            optionLayout.addView(descText);

            optionLayout.setOnClickListener(v -> {
                switch (option[0]) {
                    case "Let's Chat":
                        startActivity(new Intent(this, EmotionDetectionActivity.class));
                        break;
                    case "Breathing Exercise":
                        startActivity(new Intent(this, BreathingExerciseActivity.class));
                        break;
                    case "Mindfulness Activity":
                        startActivity(new Intent(this, MindfulnessActivity.class));
                        break;
                    case "Learning Activity":
                        startActivity(new Intent(this, LearningActivity.class));
                        break;
                    case "Goal Setting":
                        startActivity(new Intent(this, GoalSettingActivity.class));
                        break;
                    case "Tips":
                        startActivity(new Intent(this, TipsActivity.class));
                        break;
                }
            });

            optionsContainer.addView(optionLayout);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
} 