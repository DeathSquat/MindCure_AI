package com.eyantra.mind_cure_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.eyantra.mind_cure_ai.models.User;

public class SignupActivity extends AppCompatActivity {
    private TextInputEditText nameInput, emailInput, passwordInput;
    private MaterialButton signupButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupButton = findViewById(R.id.signupButton);
        View loginPrompt = findViewById(R.id.loginPrompt);

        // Set click listeners
        signupButton.setOnClickListener(v -> attemptSignup());
        loginPrompt.setOnClickListener(v -> finish());
    }

    private void attemptSignup() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        if (name.isEmpty()) {
            nameInput.setError("Name is required");
            return;
        }
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        // Show loading state
        signupButton.setEnabled(false);
        signupButton.setText("Creating account...");

        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Create user profile in Firestore
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(userId, name, email);
                        db.collection("users").document(userId).set(user)
                                .addOnCompleteListener(dbTask -> {
                                    signupButton.setEnabled(true);
                                    signupButton.setText("Sign Up");

                                    if (dbTask.isSuccessful()) {
                                        // Sign up success, update UI with the signed-in user's information
                                        startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        // If database update fails, display a message to the user
                                        Toast.makeText(SignupActivity.this, "Failed to create user profile: " + 
                                                dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // If sign up fails, display a message to the user
                        signupButton.setEnabled(true);
                        signupButton.setText("Sign Up");
                        Toast.makeText(SignupActivity.this, "Sign up failed: " + 
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
} 