package com.aliyahatzoff.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Activities.OthersProfile;
import com.aliyahatzoff.Api.ApiClient;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.Comment;
import com.aliyahatzoff.Models.VideoItem;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.PlayerUtil;
import com.aliyahatzoff.utils.Sharedhelper;
import com.aliyahatzoff.utils.Variables;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.Videoviewholder> {
    List<VideoItem> videoItemList;
    Context context;
    FragmentManager fragmentManager;
    String imageFilePath;
    Activity activity;
    Pos pos;
    RequestQueue requestQueue;
    String  secret, access, bucket;
    AmazonS3Client s3Client;
    GeneratePresignedUrlRequest request;
    URL objectURL;

    public static interface Pos{
        void Choose(int position);
    }
    public VideoAdapter(List<VideoItem> videoItemList, Context context, Activity activity,Pos pos ) {
        this.videoItemList = videoItemList;
        this.context = context;
        this.activity = activity;
        this.pos=pos;
    }
    public void setList(List<VideoItem> list) {
        this.videoItemList = list;
        notifyDataSetChanged();
    }
public List<VideoItem> getall()
{
    return  videoItemList;
}
    public void addAll(List<VideoItem> newList) {
        int lastIndex = videoItemList.size() - 1;
        videoItemList.addAll(newList);
        notifyItemRangeInserted(lastIndex, newList.size());
        notifyDataSetChanged();
    }
    public interface OnItemClickListener {
        void onItemClick(int positon, View view);
    }

    @NonNull
    @Override
    public Videoviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VideoAdapter.Videoviewholder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.model_video_container, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull Videoviewholder holder, int position) {
        // holder.setVideoData(videoItemList.get(position));
       // System.out.println("posti="+position);
        VideoItem videoItem=videoItemList.get(position);
        pos.Choose(position);
      //  System.out.println("item-"+position+"="+videoItem.getId()+"-->"+videoItem.getTotalview());
        holder.userid.setText(String.valueOf(videoItemList.get(position).getName()));
        holder.utime.setText(videoItemList.get(position).getGap());
        holder.music.setText(videoItemList.get(position).getSoundname());
        holder.caption.setText(StringEscapeUtils.unescapeJava(videoItemList.get(position).getCaption()));

        holder.txt_like.setText(String.valueOf(videoItemList.get(position).getLike()));
        holder.txt_comment.setText(String.valueOf(videoItemList.get(position).getComment()));
        holder.txt_share.setText(String.valueOf(videoItemList.get(position).getShare()));
        holder.txt_totalview.setText(String.valueOf(videoItemList.get(position).getTotalview())+" View");


        if (videoItemList.get(position).getUser_id() == Integer.valueOf(Sharedhelper.getKey(context, "loginid"))) {
            holder.txt_follow.setVisibility(View.GONE);
        } else {
            if (videoItemList.get(position).getFollow().equals("Y"))
                holder.txt_follow.setText("Followed");
            else
                holder.txt_follow.setText("Follow");

        }
        if (Sharedhelper.getKey(context, "profilepic").contains("http"))
            imageFilePath = videoItemList.get(position).getProfilepic();
        else {
            //  imageFilePath = ApiClient.image_URL + "/profile/" + videoItemList.get(position).getProfilepic();
           // request = new GeneratePresignedUrlRequest(bucket, videoItemList.get(position).getProfilepic());
           // objectURL = s3Client.generatePresignedUrl(request);
            //imageFilePath = objectURL.toString();
            imageFilePath=context.getResources().getString(R.string.cloudfront)+"/"+videoItemList.get(position).getProfilepic();

        }
        try {
            Picasso.get().
                    load(imageFilePath)
                    .resize(50, 50)
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .into(holder.profile);

        } catch (Exception e) {

        }


    }
    @Override
    public int getItemCount() {
        return videoItemList.size();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
    }
    class Videoviewholder extends RecyclerView.ViewHolder {
        PlayerView videoView;
        SimpleExoPlayer player;
        private int PERMISSION_CALLBACK_CONSTANT = 1000;

        TextView userid, utime, music, caption, txt_like, txt_comment, txt_share, txt_follow,txt_totalview;
        ImageView message, sharevideo, likevideo,compition;
        BottomSheetDialog bottomSheetDialog;
        RecyclerView rv_comments;
        EmojIconActions emojIcon;
        EmojiconEditText emojiconEditText2;
        LinearLayout lout_user;
        ImageView profile;
        //SpaceNavigationView spaceNavigationView;
        public Videoviewholder(@NonNull View itemView) {
            super(itemView);

            userid = itemView.findViewById(R.id.userid);
            utime = itemView.findViewById(R.id.utime);
            videoView = itemView.findViewById(R.id.videoview);
            message = itemView.findViewById(R.id.message);

            sharevideo = itemView.findViewById(R.id.sharevideo);

            likevideo = itemView.findViewById(R.id.viewvideo);
            lout_user=itemView.findViewById(R.id.lout_userprofile);

            music = itemView.findViewById(R.id.music);
            caption = itemView.findViewById(R.id.txt_caption);
            txt_comment = itemView.findViewById(R.id.txt_comment);
            txt_totalview = itemView.findViewById(R.id.total_view);
            txt_like = itemView.findViewById(R.id.txt_like);
            txt_share = itemView.findViewById(R.id.txt_share);
            txt_follow = itemView.findViewById(R.id.follow);
        //    spaceNavigationView = ((LandingActivity) activity).findViewById(R.id.spacenavigation);
          //  spaceNavigationView.setCentreButtonSelectable(true);
            profile = itemView.findViewById(R.id.img_profile);
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.activity_comment_screen);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            ImageView sent = bottomSheetDialog.findViewById(R.id.send);
//            ImageView emoji = bottomSheetDialog.findViewById(R.id.emoji);
            ImageView close = bottomSheetDialog.findViewById(R.id.img_close);
            try {
                EmojiconEditText comment = bottomSheetDialog.findViewById(R.id.comment);
                close.setOnClickListener(v -> {
                    bottomSheetDialog.dismiss();
                });

                rv_comments = bottomSheetDialog.findViewById(R.id.rv_comment);
                rv_comments.setLayoutManager(new LinearLayoutManager(context));


                likevideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                lout_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, OthersProfile.class);
                        intent.putExtra("username", videoItemList.get(getAdapterPosition()).getName());
                        intent.putExtra("userid", String.valueOf(videoItemList.get(getAdapterPosition()).getUser_id()));
                        intent.putExtra("interest", videoItemList.get(getAdapterPosition()).getInterest());
                        intent.putExtra("profilepic", videoItemList.get(getAdapterPosition()).getProfilepic());
                        intent.putExtra("follows",  videoItemList.get(getAdapterPosition()).getFollows());
                        intent.putExtra("following",  videoItemList.get(getAdapterPosition()).getFollowing());
                        intent.putExtra("like",  videoItemList.get(getAdapterPosition()).getHearts());
                        context.startActivity(intent);
                    }
                });
                message.setOnClickListener(v -> {
                    getcomment(String.valueOf(videoItemList.get(getAdapterPosition()).getId()));
                    sent.setOnClickListener(v1 -> {
                        if (!(comment.getText().toString().length() <= 0 || comment.getText().toString().isEmpty())) {
                            Functions.showLoadingDialog(context);
                            Common.getHatzoffApi().comment(String.valueOf(videoItemList.get(getAdapterPosition()).getId()),
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
                                        generatepushNotification();
                                        getcomment(String.valueOf(videoItemList.get(getAdapterPosition()).getId()));
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
                sharevideo.setOnClickListener(v -> {

                         downloadfile();


                });
                DefaultTrackSelector trackSelector = new DefaultTrackSelector();
                player =  new SimpleExoPlayer.Builder( context, PlayerUtil.renderersFactory(context))
              .setMediaSourceFactory(PlayerUtil.createMediaSourceFactory(context))
              .setTrackSelector(trackSelector)
              .build();
                txt_follow.setOnClickListener(v -> {
                    if (txt_follow.getText().equals("Follow")) {
                        Functions.showLoadingDialog(context);
                        Common.getHatzoffApi().followvideo(String.valueOf(videoItemList.get(getAdapterPosition()).getId())).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    System.out.println(response.errorBody().string());
                                } catch (Exception e) {
                                }
                                if (response.isSuccessful()) {
                                    Functions.cancelLoading();
                                    txt_follow.setText("Followed");

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Functions.cancelLoading();

                            }
                        });
                    }
                });
                videoView.setPlayer(player);
            } catch (Exception e) {
                Log.d("VideoAdapter", e.getMessage());
            }

        }

        private void generatepushNotification() {
            FirebaseMessaging.getInstance().subscribeToTopic("news");
requestQueue= Volley.newRequestQueue(context);
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("to","/topics/"+"news");
                JSONObject notificationObject=new JSONObject();
                notificationObject.put("title","any title");
                notificationObject.put("body","any body");
                jsonObject.put("notification",notificationObject);

                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, ApiClient.FCM_URL, jsonObject
                        , new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> stringMap=new HashMap<>();
                        stringMap.put("content-type","application/json");
                        stringMap.put("authorization","key=");

                        return stringMap;
                    }
                };
                requestQueue.add(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private void getcomment(String vid) {
            Common.getHatzoffApi().gecomment(vid).enqueue(new Callback<List<Comment>>() {
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

        private void openBottomSheet(View v) {
            //View view = activity.getLayoutInflater ().inflate (R.layout.bottom_sheet, null);
            // View view = inflater.inflate( R.layout.bottom_sheet, null );
            Context context = v.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_comment_screen, null);
            final Dialog mBottomSheetDialog = new Dialog(context);
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            mBottomSheetDialog.show();
        }


        void setVideoData(VideoItem videoItem) {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "HatzOff"));
            /*MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(videoItem.getFileurl()));
            LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource, 100);*/
            player.setMediaItem(PlayerUtil.getMediaItem(Uri.parse(videoItem.getFileurl())));
            player.prepare();
            player.setPlayWhenReady(true);
        }

        public void downloadfile() {
            String filename = "hatzoff" + new SimpleDateFormat("DHHmmssS").format(new Date()) + ".mp4";
            String folder=Environment.getExternalStorageDirectory().toString()+"/.HidedHatzoff/";
            Functions.showLoadingDialog(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                folder=Variables.app_showing_folder;
            }

            Log.d("time=",folder);

            PRDownloader.initialize(context);
            DownloadRequest prDownloader = PRDownloader.download(context.getResources().getString(R.string.cloudfront)+"/"+videoItemList.get(getAdapterPosition()).getFileurl(), folder, filename)
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

                    Functions.cancelLoading();
                    Log.d("dowload","com");
                    Uri uri=null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                     uri = Uri.parse(Variables.app_showing_folder + "/"+ filename);
                    else
                        uri = Uri.parse(Environment.getExternalStorageDirectory().toString()+"/.HidedHatzoff/" + filename);


                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hi Friend ,download Hatzoff app from Playstore, Enjoy the most viral & trending video https://play.google.com/store/apps/details?id=com.hatzoff Visit our website:- https://hatzoff.in/");
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    whatsappIntent.putExtra("videoid", videoItemList.get(getAdapterPosition()).getId());
                    whatsappIntent.setType("*/*");
                    whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(whatsappIntent);
                        Common.getHatzoffApi().sharevideo(String.valueOf(videoItemList.get(getAdapterPosition()).getId())).enqueue(new Callback<ResponseBody>() {
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
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    Functions.cancelLoading();
                }


            });

        }
    }
}



