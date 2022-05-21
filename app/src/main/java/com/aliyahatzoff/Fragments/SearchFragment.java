package com.aliyahatzoff.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Activities.DiscoverPreviewActivity;
import com.aliyahatzoff.Adapters.AllDiscoverVideoAdapter;
import com.aliyahatzoff.Adapters.DiscoverAdapter;
import com.aliyahatzoff.Adapters.RecentVideoAdapter;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.VideoGallery;
import com.aliyahatzoff.Models.VideoItem;
import com.aliyahatzoff.Models.VideoPage;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment implements View.OnClickListener {
    RecyclerView all_videos, trending_videos, recent_videos;
    List<VideoItem> discoverModelList = new ArrayList<>();
    List<VideoItem> recentModelList = new ArrayList<>();
    List<VideoItem> allModelList = new ArrayList<>();
    VideoPage videoPage,vptrending,vprecent;

    AmazonS3Client s3Client;
    GeneratePresignedUrlRequest request;
    String secret, access, bucket;
    URL objectURL;
    DiscoverAdapter discoverAdapter;
    RecentVideoAdapter recentVideoAdapter;
    AllDiscoverVideoAdapter allDiscoverVideoAdapter,trendingAdapter,recentAdapter;
    ArrayList<VideoGallery> data_list = new ArrayList<>();
    ArrayList<VideoGallery> data_list_recent = new ArrayList<>();
    ArrayList<VideoGallery> data_list_all = new ArrayList<>();
    Button recent_btn, all_btn, trending_all;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        trending_all = view.findViewById(R.id.trending_all);
        recent_btn = view.findViewById(R.id.recent_btn);
        all_btn = view.findViewById(R.id.all_btn);
        all_btn.setOnClickListener(this::onClick);
        recent_btn.setOnClickListener(this::onClick);
        trending_all.setOnClickListener(this::onClick);
        /*secret = getResources().getString(R.string.s3_secret);
        access = getResources().getString(R.string.s3_access_key);
        bucket = getResources().getString(R.string.s3_bucket);
        BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
        s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion("ap-south-1"));*/
        all_videos = view.findViewById(R.id.all_videos);
        all_videos.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        trending_videos = view.findViewById(R.id.trending);
        trending_videos.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        recent_videos = view.findViewById(R.id.recent);
        recent_videos.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        video_recent(view);
        video_trending(view);
        allVideos(view);
        return view;
    }

    void allVideos(View view) {
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
                    videoPage = response.body();
                    allModelList = videoPage.getVideoItems();
                    //System.out.println("p="+videoPage.getCurrent_page()+"-"+videoPage.getLast_page()+"-"+videoPage.getVideoItems().size());
                    for (int i = 0; i < allModelList.size(); ++i) {
                      //  request = new GeneratePresignedUrlRequest(bucket, allModelList.get(i).getFileurl());
                       // objectURL = s3Client.generatePresignedUrl(request);
                        VideoGallery item = new VideoGallery();
                        item.setVideo_path(getContext().getResources().getString(R.string.cloudfront)+"/"+allModelList.get(i).getFileurl());
                        long file_duration = 0;
                        data_list_all.add(item);
                    }
                    allDiscoverVideoAdapter = new AllDiscoverVideoAdapter(getContext(), data_list_all, "network", new AllDiscoverVideoAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int postion, VideoGallery item, View view) {

                        }
                    });
                    all_videos.setAdapter(allDiscoverVideoAdapter);
                    Functions.cancelLoading();

                }
            }

            @Override
            public void onFailure(Call<VideoPage> call, Throwable t) {
                Functions.cancelLoading();

            }
        });
    }

    void video_trending(View view) {
        Functions.showLoadingDialog(getContext());
        Common.getHatzoffApi().get_trending(1).enqueue(new Callback<VideoPage>() {
            @Override
            public void onResponse(Call<VideoPage> call, Response<VideoPage> response) {
                try {
                    System.out.println(response.errorBody().string());
                    Functions.cancelLoading();

                } catch (Exception e) {
                }
                if (response.isSuccessful()) {
                    vptrending = response.body();
                    discoverModelList= vptrending.getVideoItems();
                    int size=0;
                    if(discoverModelList.size()>6)
                        size=6;
                    else
                        size=discoverModelList.size();
                    //System.out.println("p="+videoPage.getCurrent_page()+"-"+videoPage.getLast_page()+"-"+videoPage.getVideoItems().size());
                   data_list_all.clear();
                    for (int i = 0; i < size; ++i) {
                       // request = new GeneratePresignedUrlRequest(bucket, discoverModelList.get(i).getFileurl());
                       // objectURL = s3Client.generatePresignedUrl(request);
                        VideoGallery item = new VideoGallery();
                        item.setVideo_path(getContext().getResources().getString(R.string.cloudfront)+"/"+discoverModelList.get(i).getFileurl());
                        long file_duration = 0;
                        data_list_all.add(item);
                    }
                    trendingAdapter = new AllDiscoverVideoAdapter(getContext(), data_list_all, "network", new AllDiscoverVideoAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int postion, VideoGallery item, View view) {

                        }
                    });
                    trending_videos.setAdapter(trendingAdapter);
                    Functions.cancelLoading();

                }
            }

            @Override
            public void onFailure(Call<VideoPage> call, Throwable t) {
                Functions.cancelLoading();

            }
        });
    }
    void video_recent(View view) {
        Functions.showLoadingDialog(getContext());
        Common.getHatzoffApi().get_recent(1).enqueue(new Callback<VideoPage>() {
            @Override
            public void onResponse(Call<VideoPage> call, Response<VideoPage> response) {
                try {
                    System.out.println(response.errorBody().string());
                    Functions.cancelLoading();

                } catch (Exception e) {
                }
                if (response.isSuccessful()) {
                    vprecent = response.body();
                    recentModelList = vprecent.getVideoItems();
                    int size=0;
                    if(recentModelList.size()>6)
                        size=6;
                    else
                        size=recentModelList.size();
                    data_list_all.clear();
                    //System.out.println("p="+videoPage.getCurrent_page()+"-"+videoPage.getLast_page()+"-"+videoPage.getVideoItems().size());
                    for (int i = 0; i < size; ++i) {
                       // request = new GeneratePresignedUrlRequest(bucket, recentModelList.get(i).getFileurl());
                       // objectURL = s3Client.generatePresignedUrl(request);
                        VideoGallery item = new VideoGallery();
                        item.setVideo_path(getContext().getResources().getString(R.string.cloudfront)+"/"+recentModelList.get(i).getFileurl());
                        long file_duration = 0;
                        data_list_all.add(item);
                    }
                    recentAdapter = new AllDiscoverVideoAdapter(getContext(), data_list_all, "network", new AllDiscoverVideoAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int postion, VideoGallery item, View view) {

                        }
                    });
                    recent_videos.setAdapter(recentAdapter);
                    Functions.cancelLoading();

                }
            }

            @Override
            public void onFailure(Call<VideoPage> call, Throwable t) {
                Functions.cancelLoading();

            }
        });
    }

