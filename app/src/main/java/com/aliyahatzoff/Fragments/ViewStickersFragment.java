package com.aliyahatzoff.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Activities.PreviewVideoActivity;
import com.aliyahatzoff.Adapters.StickerAdapter;
import com.aliyahatzoff.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
public class ViewStickersFragment extends Fragment implements
        StickerAdapter.RecyclerViewClickListener,
        View.OnClickListener
{

    private static final String TAG = "ViewStickersFragment";
    private static final int NUM_COLUMNS = 3;

    //widgets
    private RecyclerView mRecyclerView;

    //vars
    private ArrayList<Drawable> mStickers = new ArrayList<>();
    private StickerAdapter mStickerAdapter;
    public static ViewStickersFragment newInstance() {
        return new ViewStickersFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_stickers, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        getStickers();
        initRecyclerView();
        return view;
    }
    private void initRecyclerView(){
        if(mStickerAdapter == null){
            mStickerAdapter = new StickerAdapter(getActivity(), mStickers , this);
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), NUM_COLUMNS));
        mRecyclerView.setAdapter(mStickerAdapter);
    }
    private void getStickers(){
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.cry_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.wow));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.emoji_270c_1f3fc));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.wow));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.cry_emoji));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.wow));
        mStickers.add(getActivity().getResources().getDrawable(R.drawable.birthday));
    }
    @Override
    public void onStickerClicked(int position) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),position);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        Intent intent = new Intent(getActivity(), PreviewVideoActivity.class);
        intent.putExtra("sticker", b);
        startActivity(intent);
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

        }
    }
}