package com.aliyahatzoff.Fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.aliyahatzoff.Activities.LandingActivity;
import com.aliyahatzoff.Activities.PreComptitionActivity;
import com.aliyahatzoff.Activities.SearchActivity;
import com.aliyahatzoff.Adapters.VideoAdapter;
import com.aliyahatzoff.Api.ApiClient;
import com.aliyahatzoff.Api.ApiInterface;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.VideoItem;
import com.aliyahatzoff.Models.VideoPage;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.MyApplication;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.aliyahatzoff.utils.PlayerUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreenFragment extends Fragment implements Player.Listener {
    int currentPage = -1;
    List<VideoItem> videoItemList;
    LinearLayoutManager layoutManager;
    SimpleExoPlayer privious_player;
    RecyclerView recyclerView;
    boolean is_visible_to_user;
    VideoAdapter videoAdapter;
    String macaddress, secret, access, bucket;
    AmazonS3Client s3Client;
    GeneratePresignedUrlRequest request;
    URL objectURL;
    ImageView comptition;
    VideoPage videoPage;
    MediaPlayer mp;
    PlaybackParameters param;

    public HomeScreenFragment() {
    }

    public static HomeScreenFragment newInstance() {
        HomeScreenFragment fragment = new HomeScreenFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        comptition = view.findViewById(R.id.comptition);
        // int resID=getResources().getIdentifier("click.wav", "raw", getContext().getPackageName());
        mp = MediaPlayer.create(getContext(), R.raw.click);

        comptition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PreComptitionActivity.class));
            }
        });
        ImageView share = view.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
        recyclerView = view.findViewById(R.id.viewpager);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(false);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        // WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // WifiInfo wInfo = wifiManager.getConnectionInfo();
        // macaddress = wInfo.getMacAddress();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    loadmore();
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final int scrollOffset = recyclerView.computeVerticalScrollOffset();
                final int height = recyclerView.getHeight();
                int page_no = scrollOffset / height;
                if (page_no != currentPage) {
                    currentPage = page_no;
                    Release_Privious_Player();
                    Set_Player(currentPage);
                }
            }
        });
        ((LandingActivity) getActivity()).centerheart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Functions.showLoadingDialog(getContext());
                Common.getHatzoffApi().likevideo(String.valueOf(videoItemList.get(currentPage).getId()))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Functions.cancelLoading();
                                try {
                                    //     System.out.println(response.errorBody().string());
                                } catch (Exception e) {
                                }
                                if (response.isSuccessful()) {
                                    String islike = "0";
                                    if (response.code() == 200) {
                                        try {
                                            islike = response.body().string();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        if (islike.equals("1")) {
                                            videoItemList.get(currentPage).setIslike("Y");
                                            liked();
                                        } else {
                                            videoItemList.get(currentPage).setIslike("N");
                                            unliked();
                                        }

                                    }

                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Functions.cancelLoading();
                            }
                        });

            }
        });
        //secret = getContext().getResources().getString(R.string.s3_secret);
        //access = getContext().getResources().getString(R.string.s3_access_key);
        //bucket = getContext().getResources().getString(R.string.s3_bucket);
        //AmazonS3 s3Client = new AmazonS3Client(new BasicSessionCredentials(access, secret, sessionToken));
        //BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
        //s3Client = new AmazonS3Client(credentials);
        //s3Client.setRegion(Region.getRegion("ap-south-1"));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getVideolist();

    }

    void getVideolist() {
        Functions.showLoadingDialog(getContext());
        Common.getHatzoffApi().getvideolist2(1).enqueue(new Callback<VideoPage>() {
            @Override
            public void onResponse(Call<VideoPage> call, Response<VideoPage> response) {
                try {
                    System.out.println(response.errorBody().string());
                    Functions.cancelLoading();

                } catch (Exception e) {
                }
                if (response.isSuccessful()) {
                    Functions.cancelLoading();
                    videoPage = response.body();
                    videoItemList = videoPage.getVideoItems();
                    videoAdapter = new VideoAdapter(videoItemList, getContext(),  getActivity(), new VideoAdapter.Pos() {
                        @Override
                        public void Choose(int position) {
                        }
                    });
                    recyclerView.setAdapter(videoAdapter);

                }
            }

            @Override
            public void onFailure(Call<VideoPage> call, Throwable t) {
                Functions.cancelLoading();

            }
        });
    }

    void loadmore() {
        if (videoPage.getCurrent_page() < videoPage.getLast_page()) {
            Functions.showLoadingDialog(getContext());

            Common.getHatzoffApi().getvideolist2(videoPage.getCurrent_page() + 1).enqueue(new Callback<VideoPage>() {
                @Override
                public void onResponse(Call<VideoPage> call, Response<VideoPage> response) {
                    try {
                        //        System.out.println(response.errorBody().string());
                        Functions.cancelLoading();

                    } catch (Exception e) {
                    }
                    if (response.isSuccessful()) {
                        Functions.cancelLoading();
                        videoPage = response.body();
                        videoItemList = videoPage.getVideoItems();
                        videoAdapter.addAll(videoItemList);
                        videoItemList = videoAdapter.getall();
                        /*Collections.reverse(videoItemList);
                        videoAdapter = new VideoAdapter(videoItemList, getContext(), getChildFragmentManager(), getActivity(), new VideoAdapter.Pos() {
                            @Override
                            public void Choose(int position) {
                            }
                        });
                        recyclerView.setAdapter(videoAdapter);
*/
                    }
                }

                @Override
                public void onFailure(Call<VideoPage> call, Throwable t) {
                    Functions.cancelLoading();

                }
            });
        }
    }

    public void Release_Privious_Player() {
        if (privious_player != null) {
            privious_player.removeListener(this);
            privious_player.release();
        }
    }
    SimpleExoPlayer player;

    public void Set_Player(final int currentPage) {

        final VideoItem item = videoItemList.get(currentPage);
        if (item.getIslike().equals("Y")) {
            liked();
        } else {
            unliked();
        }

            MyApplication.getProxy(getContext());

        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, 16))
                .setBufferDurationsMs(1 * 1024, 1 * 1024, 500, 1024)
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true)
                .createDefaultLoadControl();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(getContext());

        player = new SimpleExoPlayer.Builder(getContext()).
                setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .build();
        SimpleCache simpleCache = MyApplication.simpleCache;
       /* CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), getContext().getString(R.string.app_name)))
                , CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

        ProgressiveMediaSource videoSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(Uri.parse(getContext().getResources().getString(R.string.cloudfront) + "/" + item.getFileurl()));
*/
            /*DefaultTrackSelector trackSelector = new DefaultTrackSelector();

             player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
            //DataSource.Factory dataSourceFactory = new CacheDataSourceFactory(VideoCacheSingleton.getInstance(),getContext(),
                    Util.getUserAgent(getContext(), "HatzOff"));

                    MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(MyApplication.getProxy(getContext()).getProxyUrl(getContext().getResources().getString(R.string.cloudfront) + "/" + item.getFileurl())));
*/
        player.setMediaItem(PlayerUtil.getMediaItem(Uri.parse(getContext().getResources().getString(R.string.cloudfront) + "/" + item.getFileurl())));
            player.prepare();
            if (!(item.getSpeed() == null)) {
                param = new PlaybackParameters(Float.parseFloat(item.getSpeed()));
            } else {
                param = new PlaybackParameters(Float.parseFloat("1f"));
            }
            player.setPlaybackParameters(param);

            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.addListener(this);

            View layout = layoutManager.findViewByPosition(currentPage);
            final PlayerView playerView = layout.findViewById(R.id.videoview);
            playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING);
            playerView.setUseArtwork(true);
            playerView.setPlayer(player);

            player.setPlayWhenReady(true);
            privious_player = player;
        ApiClient.getRetrofit().create(ApiInterface.class).viewvideo(String.valueOf(item.getId()), "abc").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Functions.cancelLoading();
                    System.out.println(response.errorBody().string());
                } catch (Exception e) {
                }
                if (response.isSuccessful()) {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //   Functions.cancelLoading();
                //        System.out.println(t.getMessage());


            }
        });


    }

    public boolean is_fragment_exits() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        is_visible_to_user = isVisibleToUser;
        if (privious_player != null && isVisibleToUser) {
            privious_player.setPlayWhenReady(true);
        } else if (privious_player != null && !isVisibleToUser) {
            privious_player.setPlayWhenReady(false);
        }
    }

    // this is lifecyle of the Activity which is importent for play,pause video or relaese the player
    @Override
    public void onResume() {
        super.onResume();
        if ((privious_player != null && is_visible_to_user) && !is_fragment_exits()) {
            privious_player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (privious_player != null) {
            privious_player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (privious_player != null) {
            privious_player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (privious_player != null) {
            privious_player.release();
        }
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

    public void liked() {
          ((LandingActivity) getActivity()).centerheart.setImageDrawable (getContext().getResources().getDrawable(getContext().getResources().getIdentifier("@drawable/roundheart",null,getContext().getPackageName())));
    }

    public void unliked() {
        ((LandingActivity) getActivity()).centerheart.setImageDrawable (getContext().getResources().getDrawable(getContext().getResources().getIdentifier("@drawable/roundwhiteheart",null,getContext().getPackageName())));


    }

}