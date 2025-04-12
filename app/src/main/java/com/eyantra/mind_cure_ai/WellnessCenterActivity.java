package com.eyantra.mind_cure_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;

public class WellnessCenterActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellness_center);

        // Override default activity transition
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);

        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set up back button with animation
        toolbar.setNavigationOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_scale));
            onBackPressed();
        });

        // Set up card click listeners
        setupCardClickListeners();
    }

    private void setupCardClickListeners() {
        // Let's Chat card
        MaterialCardView chatCard = findViewById(R.id.chatCard);
        chatCard.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_scale));
            startActivity(new Intent(this, EmotionDetectionActivity.class));
        });

        // Breathing Exercise card
        MaterialCardView breathingCard = findViewById(R.id.breathingCard);
        breathingCard.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_scale));
            startActivity(new Intent(this, BreathingExerciseActivity.class));
        });

        // Mindfulness Activity card
        MaterialCardView mindfulnessCard = findViewById(R.id.mindfulnessCard);
        mindfulnessCard.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_scale));
            startActivity(new Intent(this, MindfulnessActivity.class));
        });

        // Learning Activity card
        MaterialCardView learningCard = findViewById(R.id.learningCard);
        learningCard.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_scale));
            startActivity(new Intent(this, LearningActivity.class));
        });

        // Goal Setting card
        MaterialCardView goalCard = findViewById(R.id.goalCard);
        goalCard.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_scale));
            startActivity(new Intent(this, GoalSettingActivity.class));
        });

        // Tips card
        MaterialCardView tipsCard = findViewById(R.id.tipsCard);
        tipsCard.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_scale));
            startActivity(new Intent(this, TipsActivity.class));
        });
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