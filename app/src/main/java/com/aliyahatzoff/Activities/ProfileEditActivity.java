package com.aliyahatzoff.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Sharedhelper;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AppCompatActivity {
    LinearLayout edit,follwers,populars;
Context context;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activites);
        mAuth = FirebaseAuth.getInstance();
        context=ProfileEditActivity.this;
        populars=findViewById(R.id.popular);
        populars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(ProfileEditActivity.this,MostPopular.class));
                Intent intent1 = new Intent(ProfileEditActivity.this, DiscoverPreviewActivity.class);
                intent1.putExtra("type", "mostpopular");
                startActivity(intent1);

            }
        });
        follwers=findViewById(R.id.follwers);
      follwers.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(ProfileEditActivity.this,FollwersActivities.class));
          }
      });
      edit=findViewById(R.id.edit_profile);
      edit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(ProfileEditActivity.this,EditProfileActivity.class));
          }
      });
        findViewById(R.id.lout_logout).setOnClickListener(v -> {
            Functions.showLoadingDialog(context);
            Common.getHatzoffApi().logout().enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful())
                    {

                        if(Sharedhelper.getKey(ProfileEditActivity.this,"ltype").equals("Twitter"))
                        {
                            TwitterAuthConfig mTwitterAuthConfig = new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                                    getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET));
                            TwitterConfig twitterConfig = new TwitterConfig.Builder(ProfileEditActivity.this)
                                    .twitterAuthConfig(mTwitterAuthConfig)
                                    .build();
                            Twitter.initialize(twitterConfig);

                            SessionManager<TwitterSession> sessionManager = TwitterCore.getInstance().getSessionManager();
                            if (sessionManager.getActiveSession() != null){
                                sessionManager.clearActiveSession();
                                mAuth.signOut();
                            }
                        }
                        if(Sharedhelper.getKey(ProfileEditActivity.this,"ltype").equals("Facebook")) {
                            LoginManager.getInstance().logOut();
                        }
                        Sharedhelper.clearSharedPreferences(context);
                        Functions.cancelLoading();
                        startActivity(new Intent(ProfileEditActivity.this,SplashActivity.class));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Functions.cancelLoading();
                }
            });
        });

    }

     public void onBackPressed(View view) {
        super.onBackPressed();
        finish();
    }
}