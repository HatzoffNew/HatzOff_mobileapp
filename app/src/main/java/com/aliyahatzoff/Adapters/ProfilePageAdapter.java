package com.aliyahatzoff.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aliyahatzoff.Fragments.GalleryVideoFragment;
import com.aliyahatzoff.Fragments.PostVideoFragment;
import com.aliyahatzoff.Fragments.SaveVideoFragment;

public class ProfilePageAdapter extends FragmentPagerAdapter {
    private Fragment[] childFragments;

    public ProfilePageAdapter(FragmentManager fm) {
        super(fm);
        childFragments = new Fragment[] {
                new PostVideoFragment(), //0
                new GalleryVideoFragment(), //1
                new SaveVideoFragment() //2
        };
    }

    @Override
    public Fragment getItem(int position) {
        return childFragments[position];
    }

    @Override
    public int getCount() {
        return childFragments.length; //3 items
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = getItem(position).getClass().getName();
        return title.subSequence(title.lastIndexOf(".") + 1, title.length());
    }
}
