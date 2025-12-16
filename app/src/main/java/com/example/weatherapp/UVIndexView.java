package com.example.weatherapp;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class UVIndexView extends View {
    private Paint circlePaint, textPaint, backgroundPaint;
    private int uvIndex = 5;
    private int maxUV = 12;

    public UVIndexView(Context context) {
        super(context);
        init();
    }

    public UVIndexView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(8f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(24f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(8f);
    }

    public void setUVIndex(int index) {
        this.uvIndex = index;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 10;
        int centerX = width / 2;
        int centerY = height / 2;

        // Фоновый круг
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // Заполненная часть
        float sweepAngle = (float) uvIndex / maxUV * 360f;
        RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Устанавливаем цвет в зависимости от УФ-индекса
        if (uvIndex <= 2) {
            circlePaint.setColor(Color.GREEN);
        } else if (uvIndex <= 5) {
            circlePaint.setColor(Color.YELLOW);
        } else if (uvIndex <= 7) {
            circlePaint.setColor(Color.rgb(255, 165, 0)); // оранжевый
        } else if (uvIndex <= 10) {
            circlePaint.setColor(Color.RED);
        } else {
            circlePaint.setColor(Color.rgb(128, 0, 128)); // фиолетовый
        }

        canvas.drawArc(rect, -90, sweepAngle, false, circlePaint);
    }
}