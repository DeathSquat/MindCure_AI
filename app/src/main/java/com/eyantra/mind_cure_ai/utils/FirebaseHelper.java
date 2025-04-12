package com.eyantra.mind_cure_ai.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseHelper {
    private static FirebaseHelper instance;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private StorageReference storage;

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    // Authentication methods
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void signOut() {
        auth.signOut();
    }

    // Database methods
    public void saveUserProfile(String userId, String name, String email) {
        DatabaseReference userRef = database.child("users").child(userId);
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
    }

    public void saveChatMessage(String userId, String message, boolean isUser) {
        DatabaseReference chatRef = database.child("chats").child(userId).push();
        chatRef.child("message").setValue(message);
        chatRef.child("isUser").setValue(isUser);
        chatRef.child("timestamp").setValue(System.currentTimeMillis());
    }

    public void saveEmotionData(String userId, String emotion, String timestamp) {
        DatabaseReference emotionRef = database.child("emotions").child(userId).push();
        emotionRef.child("emotion").setValue(emotion);
        emotionRef.child("timestamp").setValue(timestamp);
    }

    public void saveBooking(String userId, String doctorId, String date, String time) {
        DatabaseReference bookingRef = database.child("bookings").child(userId).push();
        bookingRef.child("doctorId").setValue(doctorId);
        bookingRef.child("date").setValue(date);
        bookingRef.child("time").setValue(time);
        bookingRef.child("status").setValue("pending");
    }

    // Storage methods
    public StorageReference getProfileImageRef(String userId) {
        return storage.child("profile_images").child(userId);
    }

    public StorageReference getEmotionImageRef(String userId, String imageId) {
        return storage.child("emotion_images").child(userId).child(imageId);
    }
} 