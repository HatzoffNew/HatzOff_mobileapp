package com.aliyahatzoff.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Activities.OthersProfile;
import com.aliyahatzoff.Models.User;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Sharedhelper;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.bumptech.glide.Glide;

import java.net.URL;
import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.Viewholder> {
    Context context;
    List<User> followingsModelList;
    String secret, access, bucket;
    AmazonS3Client s3Client;
    GeneratePresignedUrlRequest request;
    URL objectURL;


    public FollowersAdapter(Context context, List<User> followingsModelList) {
        this.context = context;
        this.followingsModelList = followingsModelList;
        secret = context.getResources().getString(R.string.s3_secret);
        access = context.getResources().getString(R.string.s3_access_key);
        bucket = context.getResources().getString(R.string.s3_bucket);
        BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
        s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion("ap-south-1"));

    }

    @NonNull
    @Override
    public FollowersAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.followers_followings_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersAdapter.Viewholder holder, int position) {
        holder.userName.setText(followingsModelList.get(position).getName());
        String imageFilePath = "";
        if (Sharedhelper.getKey(context, "profilepic").contains("http"))
            imageFilePath = followingsModelList.get(position).getProfile_pic();
        else {
            //  imageFilePath = ApiClient.image_URL + "/profile/" + videoItemList.get(position).getProfilepic();
           // request = new GeneratePresignedUrlRequest(bucket, followingsModelList.get(position).getProfile_pic());
           // objectURL = s3Client.generatePresignedUrl(request);
            imageFilePath = context.getString(R.string.cloudfront)+"/"+followingsModelList.get(position).getProfile_pic();
        }
        try {
            Glide.with(context)
                    .load(imageFilePath) // image url
                    .placeholder(R.drawable.profile_image_placeholder) // any placeholder to load at start// any image in case of error
                    .override(200, 200) // resizing
                    .centerCrop()
                    .into(holder.profile);

        } catch (Exception e) {

        }


    }

    @Override
    public int getItemCount() {
        return followingsModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView profile;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            profile = itemView.findViewById(R.id.user_picture);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OthersProfile.class);
                    intent.putExtra("username", followingsModelList.get(getAdapterPosition()).getName());
                    intent.putExtra("userid", String.valueOf(followingsModelList.get(getAdapterPosition()).getId()));
                    intent.putExtra("interest", followingsModelList.get(getAdapterPosition()).getInterest());
                    intent.putExtra("profilepic", followingsModelList.get(getAdapterPosition()).getProfile_pic());
                    intent.putExtra("follows", followingsModelList.get(getAdapterPosition()).getFollow());
                    intent.putExtra("following", followingsModelList.get(getAdapterPosition()).getFollowing());
                    intent.putExtra("like", followingsModelList.get(getAdapterPosition()).getLike());
                    context.startActivity(intent);
                }
            });
        }
    }
}
