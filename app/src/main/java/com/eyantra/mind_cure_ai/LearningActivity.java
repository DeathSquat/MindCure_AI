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
import java.util.ArrayList;
import java.util.List;

public class LearningActivity extends AppCompatActivity {
    private RecyclerView lessonsRecyclerView;
    private LinearLayout optionsLayout;
    private ScrollView optionsScrollView;
    private LinearLayout contentLayout;
    private String selectedTopic;
    private List<Lesson> lessons;
    private LessonAdapter lessonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        // Override default activity transition
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle("Learning Center");
        }

        // Initialize views
        lessonsRecyclerView = findViewById(R.id.lessonsRecyclerView);
        optionsLayout = findViewById(R.id.optionsLayout);
        optionsScrollView = findViewById(R.id.optionsScrollView);
        contentLayout = findViewById(R.id.contentLayout);

        // Setup RecyclerView
        lessons = new ArrayList<>();
        lessonAdapter = new LessonAdapter(lessons);
        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lessonsRecyclerView.setAdapter(lessonAdapter);

        // Show initial options with animation
        showOptions();
    }

    private void showOptions() {
        // Fade out content layout if visible
        if (contentLayout.getVisibility() == View.VISIBLE) {
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            contentLayout.startAnimation(fadeOut);
            contentLayout.setVisibility(View.GONE);
        }

        // Show and animate options layout
        optionsLayout.setVisibility(View.VISIBLE);
        optionsLayout.removeAllViews();

        String[] options = {
            "Stress Management Techniques 🧘‍♂️",
            "Mindfulness Practices 🌿",
            "Positive Psychology Tips 🌟",
            "Coping Strategies 💪",
            "Self-Care Guide 🌺"
        };

        for (String option : options) {
            Button button = new Button(this);
            button.setText(option);
            button.setTextSize(16);
            button.setAllCaps(false);
            button.setPadding(16, 8, 16, 8);
            button.setBackgroundResource(R.drawable.option_button_bg);

            button.setOnClickListener(v -> showContent(option));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 5, 10, 5);
            button.setLayoutParams(params);
            optionsLayout.addView(button);

            // Add fade-in animation for each button
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            fadeIn.setStartOffset(optionsLayout.getChildCount() * 100); // Stagger the animations
            button.startAnimation(fadeIn);
        }
    }

    private void showContent(String option) {
        // Fade out options layout
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        optionsLayout.startAnimation(fadeOut);
        optionsLayout.setVisibility(View.GONE);

        // Show and animate content layout
        contentLayout.setVisibility(View.VISIBLE);
        contentLayout.removeAllViews();

        // Add back button with animation
        Button backButton = new Button(this);
        backButton.setText("← Back to Options");
        backButton.setTextSize(16);
        backButton.setAllCaps(false);
        backButton.setPadding(16, 8, 16, 8);
        backButton.setBackgroundResource(R.drawable.option_button_bg);
        backButton.setOnClickListener(v -> showOptions());

        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        backParams.setMargins(10, 5, 10, 5);
        backButton.setLayoutParams(backParams);
        contentLayout.addView(backButton);

        // Add content with animation
        String content = getContentForOption(option);
        Button contentButton = new Button(this);
        contentButton.setText(content);
        contentButton.setTextSize(16);
        contentButton.setAllCaps(false);
        contentButton.setPadding(16, 8, 16, 8);
        contentButton.setBackgroundResource(R.drawable.option_button_bg);
        contentButton.setEnabled(false);

        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentParams.setMargins(10, 5, 10, 5);
        contentButton.setLayoutParams(contentParams);
        contentLayout.addView(contentButton);

        // Animate the content
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        contentLayout.startAnimation(slideIn);
    }

    private String getContentForOption(String option) {
        switch (option) {
            case "Stress Management Techniques 🧘‍♂️":
                return "1. Deep Breathing\n" +
                       "   • Inhale for 4 seconds\n" +
                       "   • Hold for 4 seconds\n" +
                       "   • Exhale for 4 seconds\n" +
                       "   • Practice 5-10 times daily\n\n" +
                       "2. Progressive Muscle Relaxation\n" +
                       "   • Tense each muscle group for 5 seconds\n" +
                       "   • Release and relax for 10 seconds\n" +
                       "   • Start from toes, move up to head\n\n" +
                       "3. Time Management\n" +
                       "   • Create a daily schedule\n" +
                       "   • Prioritize tasks\n" +
                       "   • Take regular breaks\n" +
                       "   • Set realistic goals\n\n" +
                       "4. Exercise\n" +
                       "   • 30 minutes daily activity\n" +
                       "   • Mix cardio and strength training\n" +
                       "   • Include stretching\n" +
                       "   • Stay hydrated\n\n" +
                       "5. Meditation\n" +
                       "   • Find a quiet space\n" +
                       "   • Focus on your breath\n" +
                       "   • Start with 5-10 minutes\n" +
                       "   • Gradually increase duration";

            case "Mindfulness Practices 🌿":
                return "1. Body Scan\n" +
                       "   • Lie down comfortably\n" +
                       "   • Focus attention on each body part\n" +
                       "   • Notice sensations without judgment\n" +
                       "   • Duration: 10-20 minutes\n\n" +
                       "2. Mindful Walking\n" +
                       "   • Walk slowly and deliberately\n" +
                       "   • Feel each step\n" +
                       "   • Notice surroundings\n" +
                       "   • Practice for 10-15 minutes\n\n" +
                       "3. Mindful Eating\n" +
                       "   • Eat without distractions\n" +
                       "   • Chew slowly\n" +
                       "   • Notice flavors and textures\n" +
                       "   • Practice gratitude\n\n" +
                       "4. Loving-Kindness Meditation\n" +
                       "   • Start with self-compassion\n" +
                       "   • Extend to loved ones\n" +
                       "   • Include neutral people\n" +
                       "   • Finally, all beings\n\n" +
                       "5. Present Moment Awareness\n" +
                       "   • Focus on current activity\n" +
                       "   • Notice thoughts without attachment\n" +
                       "   • Return to present when mind wanders\n" +
                       "   • Practice throughout day";

            case "Positive Psychology Tips 🌟":
                return "1. Gratitude Journal\n" +
                       "   • Write 3 things daily\n" +
                       "   • Be specific\n" +
                       "   • Include small moments\n" +
                       "   • Review weekly\n\n" +
                       "2. Random Acts of Kindness\n" +
                       "   • Help someone daily\n" +
                       "   • Give compliments\n" +
                       "   • Share resources\n" +
                       "   • Volunteer regularly\n\n" +
                       "3. Positive Affirmations\n" +
                       "   • Create personal statements\n" +
                       "   • Repeat daily\n" +
                       "   • Use present tense\n" +
                       "   • Believe in them\n\n" +
                       "4. Strengths Focus\n" +
                       "   • Identify your strengths\n" +
                       "   • Use them daily\n" +
                       "   • Develop new ones\n" +
                       "   • Share with others\n\n" +
                       "5. Optimism Training\n" +
                       "   • Challenge negative thoughts\n" +
                       "   • Find silver linings\n" +
                       "   • Set hopeful goals\n" +
                       "   • Practice positive self-talk";

            case "Coping Strategies 💪":
                return "1. Problem-Solving\n" +
                       "   • Identify the problem\n" +
                       "   • Generate solutions\n" +
                       "   • Evaluate options\n" +
                       "   • Take action\n\n" +
                       "2. Emotional Expression\n" +
                       "   • Journal your feelings\n" +
                       "   • Talk to trusted friends\n" +
                       "   • Use creative outlets\n" +
                       "   • Practice self-compassion\n\n" +
                       "3. Social Support\n" +
                       "   • Build support network\n" +
                       "   • Ask for help when needed\n" +
                       "   • Join support groups\n" +
                       "   • Maintain relationships\n\n" +
                       "4. Relaxation Techniques\n" +
                       "   • Deep breathing\n" +
                       "   • Visualization\n" +
                       "   • Yoga or stretching\n" +
                       "   • Take regular breaks\n\n" +
                       "5. Cognitive Restructuring\n" +
                       "   • Identify negative thoughts\n" +
                       "   • Challenge irrational beliefs\n" +
                       "   • Replace with positive thoughts\n" +
                       "   • Practice regularly";

            case "Self-Care Guide 🌺":
                return "1. Physical Health\n" +
                       "   • Regular exercise\n" +
                       "   • Balanced diet\n" +
                       "   • Adequate sleep\n" +
                       "   • Regular check-ups\n\n" +
                       "2. Mental Health\n" +
                       "   • Daily meditation\n" +
                       "   • Stress management\n" +
                       "   • Therapy when needed\n" +
                       "   • Mental health breaks\n\n" +
                       "3. Emotional Health\n" +
                       "   • Express feelings\n" +
                       "   • Set boundaries\n" +
                       "   • Practice self-compassion\n" +
                       "   • Seek support\n\n" +
                       "4. Social Health\n" +
                       "   • Maintain relationships\n" +
                       "   • Social activities\n" +
                       "   • Community involvement\n" +
                       "   • Healthy communication\n\n" +
                       "5. Spiritual Health\n" +
                       "   • Personal reflection\n" +
                       "   • Mindfulness practice\n" +
                       "   • Nature connection\n" +
                       "   • Purpose and meaning";

            default:
                return "Content not available";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (contentLayout.getVisibility() == View.VISIBLE) {
                showOptions();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (contentLayout.getVisibility() == View.VISIBLE) {
            showOptions();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
} 