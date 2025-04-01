package com.eyantra.mind_cure_ai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.Face;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;

import kotlin.OptIn;

@OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
public class EmotionDetectionActivity extends AppCompatActivity {
    private static final String TAG = "EmotionDetection";
    private PreviewView viewFinder;
    private FaceGuideOverlay faceGuideOverlay;
    private LinearProgressIndicator emotionConfidence;
    private ExecutorService cameraExecutor;
    private FaceDetector faceDetector;
    private ImageAnalysis imageAnalysis;
    private boolean isCameraInitialized = false;
    private long lastProcessedTime = 0;
    private static final long PROCESSING_INTERVAL = 100; // Slowed down to 100ms
    private static final long WAIT_TIME = 3000; // 3 seconds wait time
    private static final long DETECTION_WINDOW = 10000; // 10 seconds detection window
    private long startWaitTime = 0;
    private long startDetectionTime = 0;
    private boolean isWaiting = false;
    private boolean isDetecting = false;
    private boolean detectionLocked = false;
    private String finalEmotion = "";
    private String finalPunchLine = "";

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private int detectionCount = 0;
    private static final int REQUIRED_DETECTIONS = 5;
    private String currentEmotion = "Processing...";
    private float accumulatedSmileProb = 0;
    private float accumulatedLeftEyeProb = 0;
    private float accumulatedRightEyeProb = 0;
    private float accumulatedHeadAngle = 0;
    private TextView emotionText;
    private TextView punchLineText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_detection);

        viewFinder = findViewById(R.id.viewFinder);
        faceGuideOverlay = findViewById(R.id.faceGuideOverlay);
        emotionConfidence = findViewById(R.id.emotionConfidence);
        emotionText = findViewById(R.id.emotionText);
        punchLineText = findViewById(R.id.punchLineText);
        
        // Set initial progress
        emotionConfidence.setMax(100); // Changed to percentage
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
        emotionConfidence.setTrackThickness(8); // Make it thicker
        emotionConfidence.setTrackCornerRadius(8); // Rounded corners
        emotionConfidence.setIndicatorColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Check and request permissions
        if (allPermissionsGranted()) {
            initializeCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @ExperimentalGetImage
    private void initializeCamera() {
        if (isCameraInitialized) return;

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                    int rotationDegrees = image.getImageInfo().getRotationDegrees();
                    InputImage inputImage = InputImage.fromMediaImage(
                            image.getImage(), rotationDegrees);
                    
                    // Process the image
                    faceDetector.process(inputImage)
                            .addOnSuccessListener(faces -> {
                                if (!faces.isEmpty()) {
                                    Face face = faces.get(0);
                                    processDetection(face);
                                } else {
                                    // Reset when face is lost
                                    detectionCount = 0;
                                    accumulatedSmileProb = 0;
                                    accumulatedLeftEyeProb = 0;
                                    accumulatedRightEyeProb = 0;
                                    accumulatedHeadAngle = 0;
                                    updateUI("No face detected", 0, "😔 Sad");
                                    faceGuideOverlay.updateFace(null, true);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Face detection failed", e);
                                updateUI("Detection failed", 0, "😔 Sad");
                            })
                            .addOnCompleteListener(task -> {
                                image.close();
                            });
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
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to start camera: " + exc.getMessage(), 
                            Toast.LENGTH_LONG).show();
                });
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void processDetection(Face face) {
        long currentTime = System.currentTimeMillis();

        if (detectionLocked) {
            // If detection is locked, show final results and navigate to chat
            updateUI(finalEmotion, 100, finalPunchLine);
            navigateToChat(finalEmotion);
            return;
        }

        if (!isWaiting && !isDetecting) {
            // Start waiting period
            startWaitTime = currentTime;
            isWaiting = true;
            updateUI("Get ready! Starting in 3...", 0, "😊 Position yourself!");
            return;
        }

        if (isWaiting) {
            long waitElapsed = currentTime - startWaitTime;
            if (waitElapsed < WAIT_TIME) {
                int seconds = (int) ((WAIT_TIME - waitElapsed) / 1000) + 1;
                updateUI("Starting in " + seconds + "...", 
                    (int)((waitElapsed * 100) / WAIT_TIME),
                    "😊 Get ready!");
                return;
            } else {
                isWaiting = false;
                isDetecting = true;
                startDetectionTime = currentTime;
                detectionCount = 0;
                accumulatedSmileProb = 0;
                accumulatedLeftEyeProb = 0;
                accumulatedRightEyeProb = 0;
                accumulatedHeadAngle = 0;
            }
        }

        if (isDetecting) {
            long detectionElapsed = currentTime - startDetectionTime;
            if (detectionElapsed > DETECTION_WINDOW) {
                // Lock the detection and show final results
                isDetecting = false;
                detectionLocked = true;
                EmotionResult finalResult = determineEmotion(
                    accumulatedSmileProb / detectionCount,
                    accumulatedLeftEyeProb / detectionCount,
                    accumulatedRightEyeProb / detectionCount,
                    accumulatedHeadAngle / detectionCount
                );
                finalEmotion = finalResult.emotion;
                finalPunchLine = getPunchLine(finalResult.emotion);
                updateUI(finalEmotion, 100, finalPunchLine);
                return;
            }

            // Process the current frame
            Float smileProb = face.getSmilingProbability();
            Float leftEyeOpenProb = face.getLeftEyeOpenProbability();
            Float rightEyeOpenProb = face.getRightEyeOpenProbability();
            float headEulerAngleY = face.getHeadEulerAngleY();

            if (smileProb != null && leftEyeOpenProb != null && rightEyeOpenProb != null) {
                accumulatedSmileProb += smileProb;
                accumulatedLeftEyeProb += leftEyeOpenProb;
                accumulatedRightEyeProb += rightEyeOpenProb;
                accumulatedHeadAngle += headEulerAngleY;
                detectionCount++;

                // Calculate progress with some randomization for interactivity
                int baseProgress = (int)((detectionElapsed * 100) / DETECTION_WINDOW);
                int randomOffset = (int)(Math.random() * 5) - 2; // Random offset between -2 and 2
                int progress = Math.min(Math.max(baseProgress + randomOffset, 0), 99);

                EmotionResult currentResult = determineEmotion(
                    smileProb,
                    leftEyeOpenProb,
                    rightEyeOpenProb,
                    headEulerAngleY
                );

                updateUI("Processing: " + progress + "%", 
                    progress, 
                    "🎯 Keep steady...");
            }
        }
    }

    private void updateUI(String text, int progress, String punchLine) {
        runOnUiThread(() -> {
            emotionText.setText(text);
            emotionConfidence.setProgress(progress);
            punchLineText.setText(punchLine);
            updateProgressIndicatorColor(progress);
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
            case "😊 Happy":
                return "Your smile lights up the room! 🌟";
            case "😔 Sad":
                return "Turn that frown upside down! 🌈";
            case "😐 Neutral":
                return "What's on your mind? 💭";
            case "Center your face":
                return "Let me see you better! 👀";
            default:
                return "Let's detect your mood! 🎭";
        }
    }

    private EmotionResult determineEmotion(float smileProb, float leftEyeOpenProb, float rightEyeOpenProb, float headEulerAngleY) {
        // Calculate average eye open probability
        float avgEyeOpenProb = (leftEyeOpenProb + rightEyeOpenProb) / 2;

        // Check if face is properly aligned (head not turned too much)
        boolean isFaceAligned = Math.abs(headEulerAngleY) < 25; // Increased threshold for more flexibility

        if (!isFaceAligned) {
            return new EmotionResult("Center your face", 0.5f);
        }

        // More refined emotion detection logic with adjusted thresholds
        if (smileProb > 0.4) {  // Lowered threshold for happy
            return new EmotionResult("😊 Happy", smileProb);
        } else if (smileProb < 0.2) {  // Lowered threshold for sad
            return new EmotionResult("😔 Sad", 1 - smileProb);
        } else {  // Neutral
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
    protected void onResume() {
        super.onResume();
        if (allPermissionsGranted() && !isCameraInitialized) {
            initializeCamera();
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
        finish();
    }
}