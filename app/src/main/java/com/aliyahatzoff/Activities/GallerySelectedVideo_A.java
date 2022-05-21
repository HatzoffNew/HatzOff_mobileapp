package com.aliyahatzoff.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Merge_Sound;
import com.aliyahatzoff.utils.PlayerUtil;
import com.aliyahatzoff.utils.Variables;
import com.coremedia.iso.boxes.Container;
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
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GallerySelectedVideo_A extends AppCompatActivity implements View.OnClickListener, Player.Listener {
    String path;
    TextView add_sound_txt;
    String draft_file;
    public static int Sounds_list_Request_code = 1;
    ActivityResultLauncher<Intent> launcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_selected_video_);
        Intent intent=getIntent();
        if(intent!=null){
            path=intent.getStringExtra("video_path");
            draft_file=intent.getStringExtra("draft_file");
        }

        Variables.Selected_sound_id="null";

        findViewById(R.id.Goback).setOnClickListener(this);

        add_sound_txt=findViewById(R.id.add_sound_txt);
        add_sound_txt.setOnClickListener(this);

        findViewById(R.id.next_btn).setOnClickListener(this);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if((result.getResultCode()==Sounds_list_Request_code)&&(result.getData() != null)){

                            if(result.getData().getStringExtra("isSelected").equals("yes")){
                                add_sound_txt.setText(result.getData().getStringExtra("sound_name"));
                                Variables.Selected_sound_id=String.valueOf(result.getData().getIntExtra("sound_id", 0));
                                PreparedAudio();
                            }
                        }
                    }
                });

        Set_Player();

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
        switch (v.getId()){

            case R.id.Goback:
                finish();
                overridePendingTransition(R.anim.in_from_top,R.anim.out_from_bottom);
                break;

            case R.id.add_sound_txt:
                Intent intent =new Intent(GallerySelectedVideo_A.this, MusicActivity.class);
//                startActivityForResult(intent,Sounds_list_Request_code);
                launcher.launch(intent);
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                break;

            case R.id.next_btn:

                if(video_player!=null) {
                    video_player.setPlayWhenReady(false);
                }
                if(audio!=null) {
                    audio.pause();
                }
                append();
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Sounds_list_Request_code){
            if(data!=null){

                if(data.getStringExtra("isSelected").equals("yes")){
                    add_sound_txt.setText(data.getStringExtra("sound_name"));
                    Variables.Selected_sound_id=String.valueOf(data.getIntExtra("sound_id", 0));
                    PreparedAudio();
                }

            }

        }
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
            if(audio!=null){
                audio.pause();
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

        if(audio!=null){
            audio.pause();
            audio.release();
        }
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Functions.deleteCache(this);
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
        if(playbackState== Player.STATE_ENDED){

            video_player.seekTo(0);
            video_player.setPlayWhenReady(true);

            if(audio!=null){
                audio.seekTo(0);
                audio.start();
            }
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
    MediaPlayer audio;
    public  void PreparedAudio() {
        video_player.setVolume(0);
        File file = new File(Variables.app_hided_folder + Variables.SelectedAudio);
        if (file.exists()) {
            audio = new MediaPlayer();
            try {
                audio.setDataSource(Variables.app_hided_folder + Variables.SelectedAudio);
                audio.prepare();
                audio.setLooping(true);

                video_player.seekTo(0);
                video_player.setPlayWhenReady(true);
                audio.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean append() {
        final ProgressDialog progressDialog=new ProgressDialog(GallerySelectedVideo_A.this);
        new Thread(new Runnable() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    public void run() {

                        progressDialog.setMessage("Please wait..");
                        progressDialog.show();
                    }
                });

                try {

                    ArrayList<String> video_list=new ArrayList<>();

                    File file=new File(path);

                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(GallerySelectedVideo_A.this, Uri.fromFile(file));
                    String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                    boolean isVideo = "yes".equals(hasVideo);



                    if(isVideo && file.length()>3000){
                        video_list.add(path);
                    }


                    Movie[] inMovies = new Movie[video_list.size()];

                    for (int i=0;i<video_list.size();i++){

                        inMovies[i]= MovieCreator.build(video_list.get(i));
                    }


                    List<Track> videoTracks = new LinkedList<Track>();
                    List<Track> audioTracks = new LinkedList<Track>();
                    for (Movie m : inMovies) {
                        for (Track t : m.getTracks()) {
                            if (t.getHandler().equals("soun")) {
                                audioTracks.add(t);
                            }
                            if (t.getHandler().equals("vide")) {
                                videoTracks.add(t);
                            }
                        }
                    }
                    Movie result = new Movie();
                    if (audioTracks.size() > 0) {
                        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    }

                    Container out = new DefaultMp4Builder().build(result);

                    String outputFilePath=null;
                    if(audio!=null){
                        outputFilePath=Variables.outputfile;
                    }else {
                        outputFilePath=Variables.outputfile2;
                    }

                    FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
                    out.writeContainer(fos.getChannel());
                    fos.close();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();

                            if(audio!=null)
                                Merge_withAudio();
                            else {
                                Go_To_preview_Activity();
                            }

                        }
                    });



                } catch (Exception e) {
                    Log.d(Variables.tag,e.toString());
                    progressDialog.dismiss();

                }
            }
        }).start();



        return true;
    }

    public void Merge_withAudio(){

        String audio_file;
        audio_file = Variables.app_hided_folder +Variables.SelectedAudio;


        Merge_Sound merge_video_audio=new Merge_Sound(GallerySelectedVideo_A.this);
        merge_video_audio.doInBackground(audio_file,Variables.outputfile,Variables.outputfile2,draft_file);

    }
    public void Go_To_preview_Activity() {

        Intent intent = new Intent(GallerySelectedVideo_A.this, PreviewVideoActivity.class);
        startActivity(intent);
    }

}