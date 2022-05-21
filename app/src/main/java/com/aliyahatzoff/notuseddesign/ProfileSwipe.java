package com.aliyahatzoff.notuseddesign;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.aliyahatzoff.R;
import com.google.android.material.tabs.TabLayout;

public class ProfileSwipe extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tableLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_swipe);
        // toolbar =(Toolbar) findViewById(R.id.toolbar);
        tableLayout=(TabLayout) findViewById(R.id.tablayout);
        viewPager=(ViewPager) findViewById(R.id.viewpager);
        setSupportActionBar( toolbar);
        setupviewpager(viewPager);
        tableLayout.setupWithViewPager(viewPager);
    }

    private void setupviewpager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new Gallery(),"Posts");
        viewPagerAdapter.addFragment(new Gallery(),"Gallery");
        viewPagerAdapter.addFragment(new Gallery(),"Gallery");
        viewPager.setAdapter(viewPagerAdapter);

    }
}