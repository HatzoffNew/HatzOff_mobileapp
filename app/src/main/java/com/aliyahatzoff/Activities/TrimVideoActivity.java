package com.aliyahatzoff.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.FileUtils;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Variables;

import java.io.File;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

public class TrimVideoActivity extends AppCompatActivity implements OnTrimVideoListener {
    private K4LVideoTrimmer mVideoTrimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim_video);
        Intent extraIntent = getIntent();
        String path = "";Uri video=null;
try {
    if (extraIntent != null) {
        path = extraIntent.getStringExtra("path");
        //    video = Uri.parse(FileUtils.getPath(this,Uri.parse(path)));
        video = Uri.parse(path);
        File video_file = FileUtils.getFileFromUri(this, video);
        video = Uri.fromFile(video_file);


    }
}catch (Exception e){}

        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {
            mVideoTrimmer.setVideoURI(video);
            mVideoTrimmer.setMaxDuration((Variables.max_recording_duration/1000));
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setDestinationPath(Variables.app_showing_folder+"/");
        }

    }

    @Override
    public void onTrimStarted() {
        Functions.showLoadingDialog(this);
    }

    @Override
    public void getResult(Uri uri) {
        Functions.cancelLoading();
        try {
            File video_file = new File(uri.getPath());
            Log.d(Variables.tag+" new path",video_file.getAbsolutePath());
            Functions.copyFile(video_file,new File(Variables.gallery_resize_video),this);
            if(video_file.exists()){
               // video_file.delete();
            }

            Intent intent=new Intent(TrimVideoActivity.this, PreviewVideoActivity.class);
            intent.putExtra("path", Variables.gallery_resize_video);
            intent.putExtra("speed","1f");
            startActivity(intent);
            mVideoTrimmer.destroy();
            finish();

        }catch (Exception e) {
            e.printStackTrace();
            Log.d(Variables.tag,e.toString());
        }


    }

    @Override
    public void cancelAction() {
        Functions.cancelLoading();
        mVideoTrimmer.destroy();
        finish();

    }

    @Override
    public void onError(String message) {
Functions.cancelLoading();
    }
}