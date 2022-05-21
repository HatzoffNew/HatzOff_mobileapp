package com.aliyahatzoff.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Adapters.SoundAdapter;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.Soundlist;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.PlayerUtil;
import com.aliyahatzoff.utils.Sharedhelper;
import com.aliyahatzoff.utils.Variables;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
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

import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class DiscoverMusic extends Fragment implements Player.Listener, View.OnClickListener {
    RecyclerView recyclerView;
    SoundAdapter soundAdapter;
    AmazonS3Client s3Client;
    GeneratePresignedUrlRequest request;
    URL objectURL;
    String secret, access, bucket;
    DownloadRequest prDownloader;
    Context context;
    TextView txt_allsongs, txt_title;
    public static String running_sound_id;
    LinearLayout bollywood, hollywood, dev, comedy, bhojpuri, pop, background, punjabi;
    Animation shake,shake1;
    public DiscoverMusic() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.music_category, container, false);
        recyclerView = view.findViewById(R.id.rv_music);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        txt_allsongs = view.findViewById(R.id.allsongs);
        txt_title = view.findViewById(R.id.title);

        bollywood=view.findViewById(R.id.lout_bollywood);
        hollywood=view.findViewById(R.id.lout_hollywood);
        dev=view.findViewById(R.id.lout_dev);
        comedy=view.findViewById(R.id.lout_comedy);

        bhojpuri=view.findViewById(R.id.lout_bhojpuri);
        pop=view.findViewById(R.id.lout_pop);
        background=view.findViewById(R.id.lout_background);
        punjabi=view.findViewById(R.id.lout_punjabi);

        txt_allsongs.setOnClickListener(this);
        bollywood.setOnClickListener(this);
        hollywood.setOnClickListener(this);
        dev.setOnClickListener(this);
        comedy.setOnClickListener(this);

        bhojpuri.setOnClickListener(this);
        pop.setOnClickListener(this);
        background.setOnClickListener(this);
        punjabi.setOnClickListener(this);



        running_sound_id = "none";
        context = getContext();
        /*secret = context.getResources().getString(R.string.s3_secret);
        access = context.getResources().getString(R.string.s3_access_key);
        bucket = context.getResources().getString(R.string.s3_bucket);
        //AmazonS3 s3Client = new AmazonS3Client(new BasicSessionCredentials(access, secret, sessionToken));
        BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
        s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion("ap-south-1"));*/
        getMusic("All");
        return view;
    }
    private void getMusic(String mtype) {
        Functions.showLoadingDialog(getContext());
        Common.getHatzoffApi().getsoundlist(mtype).enqueue(new Callback<List<Soundlist>>() {
            @Override
            public void onResponse(Call<List<Soundlist>> call, Response<List<Soundlist>> response) {
                try {
                    System.out.println(response.errorBody().string());
                } catch (Exception e) {
                }
                if (response.isSuccessful()) {
                    Functions.cancelLoading();
                //    soundAdapter = new SoundAdapter(getContext(), response.body());
                    soundAdapter = new SoundAdapter(getContext(), response.body(), new SoundAdapter.OnItemClickListener() {
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
                                Down_load_mp3(String.valueOf(item.getId()), item.getSoundname(), item.getFileurl());
                            }

                        }
                    });

                    recyclerView.setAdapter(soundAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Soundlist>> call, Throwable t) {
            }
        });
    }

    View previous_view;
    Thread thread;
    SimpleExoPlayer player;
    String previous_url = "none";

    public void playaudio(View view, final Soundlist item) {
        previous_view = view;
        if (previous_url.equals(item.getFileurl())) {

            previous_url = "none";
            running_sound_id = "none";
        } else {
            previous_url = item.getFileurl();
            running_sound_id = String.valueOf(item.getId());
            //request = new GeneratePresignedUrlRequest(bucket, item.getFileurl());
            //objectURL = s3Client.generatePresignedUrl(request);

            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            player =  new SimpleExoPlayer.Builder( context, PlayerUtil.renderersFactory(context))
              .setMediaSourceFactory(PlayerUtil.createMediaSourceFactory(context))
              .setTrackSelector(trackSelector)
              .build();

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "HATZOFF"));

           /* MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(getContext().getString(R.string.cloudfront)+"/"+item.getFileurl()));*/
            player.setMediaItem(PlayerUtil.getMediaItem(Uri.parse(getContext().getResources().getString(R.string.cloudfront) + "/" + item.getFileurl())));
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
             shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
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
         shake1 = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        ImageView imgBell= (ImageView) previous_view.findViewById(R.id.play);
        imgBell.setImageResource(R.drawable.avd_pause_to_play);
        imgBell.startAnimation(shake1);
