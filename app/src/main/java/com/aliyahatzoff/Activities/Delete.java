package com.aliyahatzoff.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aliyahatzoff.Adapters.DeleteVideoAdapter;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.VideoGallery;
import com.aliyahatzoff.Models.VideoItem;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Sharedhelper;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Delete extends AppCompatActivity {
    RecyclerView recyclerView;
    List<VideoItem> videoItemList;
    ArrayList<VideoGallery> data_list;
    DeleteVideoAdapter videoGalleryAdapter;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(Delete.this));
        shimmerFrameLayout=findViewById(R.id.shimmer);
        data_list = new ArrayList<>();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        getAllVideoPath();


    }
    private void getAllVideoPath() {
//        Functions.showLoadingDialog(Delete.this);
        Common.getHatzoffApi().getuservideolist(Sharedhelper.getKey(Delete.this,"loginid").toString()).enqueue(new Callback<List<VideoItem>>() {
            @Override
            public void onResponse(Call<List<VideoItem>> call, Response<List<VideoItem>> response) {
                try {
                    System.out.println(response.errorBody().string());
                //    Functions.cancelLoading();
                } catch (Exception e) {
                }
                if (response.isSuccessful()) {

                    videoItemList = response.body();
                    for (int i = 0; i < videoItemList.size(); ++i) {
                        VideoGallery item = new VideoGallery();
                        item.setVideo_path(getResources().getString(R.string.cloudfront)+"/"+videoItemList.get(i).getFileurl());
                        item.setId(videoItemList.get(i).getId());
                        data_list.add(item);
                    }
                    videoGalleryAdapter = new DeleteVideoAdapter(Delete.this, data_list, "network", new DeleteVideoAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int postion, VideoGallery item, View view) {
                            Intent i = new Intent(Delete.this, VideoPlayActivity.class);
                            i.putExtra("video_path", item.getVideo_path());
                            startActivity(i);
                        }
                    });
                    recyclerView.setAdapter(videoGalleryAdapter);
                   // Functions.cancelLoading();
                    shimmerFrameLayout.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<List<VideoItem>> call, Throwable t) {
                Functions.cancelLoading();

            }
        });
    }
}