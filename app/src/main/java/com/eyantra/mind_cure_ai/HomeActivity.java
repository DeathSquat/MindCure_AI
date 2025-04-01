package com.eyantra.mind_cure_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private ImageView breathingImage;
    private TextView deepBreathText, selfHealthText;
    private Button chatButton, gameButton, sosButton, bookSessionButton, btnExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Override default activity transition
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        // Initialize views
        initializeViews();
        setupClickListeners();
        animateViews();
    }

    private void initializeViews() {
        // Initialize all views
        breathingImage = findViewById(R.id.breathingImage);
        deepBreathText = findViewById(R.id.deepBreathText);
        selfHealthText = findViewById(R.id.selfHealthText);
        chatButton = findViewById(R.id.chatButton);
        gameButton = findViewById(R.id.gameButton);
        sosButton = findViewById(R.id.sosButton);
        bookSessionButton = findViewById(R.id.bookSessionButton);
        btnExercise = findViewById(R.id.btnExercise);
    }

    private void setupClickListeners() {
        // Chat button action (Navigate to ChatActivity)
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, EmotionDetectionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Game Button Action (Navigate to GamesActivity)
        gameButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GamesActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // SOS Button Action (Navigate to Emergency Help Page)
        sosButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SosActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Book Session Button Action (Navigate to Booking Page)
        bookSessionButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Exercise Button Action
        btnExercise.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, WellnessCenterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void animateViews() {
        // Fade-in animation for welcome text
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(2000);
        deepBreathText.startAnimation(fadeIn);

        // Load and start breathing animation
        try {
            Animation breathingAnimation = AnimationUtils.loadAnimation(this, R.anim.breathing_animation);
            breathingImage.startAnimation(breathingAnimation);
        } catch (Exception e) {
            breathingImage.setImageResource(R.drawable.breathing_image);
            e.printStackTrace();
        }

        // Animate self health text with delay
        Animation fadeInDelayed = new AlphaAnimation(0, 1);
        fadeInDelayed.setDuration(1500);
        fadeInDelayed.setStartOffset(1000);
        selfHealthText.startAnimation(fadeInDelayed);

        // Animate buttons with staggered delay
        Button[] buttons = {chatButton, gameButton, sosButton, bookSessionButton, btnExercise};
        for (int i = 0; i < buttons.length; i++) {
            Animation buttonAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            buttonAnim.setStartOffset(1500 + (i * 200));
            buttons[i].startAnimation(buttonAnim);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