//        ((ImageView) previous_view.findViewById(R.id.play)).setImageResource(R.drawable.avd_pause_to_play);
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
        if (playbackState == Player.STATE_BUFFERING) {
            System.out.println("buffering");
            Show_loading_state();
        } else if (playbackState == Player.STATE_READY) {
            System.out.println("ru");

            Show_Run_State();
        } else if (playbackState == Player.STATE_ENDED) {
            System.out.println("end");

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

    ProgressDialog progressDialog;

    public void Down_load_mp3(final String id, final String sound_name, String url) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //request = new GeneratePresignedUrlRequest(bucket, url);
        //objectURL = s3Client.generatePresignedUrl(request);
        //url = objectURL.toString();

        prDownloader = PRDownloader.download(getContext().getString(R.string.cloudfront)+"/"+url, Variables.app_hided_folder, Variables.SelectedAudio)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                    }
                });

        prDownloader.start(new OnDownloadListener() {

            @Override
            public void onDownloadComplete() {
                progressDialog.dismiss();
                Intent output = new Intent();
                output.putExtra("isSelected", "yes");
                output.putExtra("sound_name", sound_name);
                output.putExtra("sound_id", id);
                Sharedhelper.putKey(context, "soundid", String.valueOf(id));
                getActivity().setResult(Activity.RESULT_OK, output);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

            }

            @Override
            public void onError(Error error) {
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lout_bollywood:
                getMusic("Bollywood");
                txt_title.setText("Bollywood");
                selectcategory(0);
                break;
            case R.id.lout_hollywood:
                getMusic("Hollywood");
                txt_title.setText("Hollywood");
                selectcategory(1);
                break;
            case R.id.lout_dev:
                getMusic("Devotional");
                hollywood.setBackgroundColor(Color.GRAY);
                txt_title.setText("Devotional");
                selectcategory(2);
                break;
            case R.id.lout_comedy:
                getMusic("Comedy");
                txt_title.setText("Comedy");
                selectcategory(3);
                break;
            case R.id.lout_bhojpuri:
                getMusic("Bhojpuri");
                txt_title.setText("Bhojpuri");
                selectcategory(4);
                break;
            case R.id.lout_pop:
                getMusic("Pop");
                txt_title.setText("Pop");
                selectcategory(5);
                break;
            case R.id.lout_background:
                getMusic("Background");
                txt_title.setText("Background");
                selectcategory(6);
                break;
            case R.id.lout_punjabi:
                getMusic("Punjabi");
                txt_title.setText("Punjabi");
                selectcategory(7);
                break;
            case R.id.allsongs:
                getMusic("All");
                txt_title.setText("All");
                selectcategory(8);
                break;


        }
    }
    void selectcategory(int index)
    {
        if(index==0)
        {
            bollywood.setBackgroundColor(Color.GRAY);
            hollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            dev.setBackgroundColor(getResources().getColor(R.color.loutbg));
            comedy.setBackgroundColor(getResources().getColor(R.color.loutbg));
            bhojpuri.setBackgroundColor(getResources().getColor(R.color.loutbg));
            pop.setBackgroundColor(getResources().getColor(R.color.loutbg));
            background.setBackgroundColor(getResources().getColor(R.color.loutbg));
            punjabi.setBackgroundColor(getResources().getColor(R.color.loutbg));
        }
        else if(index==1)
        {
            bollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            hollywood.setBackgroundColor(Color.GRAY);
            dev.setBackgroundColor(getResources().getColor(R.color.loutbg));
            comedy.setBackgroundColor(getResources().getColor(R.color.loutbg));
            bhojpuri.setBackgroundColor(getResources().getColor(R.color.loutbg));
            pop.setBackgroundColor(getResources().getColor(R.color.loutbg));
            background.setBackgroundColor(getResources().getColor(R.color.loutbg));
            punjabi.setBackgroundColor(getResources().getColor(R.color.loutbg));
        }
        else if(index==2)
        {
            bollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            hollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            dev.setBackgroundColor(Color.GRAY);
            comedy.setBackgroundColor(getResources().getColor(R.color.loutbg));
            bhojpuri.setBackgroundColor(getResources().getColor(R.color.loutbg));
            pop.setBackgroundColor(getResources().getColor(R.color.loutbg));
            background.setBackgroundColor(getResources().getColor(R.color.loutbg));
            punjabi.setBackgroundColor(getResources().getColor(R.color.loutbg));
        }
        else if(index==3)
        {
            bollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            hollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            dev.setBackgroundColor(getResources().getColor(R.color.loutbg));
            comedy.setBackgroundColor(Color.GRAY);
            bhojpuri.setBackgroundColor(getResources().getColor(R.color.loutbg));
            pop.setBackgroundColor(getResources().getColor(R.color.loutbg));
            background.setBackgroundColor(getResources().getColor(R.color.loutbg));
            punjabi.setBackgroundColor(getResources().getColor(R.color.loutbg));
        }
        else if(index==4)
        {
            bollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            hollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            dev.setBackgroundColor(getResources().getColor(R.color.loutbg));
            comedy.setBackgroundColor(getResources().getColor(R.color.loutbg));
            bhojpuri.setBackgroundColor(Color.GRAY);
            pop.setBackgroundColor(getResources().getColor(R.color.loutbg));
            background.setBackgroundColor(getResources().getColor(R.color.loutbg));
            punjabi.setBackgroundColor(getResources().getColor(R.color.loutbg));
        }
        else if(index==5)
        {
            bollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            hollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            dev.setBackgroundColor(getResources().getColor(R.color.loutbg));
            comedy.setBackgroundColor(getResources().getColor(R.color.loutbg));
            bhojpuri.setBackgroundColor(getResources().getColor(R.color.loutbg));
            pop.setBackgroundColor(Color.GRAY);
            background.setBackgroundColor(getResources().getColor(R.color.loutbg));
            punjabi.setBackgroundColor(getResources().getColor(R.color.loutbg));
        }
        else if(index==6)
        {
            bollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            hollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            dev.setBackgroundColor(getResources().getColor(R.color.loutbg));
            comedy.setBackgroundColor(getResources().getColor(R.color.loutbg));
            bhojpuri.setBackgroundColor(getResources().getColor(R.color.loutbg));
            pop.setBackgroundColor(getResources().getColor(R.color.loutbg));
            background.setBackgroundColor(Color.GRAY);
            punjabi.setBackgroundColor(getResources().getColor(R.color.loutbg));
        }
        else if(index==7)
        {
            bollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            hollywood.setBackgroundColor(getResources().getColor(R.color.loutbg));
            dev.setBackgroundColor(getResources().getColor(R.color.loutbg));
            comedy.setBackgroundColor(getResources().getColor(R.color.loutbg));
            bhojpuri.setBackgroundColor(getResources().getColor(R.color.loutbg));
            pop.setBackgroundColor(getResources().getColor(R.color.loutbg));
            background.setBackgroundColor(getResources().getColor(R.color.loutbg));
            punjabi.setBackgroundColor(Color.GRAY);
        }

    }
}