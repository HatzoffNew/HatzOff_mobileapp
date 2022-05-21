package com.aliyahatzoff.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Adapters.SoundAdapter;
import com.aliyahatzoff.Models.Soundlist;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.PlayerUtil;
import com.aliyahatzoff.utils.Sharedhelper;
import com.aliyahatzoff.utils.Variables;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.PlaybackException;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocalMusic extends Fragment implements Player.Listener {
    String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    List<Soundlist> soundlists=new ArrayList<Soundlist>();
    List<String> lists=new ArrayList<>();
    String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
    };
    RecyclerView recyclerView;
    SoundAdapter soundAdapter;
    URL objectURL;
    DownloadRequest prDownloader;
    Context context;
    TextView txt_allsongs, txt_title;
    public static String running_sound_id;
    public LocalMusic(){
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.localmusic, container, false);
        context=getContext();
        recyclerView=view.findViewById(R.id.rv_music);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //========================================================
        Cursor audioCursor =getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        if(audioCursor != null){
            if(audioCursor.moveToFirst()){
                do{
                    int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    Soundlist sounditem=new Soundlist();
                    sounditem.setId(audioIndex);
                    sounditem.setFileurl(audioCursor.getString(3));
                    sounditem.setSoundname(audioCursor.getString(4));
                    soundlists.add(sounditem);
                }while(audioCursor.moveToNext());
            }
        }
        audioCursor.close();

        //========================================================


//        Cursor cursor = getActivity().managedQuery(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                projection,
//                selection,
//                null,
//                null);
////        soundlists.clear();
//        while(cursor.moveToNext()){
//            if(cursor.getString(4).endsWith(".aac")) {
//                Soundlist sounditem=new Soundlist();
//            sounditem.setId(Integer.valueOf(cursor.getString(0)));
//            sounditem.setFileurl(cursor.getString(3));
//            sounditem.setSoundname(cursor.getString(4));
//            soundlists.add(sounditem);
//            }
//        }
      //  SoundAdapter soundAdapter = new SoundAdapter(getContext(), soundlists);

            SoundAdapter soundAdapter = new SoundAdapter(getContext(), soundlists, new SoundAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Soundlist item) {
                if (view.getId() == R.id.play) {
                    if (thread != null && !thread.isAlive()) {
                        StopPlaying();
                        playaudio(view, item);
                    } else if (thread == null) {
                        StopPlaying();
                        playaudio(view, item);
                    }
                } else {
                    StopPlaying();
                    File a_file = new File(Uri.parse(item.getFileurl()).getPath());
                    try {
                        Functions.copyFile(a_file,
                                new File(Variables.app_hided_folder+ Variables.SelectedAudio),getContext());
                        Intent output = new Intent();
                        output.putExtra("isSelected", "yes");
                        output.putExtra("sound_name", item.getSoundname());
                        output.putExtra("sound_id", item.getId());
                        Sharedhelper.putKey(context, "soundid", String.valueOf(item.getId()));
                        getActivity().setResult(Activity.RESULT_OK, output);
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        recyclerView.setAdapter(soundAdapter);
        return  view;
    }


    @Override
    public void onTimelineChanged(Timeline timeline,  int reason) {

    }
    View previous_view;
    Thread thread;
    SimpleExoPlayer player;
    String previous_url = "none";

    public void playaudio(View view, final Soundlist item) {
        previous_view = view;
       // System.out.println("url"+item.getFileurl());
        if (previous_url.equals(item.getFileurl())) {

            previous_url = "none";
            running_sound_id = "none";
        } else {
            previous_url = item.getFileurl();
            running_sound_id = String.valueOf(item.getId());

            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            player =  new SimpleExoPlayer.Builder( context, PlayerUtil.renderersFactory(context))
              .setMediaSourceFactory(PlayerUtil.createMediaSourceFactory(context))
              .setTrackSelector(trackSelector)
              .build();

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "Hatzoff"));

            /*MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(item.getFileurl()));*/

            player.setMediaItem(PlayerUtil.getMediaItem(Uri.parse(item.getFileurl())));
            player.prepare();
            player.addListener(this);
            player.setPlayWhenReady(true);
        }

    }


    public void StopPlaying() {

        if (player != null) {
            player.setPlayWhenReady(false);
            player.removeListener(this);
            player.release();
        }

        show_Stop_state();

    }

    public void show_Stop_state() {

        if (previous_view != null) {
            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            ImageView imgBell= (ImageView) previous_view.findViewById(R.id.play);
            imgBell.setImageResource(R.drawable.avd_play_to_pause);
            imgBell.startAnimation(shake);
//            ((ImageView) previous_view.findViewById(R.id.play)).setImageResource(R.drawable.avd_play_to_pause);
        }
        running_sound_id = "none";
    }
    public void Show_loading_state() {
        Functions.showLoadingDialog(context);
    }

    public void Show_Run_State() {
        Functions.cancelLoading();
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        ImageView imgBell= (ImageView) previous_view.findViewById(R.id.play);
        imgBell.setImageResource(R.drawable.avd_pause_to_play);
        imgBell.startAnimation(shake);
      //  ((ImageView) previous_view.findViewById(R.id.play)).setImageResource(R.drawable.avd_pause_to_play);
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            Show_loading_state();
        } else if (playbackState == Player.STATE_READY) {
            Show_Run_State();
        } else if (playbackState == Player.STATE_ENDED) {
            show_Stop_state();
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

}