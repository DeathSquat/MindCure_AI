package com.eyantra.mind_cure_ai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.eyantra.mind_cure_ai.adapters.AvatarAdapter;

public class AvatarSelectionActivity extends AppCompatActivity {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_SELECTED_AVATAR = "selected_avatar";
    private int selectedAvatarResource = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_selection);

        RecyclerView recyclerView = findViewById(R.id.avatarRecyclerView);
        Button confirmButton = findViewById(R.id.confirmButton);

        // Set up the RecyclerView with a grid layout
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        AvatarAdapter adapter = new AvatarAdapter();
        recyclerView.setAdapter(adapter);

        // Handle avatar selection
        adapter.setOnAvatarSelectedListener((position, resourceId) -> {
            selectedAvatarResource = resourceId;
            confirmButton.setEnabled(true);
        });

        // Handle confirmation
        confirmButton.setOnClickListener(v -> {
            if (selectedAvatarResource != -1) {
                // Save the selected avatar
                SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                prefs.edit().putInt(KEY_SELECTED_AVATAR, selectedAvatarResource).apply();

                // Return to previous activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_avatar", selectedAvatarResource);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        // Initially disable the confirm button
        confirmButton.setEnabled(false);
    }
} 