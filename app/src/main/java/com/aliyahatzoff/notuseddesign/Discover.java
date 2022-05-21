package com.aliyahatzoff.notuseddesign;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aliyahatzoff.R;
import com.google.android.material.tabs.TabLayout;
import com.luseen.spacenavigation.SpaceNavigationView;

public class Discover extends Fragment {
    private Toolbar toolbar;
    private TabLayout tableLayout;
    private ViewPager viewPager;
    private ImageView camera;
    SpaceNavigationView navigationView;

    public Discover() {
        // Required empty public constructor
    }


    public static Discover newInstance(String param1, String param2) {
        Discover fragment = new Discover();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.discover_activites, container, false);
        tableLayout = (TabLayout) view.findViewById(R.id.tablayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //  camera = (ImageView)view.findViewById(R.id.camera);
        // navigationView = findViewById(R.id.space);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setupviewpager(viewPager);
        tableLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupviewpager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(((AppCompatActivity)getActivity()).getSupportFragmentManager());
        viewPagerAdapter.addFragment(new Gallery(), "Posts");
        viewPagerAdapter.addFragment(new Gallery(), "Gallery");
        viewPagerAdapter.addFragment(new Gallery(), "saved");
        viewPagerAdapter.addFragment(new Gallery(), "saved");
        viewPagerAdapter.addFragment(new Gallery(), "saved");
        viewPagerAdapter.addFragment(new Gallery(), "saved");
        viewPagerAdapter.addFragment(new Gallery(), "saved");
        viewPagerAdapter.addFragment(new Gallery(), "saved");
        viewPagerAdapter.addFragment(new Gallery(), "saved");
        viewPagerAdapter.addFragment(new Gallery(), "saved");
        viewPagerAdapter.addFragment(new Gallery(), "saved");
        viewPager.setAdapter(viewPagerAdapter);

    }
}