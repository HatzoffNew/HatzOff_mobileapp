package com.aliyahatzoff.utils;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class PlayerUtil {
    public static MediaSourceFactory createMediaSourceFactory(Context context){
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context);
        return new DefaultMediaSourceFactory(dataSourceFactory);
    }

    public static RenderersFactory renderersFactory(Context context){
        return new DefaultRenderersFactory(context.getApplicationContext())
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
    }

    public static MediaItem getMediaItem(Uri uri){
        return new MediaItem.Builder().setUri(uri).build();
    }
}
