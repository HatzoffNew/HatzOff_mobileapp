package com.aliyahatzoff.Fragments;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Activities.VideoPlayActivity;
import com.aliyahatzoff.Adapters.VideoGalleryAdapter;
import com.aliyahatzoff.Models.VideoGallery;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Variables;

import java.io.File;
import java.util.ArrayList;

public class SaveVideoFragment extends Fragment {

    RecyclerView galleryRv;
    ArrayList<VideoGallery> data_list;
    VideoGalleryAdapter videoGalleryAdapter;
    public static final int PERMISSION_READ = 0;

    public SaveVideoFragment() {
        // Required empty public constructor
    }

    public static SaveVideoFragment newInstance(String param1, String param2) {
        SaveVideoFragment fragment = new SaveVideoFragment();
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
        View view= inflater.inflate(R.layout.fragment_save_video, container, false);
        galleryRv=view.findViewById(R.id.galley_rv);
        galleryRv.setLayoutManager(new GridLayoutManager(getContext(),3));
        data_list = new ArrayList<>();
        getAllVideoPath(getContext());

        videoGalleryAdapter = new VideoGalleryAdapter(getContext(), data_list,"local", new VideoGalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, VideoGallery item, View view) {
                Intent i=new Intent(getContext(), VideoPlayActivity.class);
                i.putExtra("video_path",item.getVideo_path());
                startActivity(i);
               /* MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Bitmap bmp = null;
                try {
                    retriever.setDataSource(item.getVideo_path());
                    bmp = retriever.getFrameAtTime();
                    int videoHeight = bmp.getHeight();
                    int videoWidth = bmp.getWidth();

                } catch (Exception e) {
                    System.out.println(e.getMessage());

                }
*/

            }
        });
        galleryRv.setAdapter(videoGalleryAdapter);

        return  view;
    }
    public void getAllVideoPath(Context context) {
        Functions.showLoadingDialog(getContext());
        File file=new File(Variables.draft_app_folder);
        File[] files=file.listFiles();

        if(files!=null) {
            if (files.length > 0) {
                for (int i = 0; i < files.length; ++i) {
                    VideoGallery item = new VideoGallery();
                    item.setVideo_path(Variables.draft_app_folder + files[i].getName());
                    long file_duration = getfileduration(Uri.parse(Variables.draft_app_folder + files[i].getName()));

                    if (file_duration > 5000 && file_duration < 19500) {
                        item.setVideo_time(change_sec_to_time(file_duration));
                        data_list.add(item);
                    }
                }
            }
        }
        Functions.cancelLoading();
    }
    public long getfileduration(Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(getContext(), uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            return file_duration;
        } catch (Exception e) {

        }
        return 0;
    }

    public String change_sec_to_time(long file_duration) {
        long second = (file_duration / 1000) % 60;
        long minute = (file_duration / (1000 * 60)) % 60;

        return String.format("%02d:%02d", minute, second);

    }

}