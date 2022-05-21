package com.aliyahatzoff.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Adapters.VideoAdapter;
import com.aliyahatzoff.Adapters.VideoGalleryAdapter;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.VideoGal;
import com.aliyahatzoff.Models.VideoItem;
import com.aliyahatzoff.Models.VideoPage;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostPopular extends AppCompatActivity {
    ArrayList<VideoGal> data_list;
    VideoGalleryAdapter videoGalleryAdapter;
    public static final int PERMISSION_READ = 0;
    RecyclerView recyclerView;
    VideoPage mpopular;
    List<VideoItem> videoItemList;
    String macaddress,secret,access,bucket;
    AmazonS3Client s3Client;
    GeneratePresignedUrlRequest request;
    URL objectURL;
    ImageView image_back;
    VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_popular);
        recyclerView=findViewById(R.id.popular_videos);
        image_back=findViewById(R.id.image_back);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        data_list = new ArrayList<>();

        secret = getResources().getString(R.string.s3_secret);
        access = getResources().getString(R.string.s3_access_key);
        bucket = getResources().getString(R.string.s3_bucket);
        BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
        s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion("ap-south-1"));
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    getPopulerVideo();
    }
    void getPopulerVideo()
    {
        Functions.showLoadingDialog(this);
        Common.getHatzoffApi().getpopularvideo().enqueue(new Callback<VideoPage>() {
            @Override
            public void onResponse(Call<VideoPage> call, Response<VideoPage> response) {
                if(response.isSuccessful())
                {
                    mpopular = response.body();
                    videoItemList = mpopular.getVideoItems();
                    videoAdapter = new VideoAdapter(videoItemList,MostPopular.this , MostPopular.this, new VideoAdapter.Pos() {
                        @Override
                        public void Choose(int position) {
                        }
                    });

                    recyclerView.setAdapter(videoAdapter);

                    Functions.cancelLoading();
                }
            }

            @Override
            public void onFailure(Call<VideoPage> call, Throwable t) {

            }
        });

    }


}