package com.aliyahatzoff.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Adapters.MyPostsAdapter;
import com.aliyahatzoff.R;

public class MyPostsFragment extends Fragment {
RecyclerView postsRv;


    public MyPostsFragment() {
        // Required empty public constructor
    }


    public static MyPostsFragment newInstance(String param1, String param2) {
        MyPostsFragment fragment = new MyPostsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_posts, container, false);
        postsRv=view.findViewById(R.id.my_posts_rv);
        postsRv.setLayoutManager(new GridLayoutManager(getContext(),3));
        MyPostsAdapter myPostsAdapter=new MyPostsAdapter(getContext());
        postsRv.setAdapter(myPostsAdapter);
        return view;
    }
}