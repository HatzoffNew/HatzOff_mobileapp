package com.aliyahatzoff.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.aliyahatzoff.Fragments.DiscoverMusic;
import com.aliyahatzoff.Fragments.FavouriteSongs;
import com.aliyahatzoff.Fragments.LocalMusic;
import com.aliyahatzoff.R;
import com.aliyahatzoff.notuseddesign.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MusicActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tableLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        // toolbar =(Toolbar) findViewById(R.id.toolbar);
        tableLayout=(TabLayout) findViewById(R.id.tablayout);
        viewPager=(ViewPager) findViewById(R.id.viewpager);
        setSupportActionBar( toolbar);
        setupviewpager(viewPager);
        tableLayout.setupWithViewPager(viewPager);
    }
    private void setupviewpager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new DiscoverMusic(),"Discover");
        viewPagerAdapter.addFragment(new LocalMusic(),"Local");
        viewPagerAdapter.addFragment(new FavouriteSongs(),"Favorite");
        viewPager.setAdapter(viewPagerAdapter);
    }
}