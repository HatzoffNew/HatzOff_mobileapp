package com.aliyahatzoff.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.Comment;
import com.aliyahatzoff.Models.VideoGal;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonDiscoverAdapter  extends RecyclerView.Adapter<CommonDiscoverAdapter.ViewHolder> {
    Context context;
    List<VideoGal> videoGalleryList;
    String vtype;
    public CommonDiscoverAdapter(Context context, List<VideoGal> videoGalleryList, String vtype) {
        this.context = context;
        this.videoGalleryList = videoGalleryList;
        this.vtype=vtype;
    }

    @NonNull
    @Override
    public CommonDiscoverAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_model,parent,false)
        );
    }
    @Override
    public void onBindViewHolder(@NonNull CommonDiscoverAdapter.ViewHolder holder, int position) {
        holder.videoView.setVideoURI(Uri.parse(videoGalleryList.get(position).getVideo_path()));
        holder.videoView.requestFocus();
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.isLooping();
            }
        });
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
               holder.videoView.start();
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        MediaController mediaController = new MediaController(context);
                        holder.videoView.setMediaController(mediaController);
                        mediaController.hide();
                        mediaController.setAnchorView(holder.videoView);
                    }
                });
            }
        });
        holder.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
        holder.bind(position,videoGalleryList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoGalleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        CircleImageView sharevideo,compition;
        BottomSheetDialog bottomSheetDialog;
        RecyclerView rv_comments;
        ImageView message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView=itemView.findViewById(R.id.videoview);
            sharevideo=itemView.findViewById(R.id.sharevideo);
            message=itemView.findViewById(R.id.message);
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.activity_comment_screen);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            ImageView sent = bottomSheetDialog.findViewById(R.id.send);
            ImageView close = bottomSheetDialog.findViewById(R.id.img_close);
                rv_comments = bottomSheetDialog.findViewById(R.id.rv_comment);
                rv_comments.setLayoutManager(new LinearLayoutManager(context));
            EmojiconEditText comment = bottomSheetDialog.findViewById(R.id.comment);
            close.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
            });
            message.setOnClickListener(v -> {
                getcomment(String.valueOf(videoGalleryList.get(getAdapterPosition()).getVideo_id()));
                sent.setOnClickListener(v1 -> {
                    if (!(comment.getText().toString().length() <= 0 || comment.getText().toString().isEmpty())) {
                        Functions.showLoadingDialog(context);
                        Common.getHatzoffApi().comment(String.valueOf(videoGalleryList.get(getAdapterPosition()).getVideo_id()),
                                StringEscapeUtils.escapeJava(comment.getText().toString())).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    System.out.println(response.errorBody().string());
                                } catch (Exception e) {
                                }
                                if (response.isSuccessful()) {
                                    Functions.cancelLoading();
                                    comment.setText("");
//                                    generatepushNotification();
                                    getcomment(String.valueOf(videoGalleryList.get(getAdapterPosition()).getVideo_id()));
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Functions.cancelLoading();
                            }
                        });
                    }
                });
                bottomSheetDialog.show();
            });
            sharevideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadfile();
                }
            });
            rv_comments = bottomSheetDialog.findViewById(R.id.rv_comment);
            rv_comments.setLayoutManager(new LinearLayoutManager(context));
        }

        private void getcomment(String valueOf) {
            Common.getHatzoffApi().gecomment(valueOf).enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (Exception e) {
                    }
                    if (response.isSuccessful()) {
                        rv_comments.setAdapter(new CommentAdapter(response.body(), context));
                    }
                }

                @Override
                public void onFailure(Call<List<Comment>> call, Throwable t) {

                }
            });
        }

        public void bind(final  int position,final VideoGal videoGallery ) {
        }
        private void downloadfile() {
        /*    String macaddress,secret,access,bucket;
            AmazonS3Client s3Client;
            GeneratePresignedUrlRequest request;
            URL objectURL;
            secret = context.getResources().getString(R.string.s3_secret);
            access = context.getResources().getString(R.string.s3_access_key);
            bucket = context.getResources().getString(R.string.s3_bucket);
            BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
            s3Client = new AmazonS3Client(credentials);
            s3Client.setRegion(Region.getRegion("ap-south-1"));

            request = new GeneratePresignedUrlRequest(bucket, videoGalleryList.get(getAdapterPosition()).getVideo_path());
            objectURL = s3Client.generatePresignedUrl(request);*/

            Functions.showLoadingDialog(context);
            PRDownloader.initialize(context);
            //  System.out.println(videoItemList.get(getAdapterPosition()).getFileurl());
            DownloadRequest prDownloader = PRDownloader.download(context.getString(R.string.cloudfront)+"/"+videoGalleryList.get(getAdapterPosition()).getVideo_path(), Variables.app_showing_folder, "HatzoffVideo.mp4")
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
                            int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);

                        }
                    });


            prDownloader.start(new OnDownloadListener() {

                @Override
                public void onDownloadComplete() {
                    System.out.println("ok2");

                    Functions.cancelLoading();
                    Uri uri = Uri.parse(Variables.app_showing_folder + "HatzoffVideo.mp4");
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hi Friend ,download Hatzoff app from Playstore,Enjoy the most viral & trending video");
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    whatsappIntent.putExtra("videoid", videoGalleryList.get(getAdapterPosition()).getVideo_id());
                    whatsappIntent.setType("*/*");
                    whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(whatsappIntent);
                        Common.getHatzoffApi().sharevideo(String.valueOf(videoGalleryList.get(getAdapterPosition()).getVideo_id())).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    System.out.println(response.errorBody().string());
                                } catch (Exception e) {
                                }
                                if (response.isSuccessful()) {
                                    Log.d("Shared", "ok");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });

                    } catch (android.content.ActivityNotFoundException ex) {
                        Functions.Show_Alert(context, "Whatsapp", "Whatsapp have not been installed.");
                    }
                }

                @Override
                public void onError(Error error) {
                    Log.d("str",error.getServerErrorMessage());
                    Functions.cancelLoading();
                }


            });

        }
    }
}