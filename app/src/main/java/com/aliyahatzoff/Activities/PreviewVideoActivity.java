package com.aliyahatzoff.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.FilterType;
import com.aliyahatzoff.utils.Filter_Adapter;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.MovieWrapperView;
import com.aliyahatzoff.utils.PlayerUtil;
import com.aliyahatzoff.utils.Variables;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.egl.filter.GlFilterGroup;
import com.daasuu.gpuv.egl.filter.GlWatermarkFilter;
import com.daasuu.gpuv.player.GPUPlayerView;
import com.daasuu.gpuv.player.PlayerScaleType;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.otaliastudios.cameraview.filter.Filters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class PreviewVideoActivity extends AppCompatActivity implements Player.Listener {
    private static final String TAG = PreviewVideoActivity.class.getSimpleName();
    final List<FilterType> filterTypes = FilterType.createFilterList();
    Filter_Adapter adapter;
    RecyclerView recylerview;
    Filters filters[];
    static int currentfilter = 0;
    Context context;
    ImageView flt, stickers;
    public static int select_postion = 0;
    GPUPlayerView gpuPlayerView;
    boolean click = false;
    PlayerView playerView;
    String draft_file,speed_video;
    String video_url;
    PlaybackParameters param;
    LinearLayout smilyLayout, add_songsLayout, tv_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_video);
        filters = Filters.values();
        select_postion = 0;
        String isSoundSelected;
        context = PreviewVideoActivity.this;
        recylerview = findViewById(R.id.rv_filter);
        recylerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        gpuPlayerView = new GPUPlayerView(this);

        Intent intent = getIntent();
        if (intent != null) {
            String fromWhere = intent.getStringExtra("fromWhere");
            if (fromWhere != null && fromWhere.equals("video_recording")) {
                isSoundSelected = intent.getStringExtra("isSoundSelected");
                draft_file = intent.getStringExtra("draft_file");
            } else {
                draft_file = intent.getStringExtra("draft_file");
            }
        }
        video_url = getIntent().getStringExtra("path");
        ImageView imgclose=findViewById(R.id.imgClose);
        imgclose.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        });


        adapter = new Filter_Adapter(this, filterTypes, new Filter_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, FilterType item) {
                select_postion = postion;
                gpuPlayerView.setGlFilter(FilterType.createGlFilter(filterTypes.get(postion), getApplicationContext()));
                adapter.notifyDataSetChanged();
            }
        }, new Filter_Adapter.Fine() {
            @Override
            public void Choose(int position) {
                select_postion = position;
                gpuPlayerView.setGlFilter(FilterType.createGlFilter(filterTypes.get(position), getApplicationContext()));

            }
        });
        Set_Player(video_url);
        recylerview.setAdapter(adapter);
        findViewById(R.id.postvideo).setOnClickListener(v -> {
            if (select_postion == 0) {

                try {
                    Functions.copyFile(new File(video_url),
                            new File(Variables.output_filter_file),this);
                    GotopostScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(Variables.tag, e.toString());
                    Save_Video();
                }

            } else {
                Save_Video();
            }
        });
    }


    SimpleExoPlayer player;

    public void Set_Player(String path) {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        player = new SimpleExoPlayer.Builder( context,PlayerUtil.renderersFactory(context))
              .setMediaSourceFactory(PlayerUtil.createMediaSourceFactory(context))
              .setTrackSelector(trackSelector)
              .build();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Hatzoff"));

        /*MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(path));*/
        player.setMediaItem(PlayerUtil.getMediaItem(Uri.parse(path)));
        player.prepare();
        player.setRepeatMode(Player.REPEAT_MODE_ALL);

        player.addListener(this);
        if (null == getIntent().getStringExtra("speed")) {
        } else {
            speed_video=getIntent().getStringExtra("speed");
            param = new PlaybackParameters(Float.parseFloat(getIntent().getStringExtra("speed")));
        }
        player.setPlaybackParameters(param);
        player.setPlayWhenReady(true);

        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(path);
        String rotation = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);

        if (rotation != null && rotation.equalsIgnoreCase("0")) {
            gpuPlayerView.setPlayerScaleType(PlayerScaleType.RESIZE_FIT_WIDTH);
        } else
            gpuPlayerView.setPlayerScaleType(PlayerScaleType.RESIZE_NONE);


        gpuPlayerView.setSimpleExoPlayer(player);
        gpuPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).addView(gpuPlayerView);

        gpuPlayerView.onResume();

    }

    public void Save_Video() {
        Functions.Show_determinent_loader(this, false, false);
        try
        {
            File f=new File(Variables.stikerfile);
            InputStream inputStream = getResources().openRawResource(R.raw.wmark);
            OutputStream out=new FileOutputStream(f);
            byte buf[]=new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0)
                out.write(buf,0,len);
            out.close();
            inputStream.close();

        }catch(Exception e){}
        new GPUMp4Composer(video_url, Variables.output_filter_file)
               // .size( 540,  960)
                //.fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .filter(new GlFilterGroup(FilterType.createGlFilter(filterTypes.get(select_postion), getApplicationContext()),
                        new GlWatermarkFilter(BitmapFactory.decodeFile(Variables.stikerfile), GlWatermarkFilter.Position.LEFT_BOTTOM)
                        ))
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Log.d("resp", "" + (int) (progress * 100));
                        Functions.Show_loading_progress((int) (progress * 100));
                    }

                    @Override
                    public void onCompleted() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Functions.cancel_determinent_loader();
                                GotopostScreen();


                            }
                        });


                    }

                    @Override
                    public void onCanceled() {
                        Log.d("resp", "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {

                        Log.d("resp", exception.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    Functions.cancel_determinent_loader();

                                    Toast.makeText(PreviewVideoActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {

                                }
                            }
                        });

                    }
                })
                .start();


    }

    public void GotopostScreen() {

        Intent intent = new Intent(PreviewVideoActivity.this, PostVideoActivity.class);
        intent.putExtra("draft_file", draft_file);
        intent.putExtra("speed",speed_video);

        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (player != null) {
            player.setPlayWhenReady(false);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (player != null) {
            player.setPlayWhenReady(true);

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (player != null) {
            player.setPlayWhenReady(true);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.removeListener(PreviewVideoActivity.this);
            player.release();
            player = null;
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline,  int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(PlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
    MediaPlayer audio;

    public void preparedAudio() {
        player.setVolume(0);

        File file = new File(Variables.app_hided_folder + Variables.SelectedAudio);
        if (file.exists()) {
            audio = new MediaPlayer();
            try {
                audio.setDataSource(Variables.app_hided_folder + Variables.SelectedAudio);
                audio.prepare();
                audio.setLooping(true);


                player.seekTo(0);
                player
                        .setPlayWhenReady(true);
                audio.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
