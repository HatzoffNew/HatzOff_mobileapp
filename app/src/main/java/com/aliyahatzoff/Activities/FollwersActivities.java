package com.aliyahatzoff.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aliyahatzoff.Adapters.FragmentAdapter;
import com.aliyahatzoff.Fragments.Followers;
import com.aliyahatzoff.Fragments.Following;
import com.aliyahatzoff.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class FollwersActivities extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tableLayout;
    private ViewPager viewPager;
    ArrayList<Fragment> fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follwers_activities);
        // toolbar =(Toolbar) findViewById(R.id.toolbar);
        tableLayout=(TabLayout) findViewById(R.id.followestab);
        viewPager=(ViewPager) findViewById(R.id.viewpager);
        setSupportActionBar( toolbar);
        fragments=new ArrayList<>();
        fragments.add(new Followers());
        fragments.add(new Following());
        FragmentAdapter pageAdapter=new FragmentAdapter(getSupportFragmentManager(),FollwersActivities.this,fragments);
        viewPager.setAdapter(pageAdapter);
        tableLayout.setupWithViewPager(viewPager);
        tableLayout.getTabAt(0).setText("Followers");
        tableLayout.getTabAt(1).setText("Following");

    }


}