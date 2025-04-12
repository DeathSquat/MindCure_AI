package com.eyantra.mind_cure_ai;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceLandmark;

public class FaceGuideOverlay extends View {
    private Face face;
    private boolean isProcessing;
    private int previewWidth;
    private int previewHeight;
    private Paint facePaint;
    private Paint landmarkPaint;
    private Paint textPaint;
    private String currentEmotion = "";
    private Paint guidePaint;
    private RectF guideRect;
    private RectF faceRect;
    private boolean showGuide = true;

    public FaceGuideOverlay(Context context) {
        super(context);
        init();
    }

    public FaceGuideOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceGuideOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        facePaint = new Paint();
        facePaint.setColor(Color.GREEN);
        facePaint.setStyle(Paint.Style.STROKE);
        facePaint.setStrokeWidth(3f);
        facePaint.setAlpha(200);

        landmarkPaint = new Paint();
        landmarkPaint.setColor(Color.WHITE);
        landmarkPaint.setStyle(Paint.Style.FILL);
        landmarkPaint.setStrokeWidth(8f);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        guidePaint = new Paint();
        guidePaint.setColor(Color.WHITE);
        guidePaint.setStyle(Paint.Style.STROKE);
        guidePaint.setStrokeWidth(5f);
        guidePaint.setAlpha(128);

        guideRect = new RectF();
        faceRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Center the guide rectangle
        float size = Math.min(w, h) * 0.6f;
        float left = (w - size) / 2;
        float top = (h - size) / 2;
        guideRect.set(left, top, left + size, top + size);
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

        if (showGuide) {
            canvas.drawOval(guideRect, guidePaint);
        }
        
        if (faceRect != null) {
            canvas.drawRect(faceRect, facePaint);
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

    public void updateFace(Face face, boolean showGuide) {
        this.face = face;
        this.showGuide = showGuide;
        if (face != null) {
            faceRect.set(face.getBoundingBox());
        } else {
            faceRect.setEmpty();
        }
        invalidate();
    }

    public void updateEmotion(String emotion) {
        this.currentEmotion = emotion;
        invalidate();
    }
}