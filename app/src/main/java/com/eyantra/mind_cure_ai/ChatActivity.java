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
import android.util.TypedValue;

public class ChatActivity extends AppCompatActivity {
    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private LinearLayout optionsContainer;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final long THINKING_DELAY = 2000; // 2 seconds delay
    private Button btnTalkToTherapist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // Apply fade-in animation to the activity
        overridePendingTransition(R.anim.fade_in, 0);
        
        // Initialize views
        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollView);
        optionsContainer = findViewById(R.id.optionsContainer);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        // Set up back button with animation
        toolbar.setNavigationOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_scale));
            onBackPressed();
        });
        
        // Show initial greeting with animation
        showGreeting();

        btnTalkToTherapist = findViewById(R.id.btnTalkToTherapist);
        btnTalkToTherapist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start BookingActivity when the button is clicked
                Intent intent = new Intent(ChatActivity.this, BookingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showGreeting() {
        // Create greeting message
        LinearLayout messageLayout = createMessageLayout(false);
        TextView messageText = createMessageTextView(getString(R.string.greeting_message), false);
        messageLayout.addView(messageText);
        
        // Add to chat container with animation
        chatContainer.addView(messageLayout);
        messageLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        
        // Show options with delay
        new Handler().postDelayed(this::showOptions, THINKING_DELAY);
    }

    private void showOptions() {
        showOptions(getResources().getStringArray(R.array.chat_options));
    }

    private void showOptions(String[] options) {
        optionsContainer.removeAllViews();
        
        // Add a title for options
        TextView titleView = new TextView(this);
        titleView.setText("Choose an option:");
        titleView.setTextSize(16);
        titleView.setTextColor(Color.GRAY);
        titleView.setPadding(16, 16, 16, 8);
        optionsContainer.addView(titleView);
        
        // Create options with animations
        for (String option : options) {
            LinearLayout optionLayout = createOptionLayout(option);
            optionsContainer.addView(optionLayout);
            
            // Apply slide-up animation with staggered delay
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            animation.setStartOffset(optionsContainer.getChildCount() * 100);
            optionLayout.startAnimation(animation);
        }
    }

    private LinearLayout createOptionLayout(String text) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.option_button_bg);
        layout.setClickable(true);
        layout.setFocusable(true);
        
        // Add elevation for 3D effect
        layout.setElevation(8f);
        
        // Add padding and margins
        int padding = 24; // Increased padding
        layout.setPadding(padding, padding/2, padding, padding/2);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = 16;
        params.setMargins(margin, margin/2, margin, margin/2);
        layout.setLayoutParams(params);
        
        // Create and add text view
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(R.color.primary_pink));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Increased text size
        
        // Center the text
        textView.setGravity(Gravity.CENTER);
        
        layout.addView(textView);
        
        // Add click listener with animation
        layout.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_scale));
            handleOptionClick(text);
        });
        
        return layout;
    }

    private void handleOptionClick(String option) {
        // Add user message with animation
        addMessage(option, true);
        
        // Show thinking message
        showThinkingMessage();
        
        // Handle option after delay
        new Handler().postDelayed(() -> {
            removeThinkingMessage();
            processOption(option);
        }, THINKING_DELAY);
    }

    private void addMessage(String text, boolean isUser) {
        LinearLayout messageLayout = createMessageLayout(isUser);
        TextView messageText = createMessageTextView(text, isUser);
        messageLayout.addView(messageText);
        
        // Add to chat container with animation
        chatContainer.addView(messageLayout);
        messageLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        
        // Add extra spacing after bot messages
        if (!isUser) {
            View spacer = new View(this);
            LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                16 // 16dp spacing
            );
            spacer.setLayoutParams(spacerParams);
            chatContainer.addView(spacer);
        }
        
        scrollToBottom();
    }

    private LinearLayout createMessageLayout(boolean isUser) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setBackgroundResource(isUser ? R.drawable.user_message_bg : R.drawable.bot_message_bg);
        
        // Add elevation for 3D effect
        layout.setElevation(8f);
        
        // Add margin between messages and set proper alignment
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        
        // Set margins based on user/bot
        if (isUser) {
            params.setMargins(64, 8, 16, 8); // More left margin for user messages
            params.gravity = Gravity.END;
        } else {
            params.setMargins(16, 8, 64, 8); // More right margin for bot messages
            params.gravity = Gravity.START;
        }
        
        layout.setLayoutParams(params);
        return layout;
    }

    private TextView createMessageTextView(String text, boolean isUser) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(18); // Increased text size
        textView.setTextColor(isUser ? Color.WHITE : Color.BLACK);
        
        // Add padding for better text spacing
        textView.setPadding(8, 8, 8, 8);
        
        return textView;
    }

    private void showThinkingMessage() {
        TextView thinkingView = new TextView(this);
        thinkingView.setText("AI is thinking...");
        thinkingView.setTextColor(Color.GRAY);
        thinkingView.setPadding(16, 8, 16, 8);
        thinkingView.setTag("thinking");
        chatContainer.addView(thinkingView);
        scrollToBottom();
    }

    private void removeThinkingMessage() {
        for (int i = 0; i < chatContainer.getChildCount(); i++) {
            View view = chatContainer.getChildAt(i);
            if ("thinking".equals(view.getTag())) {
                chatContainer.removeView(view);
                break;
            }
        }
    }

    private void processOption(String option) {
        showNextOptions(option);
    }

    private void showNextOptions(String userInput) {
        String[] nextOptions;
        String botResponse;
        
        switch (userInput) {
            case "Had a good moment 😊":
                botResponse = "That's wonderful! What made your day special? 😊";
                nextOptions = new String[]{"Had fun 🎉", "Achieved something 🏆", "Spent time with loved ones ❤️"};
                break;
            case "Had fun 🎉":
                botResponse = "That sounds amazing! What kind of fun activity? 🎈";
                nextOptions = new String[]{"Played a game 🎮", "Went outdoors 🌳", "Tried something new 🔥"};
                break;
            case "Achieved something 🏆":
                botResponse = "That's impressive! What did you accomplish? 🌟";
                nextOptions = new String[]{"Completed a task ✅", "Learned something new 📖", "Got appreciated 👏"};
                break;
            case "Spent time with loved ones ❤️":
                botResponse = "That's heartwarming! Who did you spend time with? 💝";
                nextOptions = new String[]{"Family 👨‍👩‍👧‍👦", "Friends 🎊", "Someone special 💖"};
                break;
            case "Had a tough time 😞":
                botResponse = "I'm here to listen and help. Would you like to share what's bothering you? 💙";
                nextOptions = new String[]{"Yes, I want to share 💙", "No, not right now."};
                break;
            case "Yes, I want to share 💙":
                botResponse = "Thank you for trusting me. What's on your mind? 🤗";
                nextOptions = new String[]{"Felt stressed 😰", "Felt lonely 😔", "Had a bad experience 😞"};
                break;
            case "Felt stressed 😰":
            case "Felt lonely 😔":
                botResponse = "I understand how you feel. Let's try something that might help you feel better. 💚";
                nextOptions = new String[]{"Breathing exercise 🌿", "Play a game 🎮", "Just need a chat 💙"};
                break;
            case "Had a bad experience 😞":
                botResponse = "I'm sorry you went through that. Let's try something to help you feel better. 🌸";
                nextOptions = new String[]{"Play a game 🎮", "Mindfulness activity 🌿", "Just talk 💙"};
                break;
            case "Just talk 💙":
            case "Still need help 💙":
                botResponse = "I'm here to listen. Would you like to share more about what's on your mind? 💭";
                nextOptions = new String[]{"Yes, I want to share more 💙", "No, let's wrap up."};
                break;
            case "No, not right now.":
                botResponse = "That's okay. Remember, I'm here whenever you need someone to talk to. 🤗";
                nextOptions = new String[]{"Yes, I want to share more 💙", "No, let's wrap up."};
                break;
            case "Got appreciated 👏":
                botResponse = "That's fantastic! Recognition feels great, doesn't it? 🌟";
                nextOptions = new String[]{"Yes, I want to share more 💙", "No, let's wrap up."};
                break;
            case "Learned something new 📖":
                botResponse = "That's wonderful! Learning is such a rewarding experience! 🎓";
                nextOptions = new String[]{"Yes, I want to share more 💙", "No, let's wrap up."};
                break;
            case "Completed a task ✅":
                botResponse = "Great job! It must feel satisfying to accomplish your goals! 🎯";
                nextOptions = new String[]{"Yes, I want to share more 💙", "No, let's wrap up."};
                break;
            case "Played a game 🎮":
            case "Went outdoors 🌳":
            case "Tried something new 🔥":
                botResponse = "That sounds like a great experience! Would you like to tell me more about it? 😊";
                nextOptions = new String[]{"Yes, I want to share more 💙", "No, let's wrap up."};
                break;
            case "Family 👨‍👩‍👧‍👦":
            case "Friends 🎊":
            case "Someone special 💖":
                botResponse = "Those moments with loved ones are precious! How did it make you feel? 💝";
                nextOptions = new String[]{"Yes, I want to share more 💙", "No, let's wrap up."};
                break;
            case "Just need a chat 💙":
                botResponse = "I'm here to listen and chat with you. What's on your mind? 💭";
                nextOptions = new String[]{"Yes, I want to share more 💙", "No, let's wrap up."};
                break;
            case "Yes, I want to share more 💙":
                botResponse = "I'm all ears! How are you feeling now? 😊";
                nextOptions = new String[]{"Had a good moment 😊", "Had a tough time 😞"};
                break;
            case "No, let's wrap up.":
            case "No, I'm good now.":
                botResponse = "I'm glad I could help! Would you like to start fresh or head back? 🌟";
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
                botResponse = "I'm here to help. What would you like to do next? 😊";
                nextOptions = new String[]{"Start a new chat 🔄", "Exit to Home ❌"};
        }
        
        // Add bot response
        addMessage(botResponse, false);
        
        // Show options after bot response
        showOptions(nextOptions);
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void restartChat() {
        chatContainer.removeAllViews();
        addMessage(getString(R.string.greeting_message), false);
        showOptions();
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