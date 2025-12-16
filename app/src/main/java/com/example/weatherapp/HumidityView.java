package com.example.weatherapp;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class HumidityView extends View {
    private Paint circlePaint, textPaint, backgroundPaint;
    private int humidity = 50;

    public HumidityView(Context context) {
        super(context);
        init();
    }

    public HumidityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.BLUE);
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

    public void setHumidity(int humidity) {
        this.humidity = Math.min(100, Math.max(0, humidity));
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
        float sweepAngle = humidity * 3.6f; // 360° / 100%
        RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Градиент от синего к голубому
        Shader shader = new SweepGradient(centerX, centerY,
                new int[] {Color.BLUE, Color.CYAN, Color.BLUE}, null);
        circlePaint.setShader(shader);

        canvas.drawArc(rect, -90, sweepAngle, false, circlePaint);
    }
}