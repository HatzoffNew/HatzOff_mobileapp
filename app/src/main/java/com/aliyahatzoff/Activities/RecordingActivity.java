package com.aliyahatzoff.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aliyahatzoff.R;
import com.aliyahatzoff.SegmentProgress.ProgressBarListener;
import com.aliyahatzoff.SegmentProgress.SegmentedProgressBar;
import com.aliyahatzoff.tedit.StickerBSFragment;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Merge_Sound;
import com.aliyahatzoff.utils.Sharedhelper;
import com.aliyahatzoff.utils.Variables;
import com.airbnb.lottie.LottieAnimationView;
import com.coremedia.iso.boxes.Container;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Facing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//import com.arthenica.mobileffmpeg.FFmpeg;


public class RecordingActivity extends AppCompatActivity implements View.OnTouchListener  {
    CameraView cameraKitView;
    private StickerBSFragment mStickerBSFragment;
    int number = 0, sec_passed = 0;
    ImageView facing, gallery, done_btn, addfilter, img_setting, img_timer;
    ArrayList<String> videopaths = new ArrayList<>();
    boolean is_recording = false;
    boolean is_flash_on = false;
    LottieAnimationView record;
    public static int Sounds_list_Request_code = 1;
    TextView add_sound_txt;
    SegmentedProgressBar video_progress;
    Context context;
    MediaPlayer audio;
    FloatingActionButton timer,speed;
    RelativeLayout recorder;
    LinearLayout lout_timer, addsound,lout_speed;
    TextView music,timelapes;
    String audiourl,draft_file,speed_video,timer_video;
    RelativeLayout screen;
    TextView timer30sec, timer60sec, timer90sec;
    //Filters filters[];
    ActivityResultLauncher launcher,launcher2;
    static int currentfilter = 0,videotime=30000;
    int code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        ((ImageView) findViewById(R.id.cancel)).setOnClickListener((View.OnClickListener) v -> {
            onBackPressed();
        });
        cameraKitView = findViewById(R.id.camera);
        cameraKitView.setLifecycleOwner(this);

