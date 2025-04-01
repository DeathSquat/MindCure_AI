package com.eyantra.mind_cure_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class TipsActivity extends AppCompatActivity {
    private RecyclerView tipsRecyclerView;
    private ScrollView scrollView;
    private LinearLayout tipsContainer;
    private String selectedCategory;
    private List<Tip> tips;
    private TipAdapter tipAdapter;
    private boolean isShowingTips = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        // Override default activity transition
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle("Wellness Tips");
        }

        // Initialize views
        tipsRecyclerView = findViewById(R.id.tipsRecyclerView);
        scrollView = findViewById(R.id.scrollView);
        tipsContainer = findViewById(R.id.tipsContainer);

        // Initialize tips list and adapter
        tips = new ArrayList<>();
        tipAdapter = new TipAdapter(tips);
        tipsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tipsRecyclerView.setAdapter(tipAdapter);

        // Setup tips
        setupTips();
        animateViews();
    }

    private void loadTips(String category) {
        tips.clear();
        switch (category) {
            case "Stress Management":
                tips.add(new Tip("Stress Management", "Take deep breaths when feeling overwhelmed"));
                tips.add(new Tip("Stress Management", "Practice time management to reduce stress"));
                tips.add(new Tip("Stress Management", "Exercise regularly to release stress"));
                tips.add(new Tip("Stress Management", "Take regular breaks during work"));
                tips.add(new Tip("Stress Management", "Talk to someone you trust"));
                break;
            case "Anxiety":
                tips.add(new Tip("Anxiety", "Practice grounding techniques"));
                tips.add(new Tip("Anxiety", "Challenge negative thoughts"));
                tips.add(new Tip("Anxiety", "Maintain a regular routine"));
                tips.add(new Tip("Anxiety", "Limit caffeine and alcohol"));
                tips.add(new Tip("Anxiety", "Practice progressive muscle relaxation"));
                break;
            case "Sleep":
                tips.add(new Tip("Sleep", "Maintain a consistent sleep schedule"));
                tips.add(new Tip("Sleep", "Create a relaxing bedtime routine"));
                tips.add(new Tip("Sleep", "Keep your bedroom cool and dark"));
                tips.add(new Tip("Sleep", "Avoid screens before bedtime"));
                tips.add(new Tip("Sleep", "Exercise during the day, not before bed"));
                break;
            case "Self-Care":
                tips.add(new Tip("Self-Care", "Take regular breaks for yourself"));
                tips.add(new Tip("Self-Care", "Practice self-compassion"));
                tips.add(new Tip("Self-Care", "Stay hydrated and eat well"));
                tips.add(new Tip("Self-Care", "Engage in activities you enjoy"));
                tips.add(new Tip("Self-Care", "Set healthy boundaries"));
                break;
            case "Mindfulness":
                tips.add(new Tip("Mindfulness", "Practice mindful breathing daily"));
                tips.add(new Tip("Mindfulness", "Stay present in the moment"));
                tips.add(new Tip("Mindfulness", "Practice mindful eating"));
                tips.add(new Tip("Mindfulness", "Take mindful walks"));
                tips.add(new Tip("Mindfulness", "Practice gratitude daily"));
                break;
        }
        tipAdapter.notifyDataSetChanged();
        showTips();
    }

    private void showTips() {
        scrollView.setVisibility(View.GONE);
        tipsRecyclerView.setVisibility(View.VISIBLE);
        isShowingTips = true;
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
        titleView.setTextColor(getResources().getColor(android.R.color.black));
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
        if (isShowingTips) {
            // First back press: return to options
            tipsRecyclerView.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            isShowingTips = false;
        } else {
            // Second back press: return to home
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
} 