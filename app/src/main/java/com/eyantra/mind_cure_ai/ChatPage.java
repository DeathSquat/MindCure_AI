package com.eyantra.mind_cure_ai;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Toast;

public class ChatPage extends AppCompatActivity {
    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private LinearLayout optionsContainer;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final long THINKING_DELAY = 2000; // 2 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Override default activity transition
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle("MindCure AI");
        }

        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollView);
        optionsContainer = findViewById(R.id.optionsContainer);

        // Add initial greeting
        addMessage("Hey Champ! 👋 How was your day?", false);
        showOptions(new String[]{"Had a good moment 😊", "Had a tough time 😞"});
    }

    private void showOptions(String[] options) {
        optionsContainer.removeAllViews();
        optionsContainer.setVisibility(View.VISIBLE);

        for (String option : options) {
            Button button = new Button(this);
            button.setText(option);
            button.setTextSize(16);
            button.setAllCaps(false);
            button.setPadding(16, 8, 16, 8);
            button.setBackgroundResource(R.drawable.option_button_bg);
            button.setTextColor(Color.WHITE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 5, 10, 5);
            button.setLayoutParams(params);

            button.setOnClickListener(v -> {
                String selectedOption = ((Button) v).getText().toString();
                addMessage(selectedOption, true);
                optionsContainer.setVisibility(View.GONE);
                handleUserInput(selectedOption);
            });

            optionsContainer.addView(button);
        }
    }

    private void handleUserInput(String userInput) {
        // Show thinking indicator
        addThinkingIndicator();

        // Simulate thinking time
        handler.postDelayed(() -> {
            removeThinkingIndicator();
            String response = getResponse(userInput);
            addMessage(response, false);
            showNextOptions(userInput);
        }, THINKING_DELAY);
    }

    private void addThinkingIndicator() {
        TextView thinkingView = new TextView(this);
        thinkingView.setText("AI is thinking...");
        thinkingView.setTextColor(Color.GRAY);
        thinkingView.setPadding(16, 8, 16, 8);
        thinkingView.setTag("thinking");
        chatContainer.addView(thinkingView);
        scrollToBottom();
    }

    private void removeThinkingIndicator() {
        for (int i = 0; i < chatContainer.getChildCount(); i++) {
            View view = chatContainer.getChildAt(i);
            if ("thinking".equals(view.getTag())) {
                chatContainer.removeView(view);
                break;
            }
        }
    }

    private void showNextOptions(String userInput) {
        String[] nextOptions;
        switch (userInput) {
            case "Had a good moment 😊":
                nextOptions = new String[]{"Had fun 🎉", "Achieved something 🏆", "Spent time with loved ones ❤️"};
                break;
            case "Had fun 🎉":
                nextOptions = new String[]{"Played a game 🎮", "Went outdoors 🌳", "Tried something new 🔥"};
                break;
            case "Achieved something 🏆":
                nextOptions = new String[]{"Completed a task ✅", "Learned something new 📖", "Got appreciated 👏"};
                break;
            case "Spent time with loved ones ❤️":
                nextOptions = new String[]{"Family 👨‍👩‍👧‍👦", "Friends 🎊", "Someone special 💖"};
                break;
            case "Had a tough time 😞":
                nextOptions = new String[]{"Yes, I want to share 💙", "No, not right now."};
                break;
            case "Yes, I want to share 💙":
                nextOptions = new String[]{"Felt stressed 😰", "Felt lonely 😔", "Had a bad experience 😞"};
                break;
            case "Felt stressed 😰":
            case "Felt lonely 😔":
                nextOptions = new String[]{"Breathing exercise 🌿", "Play a game 🎮", "Just need a chat 💙"};
                break;
            case "Had a bad experience 😞":
                nextOptions = new String[]{"Play a game 🎮", "Mindfulness activity 🌿", "Just talk 💙"};
                break;
            case "Just talk 💙":
            case "Still need help 💙":
            case "No, not right now.":
            case "Got appreciated 👏":
            case "Learned something new 📖":
            case "Completed a task ✅":
            case "Played a game 🎮":
            case "Went outdoors 🌳":
            case "Tried something new 🔥":
            case "Family 👨‍👩‍👧‍👦":
            case "Friends 🎊":
            case "Someone special 💖":
            case "Just need a chat 💙":
                nextOptions = new String[]{"Yes, I want to share more 💙", "No, let's wrap up."};
                break;
            case "Yes, I want to share more 💙":
                nextOptions = new String[]{"Had a good moment 😊", "Had a tough time 😞"};
                break;
            case "No, let's wrap up.":
            case "No, I'm good now.":
                nextOptions = new String[]{"Start a new chat 🔄", "Exit to Home ❌"};
                break;
            case "Start a new chat 🔄":
                restartChat();
                return;
            case "Exit to Home ❌":
                addMessage("Alright! Take care and stay strong. See you soon! 😊", false);
                optionsContainer.postDelayed(() -> {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }, 2000);
                return;
            case "Breathing exercise 🌿":
                addMessage("Great choice! Redirecting you to the breathing exercise... 🌿", false);
                optionsContainer.postDelayed(() -> {
                    Intent intent = new Intent(this, BreathingExerciseActivity.class);
                    startActivity(intent);
                }, 2000);
                return;
            case "Mindfulness activity 🌿":
                addMessage("Great choice! Redirecting you to the mindfulness activity... 🌿", false);
                optionsContainer.postDelayed(() -> {
                    Intent intent = new Intent(this, MindfulnessActivity.class);
                    startActivity(intent);
                }, 2000);
                return;
            case "Play a game 🎮":
                addMessage("Great choice! Redirecting you to the game now... 🎮", false);
                optionsContainer.postDelayed(() -> {
                    Intent intent = new Intent(this, GamesActivity.class);
                    startActivity(intent);
                    finish();
                }, 2000);
                return;
            default:
                nextOptions = new String[]{"Start a new chat 🔄", "Exit to Home ❌"};
        }
        showOptions(nextOptions);
    }

    private String getResponse(String userInput) {
        switch (userInput) {
            case "Had a good moment 😊":
                return "That's wonderful! What made your day special?";
            case "Had fun 🎉":
                return "That sounds amazing! What kind of fun activity?";
            case "Achieved something 🏆":
                return "That's impressive! What did you accomplish?";
            case "Spent time with loved ones ❤️":
                return "That's heartwarming! Who did you spend time with?";
            case "Had a tough time 😞":
                return "I'm sorry to hear that. Want to talk about it?";
            case "Yes, I want to share 💙":
                return "I'm here to listen. What's on your mind?";
            case "Felt stressed 😰":
            case "Felt lonely 😔":
                return "Stress can be tough. Want to try a calming activity?";
            case "Had a bad experience 😞":
                return "That must have been hard. Do you want a distraction?";
            case "Just talk 💙":
            case "Still need help 💙":
            case "No, not right now.":
            case "Got appreciated 👏":
            case "Learned something new 📖":
            case "Completed a task ✅":
            case "Played a game 🎮":
            case "Went outdoors 🌳":
            case "Tried something new 🔥":
            case "Family 👨‍👩‍👧‍👦":
            case "Friends 🎊":
            case "Someone special 💖":
            case "Just need a chat 💙":
                return "Would you like to share more about your day?";
            case "Yes, I want to share more 💙":
                return "I'm here to listen! Tell me what's on your mind.";
            case "No, let's wrap up.":
            case "No, I'm good now.":
                return "Alright! I'm always here for you. Take care! 💙";
            default:
                return "I'm here to help. Please let me know what specific information or support you need.";
        }
    }

    private void addMessage(String message, boolean isUser) {
        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(18);
        messageView.setPadding(16, 12, 16, 12);
        messageView.setBackgroundResource(isUser ? R.drawable.user_message_bg : R.drawable.bot_message_bg);
        messageView.setTextColor(isUser ? Color.WHITE : Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = isUser ? Gravity.END : Gravity.START;
        params.setMargins(8, 8, 8, 8);
        messageView.setLayoutParams(params);

        chatContainer.addView(messageView);
        scrollToBottom();
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void restartChat() {
        chatContainer.removeAllViews();
        addMessage("Hey Nishchay! 👋 How was your day?", false);
        showOptions(new String[]{"Had a good moment 😊", "Had a tough time 😞"});
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