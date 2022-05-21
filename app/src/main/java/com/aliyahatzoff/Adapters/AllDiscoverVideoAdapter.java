package com.aliyahatzoff.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Models.VideoGallery;
import com.aliyahatzoff.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.List;

public class AllDiscoverVideoAdapter  extends RecyclerView.Adapter<AllDiscoverVideoAdapter.VideoGalleryHolder> {
    Context context;
    List<VideoGallery> videoGalleryList;
    OnItemClickListener listener;
    String vtype;
    public AllDiscoverVideoAdapter(Context context, List<VideoGallery> videoGalleryList,String vtype, OnItemClickListener listener) {
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
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
     holder.pbar.setVisibility(View.GONE);

            Glide.with(context)
                    .applyDefaultRequestOptions(options)
                    .load(Uri.parse(videoGalleryList.get(position).getVideo_path()))
                    .override(240,240)
                   .listener(new RequestListener<Drawable>() {
                       @Override
                       public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                           return false;
                       }

                       @Override
                       public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                           return false;
                       }
                   })
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
        ProgressBar pbar;

        public VideoGalleryHolder(@NonNull View itemView) {
            super(itemView);
            thumb_image=itemView.findViewById(R.id.video_thumb);
            pbar=itemView.findViewById(R.id.pb);
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
