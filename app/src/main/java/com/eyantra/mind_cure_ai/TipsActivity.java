package com.eyantra.mind_cure_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import android.graphics.Color;
import android.view.Gravity;

public class TipsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private LinearLayout tipsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        // Override default activity transition
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        tipsContainer = findViewById(R.id.tipsContainer);

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

        // Setup tips
        setupTips();
        animateViews();
    }

    private void setupTips() {
        // Daily Wellness Tips
        addTipCategory("Daily Wellness 🌅", new String[]{
            "Start your day with a glass of water",
            "Take 5 deep breaths before getting out of bed",
            "Stretch for 5 minutes in the morning",
            "Eat a healthy breakfast within 1 hour of waking",
            "Practice gratitude before starting your day"
        });

        // Stress Management Tips
        addTipCategory("Stress Management 🧘‍♂️", new String[]{
            "Practice the 4-7-8 breathing technique",
            "Take regular breaks during work",
            "Use the Pomodoro Technique (25/5)",
            "Practice progressive muscle relaxation",
            "Take a short walk when stressed"
        });

        // Sleep Tips
        addTipCategory("Better Sleep 😴", new String[]{
            "Maintain a consistent sleep schedule",
            "Create a relaxing bedtime routine",
            "Avoid screens 1 hour before bed",
            "Keep your bedroom cool and dark",
            "Limit caffeine intake after noon"
        });

        // Mental Health Tips
        addTipCategory("Mental Health 🧠", new String[]{
            "Practice mindfulness daily",
            "Connect with loved ones regularly",
            "Set healthy boundaries",
            "Practice self-compassion",
            "Seek professional help when needed"
        });

        // Physical Health Tips
        addTipCategory("Physical Health 💪", new String[]{
            "Exercise for 30 minutes daily",
            "Stay hydrated throughout the day",
            "Practice good posture",
            "Take regular movement breaks",
            "Get regular health check-ups"
        });

        // Social Wellness Tips
        addTipCategory("Social Wellness 👥", new String[]{
            "Maintain healthy relationships",
            "Practice active listening",
            "Join social groups or clubs",
            "Volunteer in your community",
            "Set aside quality time for loved ones"
        });

        // Emotional Wellness Tips
        addTipCategory("Emotional Wellness ❤️", new String[]{
            "Express your feelings healthily",
            "Practice emotional awareness",
            "Use positive self-talk",
            "Engage in creative activities",
            "Learn to say 'no' when needed"
        });

        // Work-Life Balance Tips
        addTipCategory("Work-Life Balance ⚖️", new String[]{
            "Set clear work boundaries",
            "Take regular breaks",
            "Practice time management",
            "Make time for hobbies",
            "Unplug after work hours"
        });
    }

    private void addTipCategory(String title, String[] tips) {
        MaterialCardView card = new MaterialCardView(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        card.setRadius(12);
        card.setCardElevation(4);
        card.setContentPadding(16, 16, 16, 16);

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);

        // Add title
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(getResources().getColor(R.color.primary_green));
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardContent.addView(titleView);

        // Add tips
        for (String tip : tips) {
            TextView tipView = new TextView(this);
            tipView.setText("• " + tip);
            tipView.setTextSize(16);
            tipView.setTextColor(getResources().getColor(R.color.text_primary));
            tipView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            tipView.setPadding(0, 8, 0, 8);
            cardContent.addView(tipView);
        }

        card.addView(cardContent);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 8, 16, 8);
        tipsContainer.addView(card, params);
    }

    private void animateViews() {
        // Animate each card with a staggered delay
        for (int i = 0; i < tipsContainer.getChildCount(); i++) {
            View child = tipsContainer.getChildAt(i);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            animation.setStartOffset(i * 200);
            child.startAnimation(animation);
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
        super.onBackPressed();
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