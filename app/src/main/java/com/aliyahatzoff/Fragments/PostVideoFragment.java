package com.aliyahatzoff.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Activities.VideoPlayActivity;
import com.aliyahatzoff.Adapters.VideoGalleryAdapter;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.VideoGallery;
import com.aliyahatzoff.Models.VideoItem;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostVideoFragment extends Fragment {
    ArrayList<VideoGallery> data_list;
    VideoGalleryAdapter videoGalleryAdapter;
    public static final int PERMISSION_READ = 0;
    RecyclerView recyclerView;
    List<VideoItem> videoItemList;
    private static  String userid = "param1";


    public PostVideoFragment() {
        // Required empty public constructor
    }

    public static PostVideoFragment newInstance(String id) {

        PostVideoFragment fragment = new PostVideoFragment();
        userid=id;
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
        View view = inflater.inflate(R.layout.fragment_post_video, container, false);
        recyclerView = view.findViewById(R.id.galley_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        data_list = new ArrayList<>();


        getAllVideoPath(getContext());


        return view;
    }

    private void getAllVideoPath(Context context) {
        Functions.showLoadingDialog(getContext());
        Common.getHatzoffApi().getuservideolist(userid).enqueue(new Callback<List<VideoItem>>() {
            @Override
            public void onResponse(Call<List<VideoItem>> call, Response<List<VideoItem>> response) {
                try {
                    System.out.println(response.errorBody().string());
                    Functions.cancelLoading();
                } catch (Exception e) {
                }
                if (response.isSuccessful()) {

                    videoItemList = response.body();
                    for (int i = 0; i < videoItemList.size(); ++i) {
                        VideoGallery item = new VideoGallery();
                        item.setVideo_path(getContext().getResources().getString(R.string.cloudfront)+"/"+videoItemList.get(i).getFileurl());
                        item.setId(videoItemList.get(i).getId());
                        item.setUserid(videoItemList.get(i).getUser_id());
                        data_list.add(item);
                    }
                    videoGalleryAdapter = new VideoGalleryAdapter(getContext(), data_list, "network", new VideoGalleryAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int postion, VideoGallery item, View view) {
                            Intent i = new Intent(getContext(), VideoPlayActivity.class);
                            i.putExtra("video_path", item.getVideo_path());
                            startActivity(i);
                        }
                    });
                    recyclerView.setAdapter(videoGalleryAdapter);
                    Functions.cancelLoading();

                }
            }

            @Override
            public void onFailure(Call<List<VideoItem>> call, Throwable t) {
                Functions.cancelLoading();

            }
        });
    }
}