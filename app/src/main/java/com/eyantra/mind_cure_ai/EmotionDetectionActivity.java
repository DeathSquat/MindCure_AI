package com.eyantra.mind_cure_ai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmotionDetectionActivity extends AppCompatActivity {
    private static final String TAG = "EmotionDetection";
    private PreviewView viewFinder;
    private FaceGuideOverlay faceGuideOverlay;
    private LinearProgressIndicator emotionConfidence;
    private TextView emotionText;
    private TextView punchLineText;
    private MaterialButton skipButton;
    private ExecutorService cameraExecutor;
    private FaceDetector faceDetector;
    private ImageAnalysis imageAnalysis;
    private boolean isCameraInitialized = false;

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private static final long PROCESSING_INTERVAL = 50; // Reduced from 100ms to 50ms for faster processing
    private static final long WAIT_TIME = 2000; // Reduced from 3s to 2s
    private static final long DETECTION_WINDOW = 5000; // Reduced from 8s to 5s
    private static final int MIN_DETECTIONS = 15; // Reduced from 20 to 15 since we're processing faster
    private static final float SMILE_THRESHOLD_HIGH = 0.45f; // Increased from 0.4
    private static final float SMILE_THRESHOLD_LOW = 0.15f; // Reduced from 0.2
    private static final float EYE_OPEN_THRESHOLD = 0.25f; // Reduced from 0.3
    private static final float HEAD_ANGLE_THRESHOLD = 20f; // Reduced from 25 for stricter alignment

    private long lastProcessedTime = 0;
    private long startWaitTime = 0;
    private long startDetectionTime = 0;
    private boolean isWaiting = false;
    private boolean isDetecting = false;
    private boolean detectionLocked = false;
    private String finalEmotion = "";
    private String finalPunchLine = "";
    private int detectionCount = 0;
    private float accumulatedSmileProb = 0;
    private float accumulatedLeftEyeProb = 0;
    private float accumulatedRightEyeProb = 0;
    private float accumulatedHeadAngle = 0;
    private float accumulatedLeftEyeOpen = 0;
    private float accumulatedRightEyeOpen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_detection);

        // Initialize views
        viewFinder = findViewById(R.id.viewFinder);
        faceGuideOverlay = findViewById(R.id.faceGuideOverlay);
        emotionConfidence = findViewById(R.id.emotionConfidence);
        emotionText = findViewById(R.id.emotionText);
        punchLineText = findViewById(R.id.punchLineText);
        skipButton = findViewById(R.id.skipButton);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set initial progress
        emotionConfidence.setMax(100);
        emotionConfidence.setProgress(0);
        emotionText.setText("Position your face in the frame");
        punchLineText.setText("Get ready for emotion detection!");

        // Initialize face detector with optimized settings
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setMinFaceSize(0.15f)
                .build();
        faceDetector = FaceDetection.getClient(options);

        // Style the progress bar
        emotionConfidence.setTrackThickness(8);
        emotionConfidence.setTrackCornerRadius(8);
        emotionConfidence.setIndicatorColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Setup skip button
        skipButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            navigateToChat();
        });

        // Check and request permissions
        if (allPermissionsGranted()) {
            initializeCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void initializeCamera() {
        if (isCameraInitialized) return;

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastProcessedTime < PROCESSING_INTERVAL) {
                        image.close();
                        return;
                    }
                    lastProcessedTime = currentTime;

                    int rotationDegrees = image.getImageInfo().getRotationDegrees();
                    InputImage inputImage = InputImage.fromMediaImage(
                            image.getImage(), rotationDegrees);

                    faceDetector.process(inputImage)
                            .addOnSuccessListener(faces -> {
                                if (!faces.isEmpty()) {
                                    Face face = faces.get(0);
                                    processDetection(face);
                                    faceGuideOverlay.updateFace(face, false);
                                } else {
                                    resetDetection();
                                    updateUI("No face detected", 0, "Please position your face in the frame");
                                    faceGuideOverlay.updateFace(null, true);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Face detection failed", e);
                                updateUI("Detection failed", 0, "Please try again");
                            })
                            .addOnCompleteListener(task -> image.close());
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis
                );

                isCameraInitialized = true;
                Log.d(TAG, "Camera initialized successfully");
            } catch (Exception exc) {
                Log.e(TAG, "Camera initialization failed", exc);
                runOnUiThread(() -> Toast.makeText(this, "Failed to start camera: " + exc.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void processDetection(Face face) {
        long currentTime = System.currentTimeMillis();

        if (detectionLocked) {
            updateUI(finalEmotion, 100, finalPunchLine);
            updateSkipButtonToNext();
            return;
        }

        if (!isWaiting && !isDetecting) {
            startWaitTime = currentTime;
            isWaiting = true;
            updateUI("Get ready! Starting in 2...", 0, "Position yourself!");
            return;
        }

        if (isWaiting) {
            long waitElapsed = currentTime - startWaitTime;
            if (waitElapsed < WAIT_TIME) {
                int seconds = (int) ((WAIT_TIME - waitElapsed) / 1000) + 1;
                updateUI("Starting in " + seconds + "...", 
                    (int)((waitElapsed * 100) / WAIT_TIME),
                    "Get ready!");
                return;
            } else {
                isWaiting = false;
                isDetecting = true;
                startDetectionTime = currentTime;
                resetDetection();
            }
        }

        if (isDetecting) {
            long detectionElapsed = currentTime - startDetectionTime;
            if (detectionElapsed > DETECTION_WINDOW) {
                isDetecting = false;
                detectionLocked = true;
                EmotionResult finalResult = determineEmotion(
                    accumulatedSmileProb / detectionCount,
                    accumulatedLeftEyeProb / detectionCount,
                    accumulatedRightEyeProb / detectionCount,
                    accumulatedHeadAngle / detectionCount,
                    accumulatedLeftEyeOpen / detectionCount,
                    accumulatedRightEyeOpen / detectionCount
                );
                finalEmotion = finalResult.emotion;
                finalPunchLine = getPunchLine(finalResult.emotion);
                updateUI(finalEmotion, 100, finalPunchLine);
                updateSkipButtonToNext();
                return;
            }

            Float smileProb = face.getSmilingProbability();
            Float leftEyeOpenProb = face.getLeftEyeOpenProbability();
            Float rightEyeOpenProb = face.getRightEyeOpenProbability();
            float headEulerAngleY = face.getHeadEulerAngleY();

            if (smileProb != null && leftEyeOpenProb != null && rightEyeOpenProb != null) {
                // Weight the probabilities based on detection confidence
                float confidence = (smileProb + leftEyeOpenProb + rightEyeOpenProb) / 3;
                float weight = confidence > 0.7f ? 1.2f : 1.0f; // Boost high confidence detections

                accumulatedSmileProb += smileProb * weight;
                accumulatedLeftEyeProb += leftEyeOpenProb * weight;
                accumulatedRightEyeProb += rightEyeOpenProb * weight;
                accumulatedHeadAngle += headEulerAngleY;
                accumulatedLeftEyeOpen += leftEyeOpenProb * weight;
                accumulatedRightEyeOpen += rightEyeOpenProb * weight;
                detectionCount++;

                int baseProgress = (int)((detectionElapsed * 100) / DETECTION_WINDOW);
                int randomOffset = (int)(Math.random() * 3) - 1; // Reduced random variation
                int progress = Math.min(Math.max(baseProgress + randomOffset, 0), 99);

                String status = "Analyzing your expression...";
                if (detectionCount < MIN_DETECTIONS) {
                    status = "Gathering more data...";
                } else if (detectionCount > MIN_DETECTIONS * 2) {
                    status = "Processing complete!";
                }

                updateUI(status, progress, "Keep steady...");
            }
        }
    }

    private void resetDetection() {
        detectionCount = 0;
        accumulatedSmileProb = 0;
        accumulatedLeftEyeProb = 0;
        accumulatedRightEyeProb = 0;
        accumulatedHeadAngle = 0;
        accumulatedLeftEyeOpen = 0;
        accumulatedRightEyeOpen = 0;
    }

    private void updateUI(String text, int progress, String punchLine) {
        runOnUiThread(() -> {
            emotionText.setText(text);
            emotionConfidence.setProgress(progress);
            punchLineText.setText(punchLine);
            updateProgressIndicatorColor(progress);
            
            // Show emotion emoji if detection is complete
            if (detectionLocked) {
                emotionText.setTextSize(24);
                emotionText.setAlpha(1.0f);
            } else {
                emotionText.setTextSize(18);
                emotionText.setAlpha(0.8f);
            }
        });
    }

    private void updateProgressIndicatorColor(int progress) {
        int color;
        if (progress < 30) {
            color = ContextCompat.getColor(this, android.R.color.holo_blue_light);
        } else if (progress < 60) {
            color = ContextCompat.getColor(this, android.R.color.holo_orange_light);
        } else if (progress < 90) {
            color = ContextCompat.getColor(this, android.R.color.holo_green_light);
        } else {
            color = ContextCompat.getColor(this, android.R.color.holo_purple);
        }
        emotionConfidence.setIndicatorColor(color);
    }

    private String getPunchLine(String emotion) {
        switch (emotion) {
            case "😄 Very Happy":
                return "Your joy is contagious! 🌟";
            case "😊 Happy":
                return "Your smile brightens the day! ☀️";
            case "😔 Sad":
                return "Let's talk about what's on your mind 💭";
            case "😴 Tired":
                return "Time for some self-care! 💆‍♂️";
            case "😐 Neutral":
                return "What's on your mind? 🤔";
            case "Center your face":
                return "Let me see you better! 👀";
            default:
                return "Let's detect your mood! 🎭";
        }
    }

    private EmotionResult determineEmotion(float smileProb, float leftEyeProb, float rightEyeProb, 
                                         float headAngle, float leftEyeOpen, float rightEyeOpen) {
        float avgEyeOpenProb = (leftEyeOpen + rightEyeOpen) / 2;
        boolean isFaceAligned = Math.abs(headAngle) < HEAD_ANGLE_THRESHOLD;

        if (!isFaceAligned) {
            return new EmotionResult("Center your face", 0.5f);
        }

        if (smileProb > SMILE_THRESHOLD_HIGH) {
            return new EmotionResult("😄 Very Happy", smileProb);
        } else if (smileProb < SMILE_THRESHOLD_LOW) {
            if (avgEyeOpenProb < EYE_OPEN_THRESHOLD) {
                return new EmotionResult("😴 Tired", 1 - smileProb);
            }
            return new EmotionResult("😔 Sad", 1 - smileProb);
        } else {
            if (avgEyeOpenProb < EYE_OPEN_THRESHOLD) {
                return new EmotionResult("😐 Neutral", 0.5f);
            }
            return new EmotionResult("😐 Neutral", 0.5f);
        }
    }

    private static class EmotionResult {
        final String emotion;
        final float confidence;

        EmotionResult(String emotion, float confidence) {
            this.emotion = emotion;
            this.confidence = confidence;
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initializeCamera();
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    private void navigateToChat(String emotion) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("detected_emotion", emotion);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void navigateToChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void updateSkipButtonToNext() {
        runOnUiThread(() -> {
            skipButton.setText("Next");
            skipButton.setIconResource(R.drawable.ic_arrow_forward);
            skipButton.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
                navigateToChat(finalEmotion);
            });
        });
    }
} 