package com.aliyahatzoff.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


import com.aliyahatzoff.Activities.LandingActivity;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Sharedhelper;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by qboxus on 6/7/2018.
 */


// this the background service which will upload the video into database
public class UploadService extends Service {


    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public UploadService getService() {
            return UploadService.this;
        }
    }

    boolean mAllowRebind;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }



    String hastag,caption, location,isactive,category,viewtype,video_path,speed_video;
    int comments, duet, soundid;


    public UploadService() {
        super();
    }


    @Override
    public void onCreate() {

        //sharedPreferences = Functions.getSharedPreference(this);
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {


        // get the all the selected date for send to server during the post video

        if (intent != null && intent.getAction().equals("startservice")) {
            showNotification();

            hastag=intent.getStringExtra("hastag");
            caption=intent.getStringExtra("caption");
            location=intent.getStringExtra("location");
            comments=Integer.valueOf(intent.getStringExtra("comments"));
            duet=Integer.valueOf(intent.getStringExtra("duet"));
            isactive=intent.getStringExtra("isactive");
            soundid=Integer.valueOf(intent.getStringExtra("soundid"));
            category=intent.getStringExtra("category");
            viewtype=intent.getStringExtra("viewtype");
            video_path=intent.getStringExtra("video_path");
            speed_video=intent.getStringExtra("speed");



            new Thread(new Runnable() {
                @Override
                public void run() {


                    String secret = getResources().getString(R.string.s3_secret);
                    String access = getResources().getString(R.string.s3_access_key);
                    String bucket = getResources().getString(R.string.s3_bucket);
                    //AmazonS3 s3Client = new AmazonS3Client(new BasicSessionCredentials(access, secret, sessionToken));
                    BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
                    AmazonS3Client s3Client = new AmazonS3Client(credentials);
                    TransferUtility transferUtility =
                            TransferUtility.builder()
                                    .context(getApplicationContext())
                                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                                    .s3Client(s3Client)
                                    .build();
                    File file = new File(video_path);
                    Date currentTime = Calendar.getInstance().getTime();
                    String s=String.valueOf(currentTime.getHours())+String.valueOf(currentTime.getMinutes())
                            +String.valueOf(currentTime.getSeconds())+String.valueOf(currentTime.getYear());
                    String filename="uservideo/"+ Sharedhelper.getKey(getApplicationContext(),"loginid")+"/"+ "hatzamz"+ s+".mp4";
                    TransferObserver uploadObserver =
                            transferUtility.upload(bucket, filename, file);
                    //Functions.Show_determinent_loader(getApplicationContext(), false, false);

                    uploadObserver.setTransferListener(new TransferListener() {

                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            if (TransferState.COMPLETED == state) {
                              //  Functions.cancel_determinent_loader();
                      //          Functions.showLoadingDialog(context);
                                Common.getHatzoffApi().createPost(hastag,soundid,category, StringEscapeUtils.escapeJava(caption),location,comments,duet,isactive,viewtype,filename,speed_video).enqueue(new Callback<ResponseBody>() {
                                    @Override

                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        try{
                                            Log.d("Hatz",response.errorBody().string());}catch(Exception e){}
                                        if(response.isSuccessful())
                                        {
                                            //Functions.cancelLoading();
                                            stopForeground(true);
                                            stopSelf();

                                            sendBroadcast(new Intent("uploadVideo"));
                                            sendBroadcast(new Intent("newVideo"));


                                          /*  new  AlertDialog.Builder(context)
                                                    .setTitle("Video Post")
                                                    .setMessage("Posted Successfully")
                                                    .setNegativeButton("Back To Home", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                            startActivity(new Intent(PostVideoActivity.this,LandingActivity.class));
                                                        }
                                                    }).show();*/

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Functions.cancelLoading();

                                    }
                                });


                            }
                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                            float percentDonef = ((float) bytesCurrent / (float) bytesTotal);
                            Functions.Show_loading_progress((int) (percentDonef * 100));

                        }

                        @Override
                        public void onError(int id, Exception ex) {
                            // Handle errors
                            System.out.println("error"+ex.getMessage());
                        }

                    });




                }
            }).start();


        } else if (intent != null && intent.getAction().equals("stopservice")) {
            stopForeground(true);
            stopSelf();
        }


        return Service.START_STICKY;
    }


    // this will show the sticky notification during uploading video
    private void showNotification() {

        Intent notificationIntent = new Intent(this, LandingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        final String CHANNEL_ID = "default";
        final String CHANNEL_NAME = "Default";

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel defaultChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(defaultChannel);
        }

        androidx.core.app.NotificationCompat.Builder builder = (androidx.core.app.NotificationCompat.Builder) new androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setContentTitle("Uploading Video")
                .setContentText("Please wait! Video is uploading....")
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        android.R.drawable.stat_sys_upload))
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        startForeground(101, notification);

    }


    // delete the video from draft after post video



}