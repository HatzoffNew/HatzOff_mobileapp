package com.aliyahatzoff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.aliyahatzoff.Models.VideoModel;
import com.aliyahatzoff.R;

import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.aliyahatzoff.utils.PlayerUtil;

import java.util.List;

public class GalleryVideoAdapter extends RecyclerView.Adapter<GalleryVideoAdapter.viewHolder> {

    Context context;
    List<VideoModel> videoArrayList;
    public OnItemClickListener onItemClickListener;

    public GalleryVideoAdapter(Context context, List<VideoModel> videoArrayList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.videoArrayList = videoArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public GalleryVideoAdapter.viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_gallery, viewGroup, false);
        return new viewHolder(view);
    }
    @Override
    public void onBindViewHolder(final GalleryVideoAdapter.viewHolder holder, final int i) {
        holder.title.setText(videoArrayList.get(i).getVideoTitle());
        holder.duration.setText(videoArrayList.get(i).getVideoDuration());
    }
    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title, duration;
        PlayerView videoView;
        SimpleExoPlayer player;


        public viewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            duration = (TextView) itemView.findViewById(R.id.duration);
            videoView = itemView.findViewById(R.id.videoview);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            player =  new SimpleExoPlayer.Builder( context,PlayerUtil.renderersFactory(context))
              .setMediaSourceFactory(PlayerUtil.createMediaSourceFactory(context))
              .setTrackSelector(trackSelector)
              .build();
            videoView.setPlayer(player);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }

    }
    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}
