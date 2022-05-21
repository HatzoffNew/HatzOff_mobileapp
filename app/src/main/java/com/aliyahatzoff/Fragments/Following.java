package com.aliyahatzoff.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Adapters.FollowersAdapter;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.User;
import com.aliyahatzoff.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Following extends Fragment {
RecyclerView followingRv;
List<User> followingsModelList=new ArrayList<>();
    public Following() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        followingRv=view.findViewById(R.id.following);
        followingRv.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        getData();
        return view;
    }

    private void getData() {
        Common.getHatzoffApi().followings().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    followingsModelList=response.body();
                    FollowersAdapter followersAdapter=new FollowersAdapter(getContext(),followingsModelList);
                    followingRv.setAdapter(followersAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }

}
