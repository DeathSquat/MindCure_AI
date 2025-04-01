package com.eyantra.mind_cure_ai;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class GoalSettingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GoalAdapter adapter;
    private GoalManager goalManager;
    private FloatingActionButton fabAddGoal;
    private EditText etTitle, etDescription;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_setting);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Set Your Goals");

        // Initialize GoalManager
        goalManager = new GoalManager(this);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        fabAddGoal = findViewById(R.id.fabAddGoal);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GoalAdapter(goalManager.getGoals(), this::onGoalClick);
        recyclerView.setAdapter(adapter);

        // Setup FAB click listener
        fabAddGoal.setOnClickListener(v -> showAddGoalDialog());

        // Setup Save button click listener
        btnSave.setOnClickListener(v -> saveGoal());
    }

    private void showAddGoalDialog() {
        etTitle.setVisibility(View.VISIBLE);
        etDescription.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
    }

    private void saveGoal() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Goal goal = new Goal(title, description, System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)); // 7 days default deadline
        goalManager.addGoal(goal);
        adapter.updateGoals(goalManager.getGoals());

        // Clear and hide input fields
        etTitle.setText("");
        etDescription.setText("");
        etTitle.setVisibility(View.GONE);
        etDescription.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);

        Toast.makeText(this, "Goal added successfully", Toast.LENGTH_SHORT).show();
    }

    private void onGoalClick(Goal goal) {
        // Toggle goal completion
        goal.setCompleted(!goal.isCompleted());
        goalManager.updateGoal(goalManager.getGoals().indexOf(goal), goal);
        adapter.updateGoals(goalManager.getGoals());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 