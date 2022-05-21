package com.aliyahatzoff.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.daasuu.gpuv.egl.filter.GlOverlayFilter;

public class GlWatermarkFilter extends GlOverlayFilter {

    public Bitmap bitmap;
    public Position position = Position.LEFT_TOP;

//    public GlWatermarkFilter(Bitmap bitmap) {
//        this.bitmap = bitmap;
//    }


    public GlWatermarkFilter(Bitmap bitmap, Position position) {
        this.bitmap = bitmap;
        this.position = position;
    }


    @Override
    protected void drawCanvas(Canvas canvas) {
        if (bitmap != null && !bitmap.isRecycled()) {
            switch (position) {
                case LEFT_TOP:
                    canvas.drawBitmap(bitmap, 25, 30, null);
                    break;
                case LEFT_BOTTOM:
                    canvas.drawBitmap(bitmap, 670, 200 - bitmap.getHeight(), null);
                    break;
                case RIGHT_TOP:
                    canvas.drawBitmap(bitmap, canvas.getWidth() - bitmap.getWidth(), 0, null);
                    break;
                case RIGHT_BOTTOM:
                    canvas.drawBitmap(bitmap, canvas.getWidth() - bitmap.getWidth(), canvas.getHeight() - bitmap.getHeight(), null);
                    break;
            }
        }
    }
    public enum Position {
        LEFT_TOP,
        LEFT_BOTTOM,
        RIGHT_TOP,
        RIGHT_BOTTOM
    }
}