/*
    private void recentVideos(View view) {
        recent_videos = view.findViewById(R.id.recent);
        recent_videos.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        Functions.showLoadingDialog(getContext());
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
                    for (int i = 0; i < recentModelList.size(); ++i) {
                        request = new GeneratePresignedUrlRequest(bucket, recentModelList.get(i).getFileurl());
                        objectURL = s3Client.generatePresignedUrl(request);
                        VideoGallery item = new VideoGallery();
                        item.setVideo_path(objectURL.toString());
                        long file_duration = 0;

                        data_list_recent.add(item);
                    }
                    recentVideoAdapter = new RecentVideoAdapter(getContext(), data_list_recent, "network", new RecentVideoAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int postion, VideoGallery item, View view) {

                        }
                    });
                    recent_videos.setAdapter(recentVideoAdapter);
                    Functions.cancelLoading();
                }
            }

            @Override
            public void onFailure(Call<List<RecentModel>> call, Throwable t) {
                Functions.cancelLoading();

            }
        });

    }

    private void trending(View view) {
        trending_videos = view.findViewById(R.id.trending);
        trending_videos.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        Functions.showLoadingDialog(getContext());
        Common.getHatzoffApi().trendingList().enqueue(new Callback<List<DiscoverModel>>() {
            @Override
            public void onResponse(Call<List<DiscoverModel>> call, Response<List<DiscoverModel>> response) {
                try {
                    System.out.println(response.errorBody().string());
                    Functions.cancelLoading();
                } catch (Exception e) {
                }
                if (response.isSuccessful()) {
                    discoverModelList = response.body();
                    for (int i = 0; i < discoverModelList.size(); ++i) {
                        request = new GeneratePresignedUrlRequest(bucket, discoverModelList.get(i).getFileurl());
                        objectURL = s3Client.generatePresignedUrl(request);
                        VideoGallery item = new VideoGallery();
                        item.setVideo_path(objectURL.toString());
                        long file_duration = 0;

                        data_list.add(item);
                    }
                    discoverAdapter = new DiscoverAdapter(getContext(), data_list, "network", new DiscoverAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int postion, VideoGallery item, View view) {
                            Intent i = new Intent(getActivity(), VideoPlayActivity.class);
                            i.putExtra("video_path", item.getVideo_path());
                            startActivity(i);
                        }
                    });
                    trending_videos.setAdapter(discoverAdapter);
                    Functions.cancelLoading();
                }
            }

            @Override
            public void onFailure(Call<List<DiscoverModel>> call, Throwable t) {
                Functions.cancelLoading();

            }
        });
    }
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trending_all:
                Intent intent = new Intent(getActivity(), DiscoverPreviewActivity.class);
                intent.putExtra("type", "trending");
                startActivity(intent);
                break;

            case R.id.recent_btn:
                Intent intent1 = new Intent(getActivity(), DiscoverPreviewActivity.class);
                intent1.putExtra("type", "recent");
                startActivity(intent1);
                break;

            case R.id.all_btn:
                Intent intent2 = new Intent(getActivity(), DiscoverPreviewActivity.class);
                intent2.putExtra("type", "allvideo");
                startActivity(intent2);
                break;
        }
    }
}