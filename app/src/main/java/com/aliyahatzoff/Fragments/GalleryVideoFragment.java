package com.aliyahatzoff.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Activities.VideoPlayActivity;
import com.aliyahatzoff.Adapters.VideoGalleryAdapter;
import com.aliyahatzoff.Models.VideoGallery;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class GalleryVideoFragment extends Fragment {
RecyclerView galleryRv;
    ArrayList<VideoGallery> data_list;
    VideoGalleryAdapter videoGalleryAdapter;
    public static final int PERMISSION_READ = 0;

    public GalleryVideoFragment() {
    }

    public static GalleryVideoFragment newInstance(String param1, String param2) {
        GalleryVideoFragment fragment = new GalleryVideoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_gallery_video, container, false);
        galleryRv=view.findViewById(R.id.galley_rv);
        galleryRv.setLayoutManager(new GridLayoutManager(getContext(),3));
        data_list = new ArrayList<>();
        getAllVideoPath(getContext());

        videoGalleryAdapter = new VideoGalleryAdapter(getContext(), data_list,"local", new VideoGalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, VideoGallery item, View view) {
                Intent i=new Intent(getContext(),VideoPlayActivity.class);
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

        return view;
    }

    public void getAllVideoPath(Context context) {
        Functions.showLoadingDialog(getContext());
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.VideoColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        //int vidsCount = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                VideoGallery item = new VideoGallery();
                item.setVideo_path(cursor.getString(0));
                long file_duration = getfileduration(Uri.parse(cursor.getString(0)));

                if (file_duration > 5000 && file_duration < 19500) {
                    item.setVideo_time(change_sec_to_time(file_duration));
                    data_list.add(item);
                }

            }
          //  videoGalleryAdapter.notifyDataSetChanged();
            cursor.close();

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

    public void Chnage_Video_size(VideoGallery item) {

        Functions.Show_determinent_loader(getContext(), false, false);

    }
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
                    } else {
             //           videoList();
                    }
                }
            }
        }
    }

/*
    public void videoList() {
        System.out.println("video");
        galleryRv.setItemAnimator(new DefaultItemAnimator());
        videoArrayList = new ArrayList<>();
        getVideos();
    }

    private void getVideos() {
        /*ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                VideoModel  videoModel  = new VideoModel ();
                videoModel .setVideoTitle(title);
                videoModel .setVideoUri(Uri.parse(data));
                videoModel .setVideoDuration(timeConversion(Long.parseLong(duration)));
                videoArrayList.add(videoModel);

            } while (cursor.moveToNext());
        }
        String[] proj = { MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION };
        cursor =getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                proj, null, null, null);
        int count = cursor.getCount();
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                System.out.println(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
               // Bitmap bitmap= ThumbnailUtils.createVideoThumbnail(data,MediaStore.Video.Thumbnails.MINI_KIND);
                VideoModel  videoModel  = new VideoModel ();
                videoModel .setVideoTitle(title);
               // videoModel.setBitmap(bitmap);
                videoModel .setVideoUri(Uri.parse(data));
                videoModel .setVideoDuration(timeConversion(Long.parseLong(duration)));
                videoArrayList.add(videoModel);

            } while (cursor.moveToNext());
        }

        GalleryVideoAdapter adapter = new GalleryVideoAdapter(getContext(), videoArrayList, new GalleryVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                Intent intent = new Intent(getApplicationContext(), VideoPlayActivity.class);
                intent.putExtra("pos",String.valueOf(pos));
                startActivity(intent);
            }
        });
        galleryRv.setAdapter(adapter);

    }
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
                    } else {
                        videoList();
                    }
                }
            }
        }
    }
    private String timeConversion(long parseLong) {
        String videoTime;
        int dur = (int) parseLong;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;
    }*/
}