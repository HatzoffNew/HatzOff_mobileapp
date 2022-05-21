package com.aliyahatzoff.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Models.VideoGallery;
import com.aliyahatzoff.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;

public class RecentVideoAdapter  extends RecyclerView.Adapter<RecentVideoAdapter.VideoGalleryHolder> {

    Context context;
    List<VideoGallery> videoGalleryList;
    OnItemClickListener listener;
    String vtype;
    public RecentVideoAdapter(Context context, List<VideoGallery> videoGalleryList,String vtype, OnItemClickListener listener) {
        this.context = context;
        this.videoGalleryList = videoGalleryList;
        this.listener = listener;
        this.vtype=vtype;
    }
    @NonNull
    @Override
    public VideoGalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoGalleryHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.commom_discover_model,parent,false)
        );
    }
    public interface OnItemClickListener {
        void onItemClick(int postion, VideoGallery item, View view);
    }
    @Override
    public void onBindViewHolder(@NonNull VideoGalleryHolder holder, int position) {
        if(vtype.equals("local")) {
            Glide.with(context)
                    .load(Uri.fromFile(new File(videoGalleryList.get(position).getVideo_path())))
                    .into(holder.thumb_image);
        }
        else
        {
            RequestOptions options = new RequestOptions();
            Glide.with(context).asBitmap()
                    .load(Uri.parse(videoGalleryList.get(position).getVideo_path()))
                    .apply(options)
                    .into(holder.thumb_image);
        }
        holder.bind(position,videoGalleryList.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return videoGalleryList.size();
    }

    class VideoGalleryHolder extends RecyclerView.ViewHolder{
        ImageView thumb_image;

        public VideoGalleryHolder(@NonNull View itemView) {
            super(itemView);
            thumb_image=itemView.findViewById(R.id.video_thumb);
        }
        public void bind(final  int position,final VideoGallery videoGallery,final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position,videoGallery,itemView);
                }
            });
        }
    }
}
