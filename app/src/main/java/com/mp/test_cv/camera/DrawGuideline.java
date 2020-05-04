package com.mp.test_cv.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.TextureView;
import android.view.View;

public class DrawGuideline extends View {
    TextureView textureView;
    public DrawGuideline(Context context, TextureView textureView) {
        super(context);
        this.textureView = textureView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        DrawBox(canvas);
        super.onDraw(canvas);
    }

    protected void DrawBox(Canvas canvas) {
        int w = textureView.getMeasuredWidth();
        int h = textureView.getMeasuredHeight();

        Paint paint = new Paint();
        paint.setStrokeWidth(textureView.getMeasuredWidth() / 50.0f);
        paint.setStyle(Paint.Style.STROKE);


        paint.setColor(Color.WHITE);
        paint.setAlpha(128);
        canvas.drawRect(300,150, 400+(w/2.0f) , 400+(h/1.5f),paint);

    }
}

