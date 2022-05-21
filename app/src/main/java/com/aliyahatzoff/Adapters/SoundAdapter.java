package com.aliyahatzoff.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Api.ApiClient;
import com.aliyahatzoff.Models.Soundlist;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Sharedhelper;
import com.aliyahatzoff.utils.Variables;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundHolder> {
    Context context;
    List<Soundlist> soundlistList;
    SimpleExoPlayer player;
    String soundurl;
    ImageView prv_img;
    int soundid;
    Boolean isplaying = false;
    public  SoundAdapter.OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(View view,int position,Soundlist item);
    }
    public SoundAdapter(Context context, List<Soundlist> soundlistList,SoundAdapter.OnItemClickListener listener) {
        this.context = context;
        this.soundlistList = soundlistList;
        this.listener=listener;
    }
    @NonNull
    @Override
    public SoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SoundAdapter.SoundHolder(
                LayoutInflater.from(context).inflate(R.layout.model_addmusic, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SoundHolder holder, int position) {
        holder.textView.setText(soundlistList.get(position).getSoundname());
        if(soundlistList.get(position).getThumb()!=null)
        if(!soundlistList.get(position).getThumb().equals("thumb"))
        {
            Picasso.get().
                    load(ApiClient.image_URL + "/soundfile/"+soundlistList.get(position).getThumb())
                    .resize(150, 150)
                    .placeholder(context.getResources().getDrawable(R.drawable.singer))
                    .into(holder.soundimg);
        }
        holder.bind(position,soundlistList.get(position),listener);

      /*  holder.imgplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isplaying) {
                    if (prv_img != null) {
                        prv_img.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    }

                    holder.Release_Privious_Player();
                    holder.imgplay.setImageResource(R.drawable.exo_controls_play);
                } else {
                    Functions.cancelLoading();
                    Functions.showLoadingDialog(context);
                    holder.Release_Privious_Player();
                    holder.setPlayer(soundlistList.get(position).getFileurl());
                    holder.imgplay.setImageResource(R.drawable.ic_baseline_stop_24);
                    System.out.println("m=" + holder.imgplay.toString());
                    prv_img = holder.imgplay;
                }
            }
        });*/

    }

    public String getsoundTrack() {
        return soundurl;
    }

    public int getsoundId() {
        return soundid;
    }

    @Override
    public int getItemCount() {
        return soundlistList.size();
    }

    class SoundHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imgplay,soundimg;


        public SoundHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.music_title);
            imgplay = itemView.findViewById(R.id.play);
            soundimg=itemView.findViewById(R.id.soundimage);
          /*  itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Release_Privious_Player();
                    soundurl = soundlistList.get(getAdapterPosition()).getFileurl();
                    soundid = soundlistList.get(getAdapterPosition()).getId();

                Down_load_mp3(soundid,soundlistList.get(getAdapterPosition()).getSoundname(),soundurl);
                }
            });*/
        }
        public void bind(final int pos,final  Soundlist item,final  SoundAdapter.OnItemClickListener listener)
        {
            itemView.setOnClickListener(v -> {
                listener.onItemClick(v,pos,item);
            });
            imgplay.setOnClickListener(v -> {
                listener.onItemClick(v,pos,item);
            });
        }
/*
        public void Release_Privious_Player() {
            if (player != null) {
                player.setPlayWhenReady(false);
                player.removeListener(this);
                player.release();
                isplaying = false;
            }
        }

        public void setPlayer(String url) {

            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            final ExoPlayer player =  new SimpleExoPlayer.Builder( context, PlayerUtil.renderersFactory(context))
              .setMediaSourceFactory(PlayerUtil.createMediaSourceFactory(context))
              .setTrackSelector(trackSelector)
              .build();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "HatzOff"));
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(url));
            player.addListener(this);
            player.prepare(videoSource);
            player.setPlayWhenReady(true);
            isplaying = true;

        }
*/

        DownloadRequest prDownloader;

        public void Down_load_mp3(final int id, final String sound_name, String url) {

            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();

            prDownloader = PRDownloader.download(url, Variables.app_hided_folder, Variables.SelectedAudio)
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {

                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {

                        }
                    });

            prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    progressDialog.dismiss();
                    Intent output = new Intent();
                    output.putExtra("isSelected", "yes");
                    output.putExtra("sound_id", soundid);
                    output.putExtra("sound_name",sound_name);
                    Sharedhelper.putKey(context,"soundid",String.valueOf(soundid));
                    ((Activity) context).setResult(RESULT_OK, output);
                    ((Activity) context).finish();
                    ((Activity) context).overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
                }
                @Override
                public void onError(Error error) {
                    progressDialog.dismiss();
                }
            });

        }

    }


}