        record = findViewById(R.id.record);
        facing = findViewById(R.id.facing);
        gallery = findViewById(R.id.gallery);
        done_btn = findViewById(R.id.done);
        music = findViewById(R.id.music);
        addsound = findViewById(R.id.addsound);
        addfilter = findViewById(R.id.addfilter);
        img_timer = findViewById(R.id.imgtimer);
        img_setting = findViewById(R.id.img_setting);
        recorder = findViewById(R.id.recorder);
        lout_timer = findViewById(R.id.lout_timer);
        lout_speed=findViewById(R.id.lout_speed);
        screen = findViewById(R.id.screen);
        timer = findViewById(R.id.timer);
        speed=findViewById(R.id.speed);
        timelapes=findViewById(R.id.timeprg);
        // timer30sec = findViewById(R.id.timer30sec);
        // timer60sec = findViewById(R.id.timer60sec);
        // timer90sec = findViewById(R.id.timer90sec);
        //   filters = Filters.values();
        speed_video="1f";
        video_progress = findViewById(R.id.video_progress);
        video_progress.enableAutoProgressView(videotime);
        takepermissions();
        Sharedhelper.putKey(RecordingActivity.this, "soundid", "0");

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getData() != null) {
                            if (result.getData().getStringExtra("isSelected").equals("yes")) {
                                //System.out.println(data.getStringExtra("sound_name"));
                                //System.out.println(data.getIntExtra("sound_id",0));
                                music.setText(result.getData().getStringExtra("sound_name"));
                                audiourl = result.getData().getStringExtra("sound_url");
                                Variables.Selected_sound_id = result.getData().getStringExtra("sound_id");
                                PreparedAudio();
                            }

                        }
                    }
                });
        launcher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri uri = result.getData().getData();
                            Intent intent = new Intent(RecordingActivity.this, TrimVideoActivity.class);
                            intent.putExtra("path",String.valueOf(uri) );
                            startActivity(intent);
                            //}
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lout_speed.setVisibility(View.VISIBLE);
                lout_timer.setVisibility(View.GONE);
                speed.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(RecordingActivity.this, R.color.bg)));
                timer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(RecordingActivity.this, R.color.pink)));

            }
        });
        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lout_timer.setVisibility(View.VISIBLE);
                lout_speed.setVisibility(View.GONE);
                timer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(RecordingActivity.this, R.color.bg)));
                speed.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(RecordingActivity.this, R.color.pink)));

            }
        });




        video_progress.setDividerColor(Color.WHITE);
        video_progress.setDividerEnabled(true);
        video_progress.setDividerWidth(4);
        video_progress.setShader(new int[]{Color.CYAN, Color.CYAN, Color.CYAN});


        // boolean a = Functions.Checkstoragepermision(RecordingActivity.this);
        findViewById(R.id.timer30sec).setBackgroundResource(R.drawable.selected_px);
        findViewById(R.id.timer30sec).setOnClickListener(v -> {
            timer_video="30s";
            videotime=30000;
            video_progress.enableAutoProgressView(videotime);

            findViewById(R.id.timer30sec).setBackgroundResource(R.drawable.selected_px);
            findViewById(R.id.timer60sec).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.timer90sec).setBackgroundResource(R.drawable.pixels_bg);
        });

        findViewById(R.id.timer60sec).setOnClickListener(v -> {
//            cameraKitView.setVideoBitRate(-1000);
            timer_video="60s";
            videotime=60000;
            video_progress.enableAutoProgressView(videotime);

            findViewById(R.id.timer60sec).setBackgroundResource(R.drawable.selected_px);
            findViewById(R.id.timer30sec).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.timer90sec).setBackgroundResource(R.drawable.pixels_bg);
        });
        findViewById(R.id.timer90sec).setOnClickListener(v -> {
            timer_video="90s";
            videotime=90000;
            video_progress.enableAutoProgressView(videotime);
            findViewById(R.id.timer90sec).setBackgroundResource(R.drawable.selected_px);
            findViewById(R.id.timer60sec).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.timer30sec).setBackgroundResource(R.drawable.pixels_bg);
        });



        context = RecordingActivity.this;
        //  boolean b = Functions.Checkstoragepermision(RecordingActivity.this);
        findViewById(R.id.three).setBackgroundResource(R.drawable.selected_px);
        findViewById(R.id.one).setOnClickListener(v -> {
//            cameraKitView.setVideoBitRate(-2000);
            speed_video="0.3f";
            findViewById(R.id.one).setBackgroundResource(R.drawable.selected_px);
            findViewById(R.id.two).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.three).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.four).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.five).setBackgroundResource(R.drawable.pixels_bg);
        });
        findViewById(R.id.two).setOnClickListener(v -> {
//            cameraKitView.setVideoBitRate(-1000);
            speed_video="0.6f";
            findViewById(R.id.two).setBackgroundResource(R.drawable.selected_px);
            findViewById(R.id.one).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.three).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.four).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.five).setBackgroundResource(R.drawable.pixels_bg);
        });
        findViewById(R.id.three).setOnClickListener(v -> {
//            cameraKitView.setVideoBitRate(0);
            speed_video="1f";
            findViewById(R.id.three).setBackgroundResource(R.drawable.selected_px);
            findViewById(R.id.one).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.two).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.four).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.five).setBackgroundResource(R.drawable.pixels_bg);
        });
        findViewById(R.id.four).setOnClickListener(v -> {
//            cameraKitView.setVideoBitRate(2000);
            speed_video="2f";
            findViewById(R.id.four).setBackgroundResource(R.drawable.selected_px);
            findViewById(R.id.one).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.two).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.three).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.five).setBackgroundResource(R.drawable.pixels_bg);
        });findViewById(R.id.five).setOnClickListener(v -> {
//            cameraKitView.setVideoBitRate(2000);
            speed_video="3f";
            findViewById(R.id.five).setBackgroundResource(R.drawable.selected_px);
            findViewById(R.id.one).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.two).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.three).setBackgroundResource(R.drawable.pixels_bg);
            findViewById(R.id.four).setBackgroundResource(R.drawable.pixels_bg);

        });
        done_btn.setOnClickListener(v -> {
            if (!is_recording && sec_passed > 5) {
                append();
                //  Intent intent = new Intent(context, SelectedVideoActivity.class);
                // intent.putExtra("video_path", Variables.gallery_resize_video);
                //startActivity(intent);
            }

        });
        addsound.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicActivity.class);
            //startActivityForResult(intent, Sounds_list_Request_code);
            launcher.launch(intent);
            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

        });
        cameraKitView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
            }


            @Override
            public void onVideoTaken(VideoResult result) {
            }

            @Override
            public void onVideoRecordingStart() {
                System.out.println("sup");
                video_progress.resume();
                record.setImageDrawable(getResources().getDrawable(R.drawable.ic_recoding_yes));
                offVisibility();

            }

            @Override
            public void onVideoRecordingEnd() {
                video_progress.pause();
                video_progress.addDivider();
                record.setImageDrawable(getResources().getDrawable(R.drawable.ic_recoding_no));
                if (sec_passed > 5) {
                    done_btn.setVisibility(View.VISIBLE);
                }
            }
        });


        video_progress.SetListener(new ProgressBarListener() {
            @Override
            public void TimeinMill(long mills) {
                sec_passed = (int) (mills / 1000);
                timelapes.setText(String.valueOf(sec_passed));

                if (mills > (videotime-1)) {
                    Start_or_Stop_Recording();
                }

            }
        });


        facing.setOnClickListener(v -> {
            cameraKitView.toggleFacing();
        });
        record.setOnClickListener(v -> {
            Log.d("VideoRecording","Start0");

            Start_or_Stop_Recording();
            //ashish
            music.setVisibility(View.GONE);
            addsound.setVisibility(View.GONE);
            facing.setVisibility(View.GONE);
            gallery.setVisibility(View.GONE);
            //ashish
        });

        gallery.setOnClickListener(v -> {
           /* Intent upload_intent = new Intent(this, VideoGalleryActivity.class);
            startActivity(upload_intent);
            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);*/

            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            launcher2.launch(intent);

            //startActivityForResult(intent, 210);


        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        video_progress.enableAutoProgressView(videotime);

    }

    public void Start_or_Stop_Recording() {

        if (!is_recording && sec_passed < (Integer.valueOf(videotime/1000))) {
            number = number + 1;
            is_recording = true;
            addsound.setClickable(false);
            Log.d("VideoRecording","Start1");
            File file = new File(Variables.app_hided_folder + "myvideo" + (number) + ".mp4");

            Log.d("VideoRecording","Start2");

            videopaths.add(Variables.app_hided_folder + "myvideo" + (number) + ".mp4");

            Log.d("VideoRecording","Start3");

            if (cameraKitView.isTakingVideo() || cameraKitView.isTakingPicture()) return;
            cameraKitView.takeVideo(file);
            Log.d("VideoRecording","Start4");



            if (audio != null)
                audio.start();


        } else if (is_recording) {
            is_recording = false;
            if (audio != null)
                audio.pause();
            cameraKitView.stopVideo();


        } else if (sec_passed > Integer.valueOf((videotime/1000))) {
            Functions.Show_Alert(this, "Alert", "Video only can be a ."+String.valueOf(Integer.valueOf((videotime/1000))));
        }


    }

    /*public void Go_To_preview_Activity() {
        finish();
        Intent intent = new Intent(this, PreviewVideoActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }*/
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, height, width, true);
    }

    public void Go_To_preview_Activity() {
        Intent intent = new Intent(context, PreviewVideoActivity.class);
        intent.putExtra("path", Variables.outputfile2);
        intent.putExtra("draft_file", draft_file);
        intent.putExtra("speed",speed_video);
        intent.putExtra("fromWhere", "video_recording");

        if (audio != null)
            intent.putExtra("isSoundSelected", "yes");
        context.startActivity(intent);


    }
    private boolean append() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.setMessage("Please wait..");
                        progressDialog.show();
                    }
                });

                ArrayList<String> video_list = new ArrayList<>();
                for (int i = 0; i < videopaths.size(); i++) {
                    File file = new File(videopaths.get(i));
                    if (file.exists()) {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(context, Uri.fromFile(file));
                        String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                        boolean isVideo = "yes".equals(hasVideo);
                        if (isVideo && file.length() > 3000) {
                            // Log.d("HatzOff", videopaths.get(i));
                            video_list.add(videopaths.get(i));
                        }
                    }
                }


                try {

                    Movie[] inMovies = new Movie[video_list.size()];

                    for (int i = 0; i < video_list.size(); i++) {
                        inMovies[i] = MovieCreator.build(video_list.get(i));

                    }
                    List<Track> videoTracks = new LinkedList<Track>();
                    List<Track> audioTracks = new LinkedList<Track>();
                    for (Movie m : inMovies) {
                        for (Track t : m.getTracks()) {
                            if (t.getHandler().equals("soun")) {
                                audioTracks.add(t);
                            }
                            if (t.getHandler().equals("vide")) {
                                videoTracks.add(t);
                            }
                        }
                    }

                    Movie result = new Movie();
                    if (audioTracks.size() > 0) {
                        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    }

                    Container out = new DefaultMp4Builder().build(result);

                    String outputFilePath = null;
                    if(cameraKitView.getFacing()== Facing.FRONT ){
                        outputFilePath=Variables.output_frontcamera;

                    }
                    else {
                        outputFilePath = Variables.outputfile2;
                    }
                   /* else {

                        if (audio != null) {
                        outputFilePath = Variables.outputfile;
                    } else {
                        outputFilePath = Variables.outputfile2;
                    }}*/

                    FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
                    out.writeContainer(fos.getChannel());
                    fos.close();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            if(cameraKitView.getFacing()== Facing.FRONT)
                            {
                                if(audio!=null) {
                                    Change_fliped_video(Variables.output_frontcamera, Variables.outputfile2);
                                }
                                else {
                                    Change_fliped_video(Variables.output_frontcamera, Variables.outputfile2);
                                }
                            }
                            else {
                                /*if (audio != null) {

                                    Merge_withAudio();
                                } else {*/
                                Go_To_preview_Activity();
                                // }
                            }
                        }
                    });
                    Log.d("HatzOff", "e");

                } catch (Exception e) {
//                    Log.d("HatzOff", e.getMessage());
                }
            }
        }).start();


        return true;
    }
    // ImageView record, facing, gallery,done_btn,addsound,addfilter;

    void offVisibility() {
        facing.setVisibility(View.GONE);
        //  gallery.setVisibility(View.GONE);
        // addsound.setVisibility(View.GONE);
        // addfilter.setVisibility(View.GONE);
        done_btn.setVisibility(View.VISIBLE);
        lout_timer.setVisibility(View.GONE);
        img_setting.setVisibility(View.GONE);
        img_timer.setVisibility(View.GONE);
    }

    void onVisibility() {
        facing.setVisibility(View.VISIBLE);
        gallery.setVisibility(View.VISIBLE);
        addsound.setVisibility(View.VISIBLE);
        addfilter.setVisibility(View.VISIBLE);
        lout_timer.setVisibility(View.VISIBLE);
        img_setting.setVisibility(View.VISIBLE);
        img_timer.setVisibility(View.VISIBLE);

    }
    /*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == Sounds_list_Request_code) {
                if (data != null) {
                    if (data.getStringExtra("isSelected").equals("yes")) {
                        //System.out.println(data.getStringExtra("sound_name"));
                        //System.out.println(data.getIntExtra("sound_id",0));
                        music.setText(data.getStringExtra("sound_name"));
                        audiourl = data.getStringExtra("sound_url");
                        Variables.Selected_sound_id = data.getStringExtra("sound_id");
                        PreparedAudio();
                    }

                }
            } else if (requestCode == 210) {
                try {
                    Uri uri = data.getData();
                    Intent intent = new Intent(RecordingActivity.this, TrimVideoActivity.class);
                    intent.putExtra("path",String.valueOf(uri) );
                    startActivity(intent);
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode==100){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if(Environment.isExternalStorageManager()){
                        Toast.makeText(this,"Permission Granted 11",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        takepermission();
                    }
                }
            }
        }*/
    public void Chnage_Video_size(String src_path, String destination_path) {

        try {
            Functions.copyFile(new File(src_path),
                    new File(destination_path),this);
            /*Intent intent = new Intent(RecordingActivity.this, GallerySelectedVideo_A.class);
            intent.putExtra("video_path", Variables.gallery_resize_video);
            startActivity(intent);*/

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Variables.tag, e.toString());
        }
    }

    public void PreparedAudio() {
        File file = new File(Variables.app_hided_folder + Variables.SelectedAudio);
        if (file.exists()) {
            audio = new MediaPlayer();
            try {
                audio.setDataSource(Variables.app_hided_folder + Variables.SelectedAudio);
                audio.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void Merge_withAudio() {

        String audio_file;
        audio_file = Variables.app_hided_folder + Variables.SelectedAudio;

        Merge_Sound merge_video_audio = new Merge_Sound(context);
        merge_video_audio.doInBackground(audio_file, Variables.outputfile, Variables.outputfile2,speed_video);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
    @Override
    protected void onDestroy() {
        Release_Resources();
        super.onDestroy();

    }
    public void Release_Resources(){
        try {

            if (audio != null) {
                audio.stop();
                audio.reset();
                audio.release();
            }
            cameraKitView.stopVideo();

        }catch (Exception e){

        }
        DeleteFile();
    }
    public void DeleteFile(){

        File output = new File(Variables.outputfile);
        File output2 = new File(Variables.outputfile2);

        File gallery_trimed_video = new File(Variables.gallery_trimed_video);
        File gallery_resize_video = new File(Variables.gallery_resize_video);


        if(output.exists()){
            output.delete();
        }

        if(output2.exists()){

            output2.delete();
        }


        if(gallery_trimed_video.exists()){
            gallery_trimed_video.delete();
        }

        if(gallery_resize_video.exists()){
            gallery_resize_video.delete();
        }

        for (int i=0;i<=12;i++) {

            File file = new File(Variables.app_hided_folder + "myvideo" + (i) + ".mp4");
            if (file.exists()) {
                file.delete();
            }

        }


    }
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("Are you Sure? if you Go back you can't undo this action")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        videotime=30000;
                        Release_Resources();

                        finish();
                        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

                    }
                }).show();


    }

    public void Change_fliped_video(String srcMp4Path, final String destMp4Path){

        Functions.Show_determinent_loader(this,false,false);
        new GPUMp4Composer(srcMp4Path, destMp4Path)
                .flipHorizontal(true)
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Log.d("resp",""+(int) (progress*100));
                        Functions.Show_loading_progress((int)(progress*100));
                    }

                    @Override
                    public void onCompleted() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Functions.cancel_determinent_loader();
                                //  if (audio != null)
                                //    Merge_withAudio();
                                //else {
                                Go_To_preview_Activity();
                                //}


                            }
                        });
                    }

                    @Override
                    public void onCanceled() {
                        Log.d("resp", "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {

                        Log.d("resp",exception.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Functions.cancel_determinent_loader();
                                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                                }catch (Exception e){
                                }
                            }
                        });
                    }
                })
                .start();
    }

    void takepermissions(){
        if(Checkstoragepermision()){
            //Toast.makeText(this,"Permission Already Granted",Toast.LENGTH_SHORT).show();
            makedirectory();

        }
        else
        {
            takepermission();
        }
    }
    public  boolean Checkstoragepermision(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //return Environment.isExternalStorageManager();
            return true;
        }
        else{
            int readExternalStoragePermission= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readExternalStoragePermission== PackageManager.PERMISSION_GRANTED;
        }
    }
    public  void takepermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            /*try{
                Intent intent=new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent,100);
            }
            catch(Exception e){
                Intent intent=new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent,100);
            }*/
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            if(requestCode==101){
                boolean readExternalstorage=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(readExternalstorage){
                    Toast.makeText(this,"Permission granted 10",Toast.LENGTH_SHORT).show();
                }
                else {
                    takepermission();
                }
            }
        }
    }
    void makedirectory()
    {
        File dir = new File(Variables.app_hided_folder);
        try{
            if (! dir.exists()) {
                dir.mkdir();
                Log.d("Recording","Directory created");
            }
            else{
                Log.d("Recording","Directory exist");

            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}