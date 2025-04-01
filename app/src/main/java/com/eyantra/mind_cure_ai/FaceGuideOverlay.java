package com.eyantra.mind_cure_ai;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceLandmark;

public class FaceGuideOverlay extends View {
    private Face face;
    private boolean isProcessing;
    private int previewWidth;
    private int previewHeight;
    private final Paint facePaint;
    private final Paint landmarkPaint;
    private final Paint textPaint;
    private String currentEmotion = "";

    public FaceGuideOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        facePaint = new Paint();
        facePaint.setColor(Color.TRANSPARENT); // Remove the green circle
        facePaint.setStyle(Paint.Style.STROKE);
        facePaint.setStrokeWidth(4f);

        landmarkPaint = new Paint();
        landmarkPaint.setColor(Color.WHITE);
        landmarkPaint.setStyle(Paint.Style.FILL);
        landmarkPaint.setStrokeWidth(8f);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (face == null) return;

        // Scale factors
        float scaleX = getWidth() / (float) previewWidth;
        float scaleY = getHeight() / (float) previewHeight;
        float scale = Math.min(scaleX, scaleY);

        // Draw facial landmarks
        drawFaceLandmark(canvas, face.getLandmark(FaceLandmark.LEFT_EYE), scale);
        drawFaceLandmark(canvas, face.getLandmark(FaceLandmark.RIGHT_EYE), scale);
        drawFaceLandmark(canvas, face.getLandmark(FaceLandmark.NOSE_BASE), scale);
        drawFaceLandmark(canvas, face.getLandmark(FaceLandmark.MOUTH_LEFT), scale);
        drawFaceLandmark(canvas, face.getLandmark(FaceLandmark.MOUTH_RIGHT), scale);

        // Draw emotion text if available
        if (!currentEmotion.isEmpty()) {
            float textX = getWidth() / 2f;
            float textY = getHeight() - 100f;
            canvas.drawText(currentEmotion, textX, textY, textPaint);
        }
    }

    private void drawFaceLandmark(Canvas canvas, FaceLandmark landmark, float scale) {
        if (landmark != null) {
            float x = translateX(landmark.getPosition().x * scale);
            float y = landmark.getPosition().y * scale;
            canvas.drawCircle(x, y, 8f, landmarkPaint);
        }
    }

    private float translateX(float x) {
        return getWidth() - x;  // Mirror for front camera
    }

    public void setPreviewSize(int width, int height) {
        previewWidth = width;
        previewHeight = height;
    }

    public void updateFace(Face face, boolean isProcessing) {
        this.face = face;
        this.isProcessing = isProcessing;
        invalidate();
    }

    public void updateEmotion(String emotion) {
        this.currentEmotion = emotion;
        invalidate();
    }
}