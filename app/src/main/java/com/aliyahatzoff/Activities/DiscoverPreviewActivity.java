package com.aliyahatzoff.Activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.exoplayer2.PlaybackException;
import com.aliyahatzoff.Adapters.CommonDiscoverAdapter;
import com.aliyahatzoff.Adapters.VideoAdapter;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.DiscoverModel;
import com.aliyahatzoff.Models.RecentModel;
import com.aliyahatzoff.Models.VideoGal;
import com.aliyahatzoff.Models.VideoItem;
import com.aliyahatzoff.Models.VideoPage;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverPreviewActivity extends AppCompatActivity implements View.OnClickListener , Player.Listener{
    AmazonS3Client s3Client;
    int currentPage = -1;
    VideoPage videoPage;
    List<VideoItem> videoItemList;
    VideoAdapter videoAdapter;
    SimpleExoPlayer privious_player;
    LinearLayoutManager layoutManager;

    GeneratePresignedUrlRequest request;
    String secret,access,bucket;
    URL objectURL;
    TextView title;
    ImageView imageView;
    Context context;
    //trending===========
    RecyclerView recyclerView;
    List<RecentModel> recentModelList=new ArrayList<>();
    List<VideoItem> allModelList=new ArrayList<>();
    List<DiscoverModel> discoverModelList=new ArrayList<>();
    ArrayList<VideoGal> data_list=new ArrayList<>();
    CommonDiscoverAdapter commonDiscoverAdapter;
    //======================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_discover_preview);
        recyclerView=findViewById(R.id.common_rv);
        context=DiscoverPreviewActivity.this;
        imageView=findViewById(R.id.back);
        imageView.setOnClickListener(this::onClick);
        title=findViewById(R.id.title);
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
       // secret = getResources().getString(R.string.s3_secret);
       // access = getResources().getString(R.string.s3_access_key);
        //bucket = getResources().getString(R.string.s3_bucket);
        //BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
        //s3Client = new AmazonS3Client(credentials);
        //s3Client.setRegion(Region.getRegion("ap-south-1"));

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


        if (getIntent().getStringExtra("type").equals("trending")) {
            title.setText("Trending Videos");
           gettrending();

        }else if(getIntent().getStringExtra("type").equals("recent")){
            title.setText("Recent Videos");
           getrecent();
        }else if(getIntent().getStringExtra("type").equals("allvideo")){
            title.setText("All Videos");
            getVideolist();
        }
        else if(getIntent().getStringExtra("type").equals("mostpopular")){
            title.setText("Your Most Popular Video");
            getPopularlist();
        }
    }


    private void getRecentList() {
        Functions.showLoadingDialog(this);
        Common.getHatzoffApi().recentList().enqueue(new Callback<List<RecentModel>>() {
            @Override
            public void onResponse(Call<List<RecentModel>> call, Response<List<RecentModel>> response) {
                try {
                    System.out.println(response.errorBody().string());
                    Functions.cancelLoading();
                } catch (Exception e) {
                }
                if (response.isSuccessful()) {
                    recentModelList = response.body();
                    for(int i=0;i<recentModelList.size();++i)
                    {
                      //  request = new GeneratePresignedUrlRequest(bucket, recentModelList.get(i).getFileurl());
                       // objectURL = s3Client.generatePresignedUrl(request);
                        VideoGal item = new VideoGal();
                        item.setVideo_path(getResources().getString(R.string.cloudfront)+"/"+recentModelList.get(i).getFileurl());
                        item.setVideo_id(recentModelList.get(i).getId());
                        long file_duration =0;
                        data_list.add(item);
                    }
                    commonDiscoverAdapter = new CommonDiscoverAdapter(DiscoverPreviewActivity.this, data_list, "network");
                    recyclerView.setAdapter(commonDiscoverAdapter);
                    Functions.cancelLoading();
                }
            }
            @Override
            public void onFailure(Call<List<RecentModel>> call, Throwable t) {
                Functions.cancelLoading();
            }
        });

    }

    private void getTrendingVideos() {
        Functions.showLoadingDialog(this);
        Common.getHatzoffApi().trendingList().enqueue(new Callback<List<DiscoverModel>>() {
            @Override
            public void onResponse(Call<List<DiscoverModel>> call, Response<List<DiscoverModel>> response) {
                if (response.isSuccessful()){
                    try {
                        System.out.println(response.errorBody().string());
                        Functions.cancelLoading();
                    } catch (Exception e) {
                    }
                    if (response.isSuccessful()) {
                        discoverModelList = response.body();
                        Toast.makeText(DiscoverPreviewActivity.this, String.valueOf(discoverModelList.size()), Toast.LENGTH_SHORT).show();
                        for(int i=0;i<discoverModelList.size();++i)
                        {
                            //request = new GeneratePresignedUrlRequest(bucket, discoverModelList.get(i).getFileurl());
                            //objectURL = s3Client.generatePresignedUrl(request);
                            VideoGal item = new VideoGal();
                            item.setVideo_path(getResources().getString(R.string.cloudfront)+"/"+discoverModelList.get(i).getFileurl());
                            item.setVideo_id(discoverModelList.get(i).getId());
                            long file_duration =    0;
                            data_list.add(item);
                        }
                        commonDiscoverAdapter = new CommonDiscoverAdapter(DiscoverPreviewActivity.this, data_list, "network");
                        recyclerView.setAdapter(commonDiscoverAdapter);
                        Functions.cancelLoading();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<DiscoverModel>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;

        }
    }
    void getVideolist() {
        Functions.showLoadingDialog(DiscoverPreviewActivity.this);
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
                    videoAdapter = new VideoAdapter(videoItemList,context , DiscoverPreviewActivity.this, new VideoAdapter.Pos() {
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

    void gettrending() {
        Functions.showLoadingDialog(DiscoverPreviewActivity.this);
        Common.getHatzoffApi().get_trending(1).enqueue(new Callback<VideoPage>() {
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
                    videoAdapter = new VideoAdapter(videoItemList,context , DiscoverPreviewActivity.this, new VideoAdapter.Pos() {
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
    void getrecent() {
        Functions.showLoadingDialog(DiscoverPreviewActivity.this);
        Common.getHatzoffApi().get_recent(1).enqueue(new Callback<VideoPage>() {
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
                    videoAdapter = new VideoAdapter(videoItemList,context , DiscoverPreviewActivity.this, new VideoAdapter.Pos() {
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

    void getPopularlist() {
        Functions.showLoadingDialog(DiscoverPreviewActivity.this);
        Common.getHatzoffApi().getpopularvideo().enqueue(new Callback<VideoPage>() {
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
                    videoAdapter = new VideoAdapter(videoItemList,context , DiscoverPreviewActivity.this, new VideoAdapter.Pos() {
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
            Functions.showLoadingDialog(context);

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

    public void Set_Player(final int currentPage) {
        final VideoItem item = videoItemList.get(currentPage);

        //request = new GeneratePresignedUrlRequest(bucket, item.getFileurl());
        //objectURL = s3Client.generatePresignedUrl(request);

        //System.out.println(objectURL);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        final SimpleExoPlayer player =  new SimpleExoPlayer.Builder( context,PlayerUtil.renderersFactory(context))
              .setMediaSourceFactory(PlayerUtil.createMediaSourceFactory(context))
              .setTrackSelector(trackSelector)
              .build();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "HatzOff"));
       /* MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(getResources().getString(R.string.cloudfront)+"/"+item.getFileurl()));
*/
        player.setMediaItem(PlayerUtil.getMediaItem(Uri.parse(getResources().getString(R.string.cloudfront)+"/"+item.getFileurl())));
        
        player.prepare();

        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.addListener(this);

        View layout = layoutManager.findViewByPosition(currentPage);
        final PlayerView playerView = layout.findViewById(R.id.videoview);
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING);
        playerView.setUseArtwork(true);
        playerView.setPlayer(player);

        player.setPlayWhenReady(true);
        privious_player = player;


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

    @Override
    protected void onResume() {
        super.onResume();
        if ((privious_player != null )) {
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
}