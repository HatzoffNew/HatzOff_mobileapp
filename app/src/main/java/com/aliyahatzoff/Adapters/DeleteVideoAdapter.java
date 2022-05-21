package com.aliyahatzoff.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Api.ApiClient;
import com.aliyahatzoff.Api.ApiInterface;
import com.aliyahatzoff.Models.VideoGallery;
import com.aliyahatzoff.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.File;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteVideoAdapter extends RecyclerView.Adapter<DeleteVideoAdapter.DeleteVideHolder> {
    Context context;
    List<VideoGallery> videoGalleryList;
    DeleteVideoAdapter.OnItemClickListener listener;
    String vtype;

    public DeleteVideoAdapter(Context context, List<VideoGallery> videoGalleryList, String vtype,OnItemClickListener listener) {
        this.context = context;
        this.videoGalleryList = videoGalleryList;
        this.listener = listener;
        this.vtype = vtype;
    }

    @NonNull
    @Override
    public DeleteVideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeleteVideHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_design,parent,false));
    }
    public interface OnItemClickListener {
        void onItemClick(int postion, VideoGallery item, View view);
    }
    @Override
    public void onBindViewHolder(@NonNull DeleteVideHolder holder, int position) {
       // holder.view_txt.setText(videoGalleryList.get(position).getVideo_time());
        if(vtype.equals("local")) {
            holder.pbar.setVisibility(View.GONE);
            Glide.with(context)

                    .load(Uri.fromFile(new File(videoGalleryList.get(position).getVideo_path())))
                    .into(holder.thumb_image);
        }
        else
        {
            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            //holder.pbar.setVisibility(View.GONE);
            holder.shimer.startShimmer();
            holder.shimer.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .applyDefaultRequestOptions(options)
                    .load(videoGalleryList.get(position).getVideo_path())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.shimer.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .override(240,240)
                    .into(holder.thumb_image)

            ;

        }
        holder.bind(position,videoGalleryList.get(position),listener);

    }

    @Override
    public int getItemCount() {
        return videoGalleryList.size();
    }

    class DeleteVideHolder extends RecyclerView.ViewHolder{
        ImageView thumb_image;
        ProgressBar pbar;
        LinearLayout delete;
        ShimmerFrameLayout shimer;

        public DeleteVideHolder(@NonNull View itemView) {
            super(itemView);
            thumb_image=itemView.findViewById(R.id.thumb_image);
            delete=itemView.findViewById(R.id.cmd_delete);
            shimer=itemView.findViewById(R.id.shimmer);
            //view_txt=itemView.findViewById(R.id.view_txt);
            //pbar=itemView.findViewById(R.id.pb);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ApiClient.getRetrofit().create(ApiInterface.class).deletepost(String.valueOf(videoGalleryList.get(getAdapterPosition()).getId())).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                System.out.println(response.errorBody().string());
                            } catch (Exception e) {
                            }
                            if(response.isSuccessful())
                            {
                                videoGalleryList.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                notifyItemRangeChanged(getAdapterPosition(),videoGalleryList.size());
                            }

                            }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            });

        }

        public void bind(final  int position,final VideoGallery videoGallery,final DeleteVideoAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position,videoGallery,itemView);
                }
            });
        }
    }
}
