package com.aliyahatzoff.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aliyahatzoff.R;
import com.aliyahatzoff.Services.UploadService;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Sharedhelper;
import com.aliyahatzoff.utils.Variables;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

public class PostVideoActivity extends AppCompatActivity {

    ImageView video_thumbnail;
    String video_path;
    LinearLayout videoview;
    ProgressDialog progressDialog;
    Context context;
    TextView viewtext,txt_location;
    TextView pulictext,privatetext;
    LinearLayout cancel;
    String hastag, category, caption, location, isactive;
    int soundid, duet=1, comments=1;
    EditText et_caption;
    String draft_file,duet_video_id,speed_video;
    Switch switchcomment,switchduet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);
        videoview = findViewById(R.id.videoview);
        cancel =findViewById(R.id.cancel);
        viewtext = findViewById(R.id.viewtext);
        video_thumbnail = findViewById(R.id.video_thumb);
        et_caption=findViewById(R.id.et_caption);
        txt_location=findViewById(R.id.txt_location);
        switchcomment=findViewById(R.id.comment);
        switchduet=findViewById(R.id.duet);
        switchcomment.setChecked(true);
        switchduet.setChecked(true);
        speed_video="1f";
        checkPermission();
        videoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(PostVideoActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.videoview,null);
                final RelativeLayout relativeLayout = (RelativeLayout) mView.findViewById(R.id.pubc);
                final RelativeLayout relativeLayout2 = (RelativeLayout) mView.findViewById(R.id.privc);
                pulictext = (TextView)mView.findViewById(R.id.pub);
                privatetext = (TextView)mView.findViewById(R.id.private1);
                ImageView img=(ImageView)mView.findViewById(R.id.privateimg);

                ImageView img2=(ImageView)mView.findViewById(R.id.publicimg);
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(true);
                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     //   alertDialog.dismiss();
                        img.setVisibility(View.GONE);
                        img2.setVisibility(View.VISIBLE);
                        viewtext.setText(pulictext.getText().toString());
                        alertDialog.dismiss();

                    }
                });
                relativeLayout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     //   myCustomMessage.setText(txt_inputText.getText().toString());
                       // alertDialog.dismiss();
                        img.setVisibility(View.VISIBLE);
                        img2.setVisibility(View.GONE);
                        viewtext.setText(privatetext.getText().toString());
                        alertDialog.dismiss();

                    }
                });
                alertDialog.show();

            }

        });
        context = PostVideoActivity.this;
        Intent intent=getIntent();
        if(intent!=null){
            draft_file=intent.getStringExtra("draft_file");
            duet_video_id=intent.getStringExtra("duet_video_id");
            speed_video=intent.getStringExtra("speed");
        }
        video_path = Variables.output_filter_file;

        Bitmap bmThumbnail;
        bmThumbnail = ThumbnailUtils.createVideoThumbnail(video_path,
                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        if(bmThumbnail != null && duet_video_id!=null){
            Bitmap duet_video_bitmap = null;
            if(duet_video_id!=null){
                duet_video_bitmap= ThumbnailUtils.createVideoThumbnail(Variables.app_showing_folder+duet_video_id+".mp4",
                        MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            }
            Bitmap combined=combineImages(bmThumbnail,duet_video_bitmap);
            video_thumbnail.setImageBitmap(combined);

        }
        else if(bmThumbnail != null){
            video_thumbnail.setImageBitmap(bmThumbnail);

        }
        findViewById(R.id.draft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save_file_in_draft();
            }
        });
        findViewById(R.id.savevideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save_file_in_draft();
            }
        });
        findViewById(R.id.cmd_post).setOnClickListener(v -> {
            hastag = "1,2,3,4";
            caption = et_caption.getText().toString();
            location = txt_location.getText().toString();
            category="Dance";
            isactive = "Y";
            if(switchcomment.isChecked())
                comments=1;
            else
                comments=0;
            if(switchduet.isChecked())
                duet=1;
            else
                duet=0;


            if(Sharedhelper.getKey(this,"soundid").equals("0")||Sharedhelper.getKey(this,"soundid").isEmpty())
                soundid=0;
            else
            soundid = Integer.valueOf(Sharedhelper.getKey(this,"soundid"));
            if(viewtext.getText().toString().isEmpty())
                viewtext.setText("public");
           // transferToAws(hastag,caption,location,comments,duet,isactive,soundid,category,viewtext.getText().toString());
            startService(hastag,caption,location,comments,duet,isactive,soundid,category,viewtext.getText().toString());
        });
        findViewById(R.id.cancel).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(PostVideoActivity.this,LandingActivity.class));
        });

    }

    public void startService(String hastag,String caption,String location,int comments,int duet,String isactive,int soundid,String category,String viewtype) {

        UploadService mService = new UploadService();
        if (!Functions.isMyServiceRunning(this, mService.getClass())) {
            Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
            mServiceIntent.setAction("startservice");
            mServiceIntent.putExtra("hastag", hastag);
            mServiceIntent.putExtra("caption", caption);
            mServiceIntent.putExtra("location", "" + location);
            mServiceIntent.putExtra("comments", "" + comments);
            mServiceIntent.putExtra("duet", ""+duet);
            mServiceIntent.putExtra("isactive", isactive);
            mServiceIntent.putExtra("soundid", ""+soundid);
            mServiceIntent.putExtra("category", category);
            mServiceIntent.putExtra("viewtype", viewtype);
            mServiceIntent.putExtra("video_path", video_path);
            mServiceIntent.putExtra("speed", speed_video);



            startService(mServiceIntent);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendBroadcast(new Intent("uploadVideo"));
                    new  AlertDialog.Builder(context)
                            .setTitle("Video Post")
                            .setMessage("VIDEO Uploading Started")
                            .setNegativeButton("Back To Home", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    startActivity(new Intent(PostVideoActivity.this,LandingActivity.class));
                                }
                            }).show();
                }
            }, 1000);


        } else {
            Toast.makeText(PostVideoActivity.this, "Please wait video already in uploading progress", Toast.LENGTH_SHORT).show();
        }


    }


    public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        return cs;
    }

    public void Save_file_in_draft(){
        File source = new File(video_path);
        File dr=new File(Variables.draft_app_folder);
        if(!dr.exists())
            dr.mkdir();
        File destination = new File(Variables.draft_app_folder+Functions.getRandomString()+".mp4");

        try
        {
            if(source.exists()){

                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(destination);

                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
                Toast.makeText(PostVideoActivity.this, "File saved in Draft", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PostVideoActivity.this, LandingActivity.class));

            }else{
                Toast.makeText(PostVideoActivity.this, "File failed to saved in Draft", Toast.LENGTH_SHORT).show();

            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
/*
    void transferToAws(String hastag,String caption,String location,int comments,int duet,String isactive,int soundid,String category,String viewtype) {
        String secret = context.getResources().getString(R.string.s3_secret);
        String access = context.getResources().getString(R.string.s3_access_key);
        String bucket = context.getResources().getString(R.string.s3_bucket);
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
        String filename="uservideo/"+ Sharedhelper.getKey(context,"loginid")+"/"+ "hatzamz"+ s+".mp4";
        TransferObserver uploadObserver =
                transferUtility.upload(bucket, filename, file);
        Functions.Show_determinent_loader(this, false, false);

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Functions.cancel_determinent_loader();
                    Functions.showLoadingDialog(context);
                    Common.getHatzoffApi().createPost(hastag,soundid,category,caption,location,comments,duet,isactive,viewtype,filename).enqueue(new Callback<ResponseBody>() {
                        @Override

                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try{Log.d("Hatz",response.errorBody().string());}catch(Exception e){}
                            if(response.isSuccessful())
                            {
                                Functions.cancelLoading();
                                Delete_draft_file();


                                new  AlertDialog.Builder(context)
                                        .setTitle("Video Post")
                                        .setMessage("Posted Successfully")
                                        .setNegativeButton("Back To Home", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                startActivity(new Intent(PostVideoActivity.this,LandingActivity.class));
                                            }
                                        }).show();

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
   */
    public void onBackPressed(View va) {
        super.onBackPressed();
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PostVideoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Functions.Show_Alert(context, "", "Permission Denied!Please Allow to Get  Location");
            }
        }

    }

    public void getCurrentLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(PostVideoActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(PostVideoActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocation = locationResult.getLocations().size() - 1;
                            double lat = locationResult.getLocations().get(latestlocation).getLatitude();
                            double lng = locationResult.getLocations().get(latestlocation).getLongitude();
                            Log.d("Lat=", lat + "," + lng);
                            Location location = new Location("providerNa");
                            location.setLatitude(lat);
                            location.setLongitude(lng);
                            Geocoder geocoder = new Geocoder(PostVideoActivity.this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(lat, lng, 1);
                                if (addresses == null || addresses.isEmpty()) {
                                    Functions.Show_Alert(context, "", "Somthing Wrong !");
                                } else {
                                    String s = "";
                                    Address address = addresses.get(0);
                                   // Sharedhelper.putKey(LandingActivity.this, "lat", String.valueOf(lat));
                                   // Sharedhelper.putKey(LandingActivity.this, "lng", String.valueOf(lng));
                                   // Sharedhelper.putKey(PostVideoActivity.this, "address", String.valueOf(address.getAddressLine(0)));
                                    txt_location.setText(String.valueOf(address.getAddressLine(0)));

                                }
                            } catch (Exception e) {

                            }

                        }


                    }
                }, Looper.getMainLooper());
    }

    public void Delete_draft_file(){
        try {
            if(draft_file!=null) {
                File file = new File(draft_file);
                file.delete();
            }
        }catch (Exception e){

        }


    }

}