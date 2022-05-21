package com.aliyahatzoff.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Sharedhelper;

public class SplashActivity extends AppCompatActivity {
    Intent in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Functions.setFullScreenView(this);
       // Sharedhelper.clearSharedPreferences(this);
       // System.out.println(Sharedhelper.getKey(this,"token"));

        if(Sharedhelper.getKey(this,"token").length()>10)
              in = new Intent(this, LandingActivity.class);
         else
            in = new Intent(this, OnBoardActivity.class);

            //in = new Intent(this, OnBoardActivity.class);

        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(in);
                    finish();
                }
            }
        };
        timer.start();
    }

}