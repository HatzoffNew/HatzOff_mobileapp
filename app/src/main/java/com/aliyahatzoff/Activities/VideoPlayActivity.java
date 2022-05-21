package com.aliyahatzoff.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.google.android.exoplayer2.PlaybackException;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.aliyahatzoff.utils.PlayerUtil;

public class VideoPlayActivity extends AppCompatActivity implements View.OnClickListener, Player.Listener {

    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        path=getIntent().getStringExtra("video_path");
        Set_Player();

//        if (checkPermission()) {
//            setVideo();
//        }
    }

    SimpleExoPlayer video_player;
    public void Set_Player(){

        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        video_player = new SimpleExoPlayer.Builder( this,PlayerUtil.renderersFactory(this))
              .setMediaSourceFactory(PlayerUtil.createMediaSourceFactory(this))
              .setTrackSelector(trackSelector)
              .build();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Hatzoff"));

        /*MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(path));*/
        video_player.setMediaItem(PlayerUtil.getMediaItem(Uri.parse(path)));
        video_player.prepare();

        video_player.setRepeatMode(Player.REPEAT_MODE_OFF);
        video_player.addListener(this);



        final PlayerView playerView=findViewById(R.id.playerview);
        playerView.setPlayer(video_player);

        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        video_player.setPlayWhenReady(true);


    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if(playbackState== Player.STATE_ENDED){

            video_player.seekTo(0);
            video_player.setPlayWhenReady(true);

        }
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

@Override
    protected void onResume() {
        super.onResume();
        if(video_player!=null){
            video_player.setPlayWhenReady(true);
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        try{
            if(video_player!=null){
                video_player.setPlayWhenReady(false);
            }
                 }catch (Exception e){

        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(video_player!=null){
            video_player.release();
        }
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Functions.deleteCache(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(video_player!=null){
            video_player.release();
        }

        System.out.println("outout");
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }
}