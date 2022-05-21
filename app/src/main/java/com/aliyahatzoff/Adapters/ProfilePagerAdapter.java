package com.aliyahatzoff.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aliyahatzoff.Fragments.GalleryVideoFragment;
import com.aliyahatzoff.Fragments.MyPostsFragment;
import com.aliyahatzoff.Fragments.SaveVideoFragment;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    public ProfilePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MyPostsFragment paymentFragment = new MyPostsFragment();
                return paymentFragment;

                case 1:
                GalleryVideoFragment galleryVideoFragment = new GalleryVideoFragment();
                return galleryVideoFragment;
            case 2:
                SaveVideoFragment saveVideoFragment = new SaveVideoFragment();
                return saveVideoFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}

