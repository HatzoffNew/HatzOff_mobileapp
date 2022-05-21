package com.aliyahatzoff.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.aliyahatzoff.R;
import com.daasuu.gpuv.egl.filter.GlFilter;

import java.util.Arrays;
import java.util.List;

public enum  StickersType {
    Sticker1,
    Sticker2,
    Sticker3,
    Sticker4;

    public static List<StickersType> createStickersList() {
        return Arrays.asList(StickersType.values());
    }
    public static GlFilter createGlFilter(StickersType filterType, Context context) {
        switch (filterType) {
            case Sticker1:
                Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
                Bitmap bitmap = Bitmap.createScaledBitmap(largeIcon, 120, 52, true);
                return new com.aliyahatzoff.utils.GlWatermarkFilter(bitmap, com.aliyahatzoff.utils.GlWatermarkFilter.Position.LEFT_TOP);
               case Sticker2:
                Bitmap largeIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.wow);
                Bitmap bitmap1= Bitmap.createScaledBitmap(largeIcon1, 120, 52, true);
                return new com.aliyahatzoff.utils.GlWatermarkFilter(bitmap1, com.aliyahatzoff.utils.GlWatermarkFilter.Position.LEFT_TOP);
            default:
                return new GlFilter();
    }
    }
}
