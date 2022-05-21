package com.aliyahatzoff.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aliyahatzoff.Adapters.FragmentAdapter;
import com.aliyahatzoff.Fragments.PostVideoFragment;
import com.aliyahatzoff.R;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class OthersProfile extends AppCompatActivity {
TextView txt_username,txt_profession,follow,txt_following,txt_follows,txt_heart;
ImageView arrow,profilepic;
    String secret, access, bucket,imagefile,imageFilePath;
    AmazonS3Client s3Client;
    GeneratePresignedUrlRequest request;
    URL objectURL;
    ViewPager viewPager;
    TabLayout tl;
    ArrayList<Fragment> fragments=new ArrayList<>();

LinearLayout lytfollow,lytfollowed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        txt_username=findViewById(R.id.txt_username);
        profilepic=findViewById(R.id.profilepic);
        txt_profession=findViewById(R.id.txt_profession);
        txt_heart=findViewById(R.id.txt_likes);
        txt_follows=findViewById(R.id.txt_follows);
        txt_following=findViewById(R.id.txt_following);
        tl=(TabLayout) findViewById(R.id.uservideo);

        viewPager=findViewById(R.id.viewpager);
        Fragment postvideos=PostVideoFragment.newInstance(getIntent().getStringExtra("userid"));
        fragments.add(postvideos);

        FragmentAdapter pageAdapter=new FragmentAdapter(getSupportFragmentManager(),OthersProfile.this,fragments);
        viewPager.setAdapter(pageAdapter);
        tl.setupWithViewPager(viewPager);
        tl.getTabAt(0).setText("Posted Video");


        txt_username.setText(getIntent().getStringExtra("username"));
        txt_profession.setText(getIntent().getStringExtra("interest"));
        imagefile=getIntent().getStringExtra("profilepic");
        txt_follows.setText(getIntent().getStringExtra("follows"));
        txt_following.setText(getIntent().getStringExtra("following"));
        txt_heart.setText(getIntent().getStringExtra("like"));

        follow=findViewById(R.id.follow);
        lytfollow=(LinearLayout)findViewById(R.id.lytfollow);
        lytfollowed=(LinearLayout)findViewById(R.id.lytfollowed);
        secret = getResources().getString(R.string.s3_secret);
        access = getResources().getString(R.string.s3_access_key);
        bucket = getResources().getString(R.string.s3_bucket);
        BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
        s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion("ap-south-1"));

        if (imagefile.contains("http"))
            imageFilePath = imagefile;
        else {
            //request = new GeneratePresignedUrlRequest(bucket,imagefile);
            //objectURL = s3Client.generatePresignedUrl(request);
            imageFilePath = getResources().getString(R.string.cloudfront)+"/"+imagefile;
        }
        try {
            Picasso.get().
                    load(imageFilePath)
                    .resize(150, 150)
                    .placeholder(getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .into(profilepic);

        } catch (Exception e) {

        }




        lytfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details();
            }
        });

        lytfollowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               details();

            }
        });

    }

    public void onBackPressed(View view) {
        finish();
    }
    void details() {
        startActivity(new Intent(OthersProfile.this, FollwersActivities.class));
    }

}