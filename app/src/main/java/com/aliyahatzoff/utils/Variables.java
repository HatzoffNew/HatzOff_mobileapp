package com.aliyahatzoff.utils;

import android.content.SharedPreferences;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Variables {
    public static String app_version="1.0";
    public static String device="android";
    public static String TAG="Hatzoff";
    public static int max_recording_duration=54000;
    public static String tag="Hatzoff_";


    public static int screen_width;
    public static int screen_height;

    public static String SelectedAudio="SelectedAudio.aac";

    //public static String root= Environment.getExternalStorageDirectory().toString();

//    public static String root=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    public static String root=MyApplication.getAppContext().getFilesDir().toString();
    public static final String app_hided_folder =root+"/.HidedHatzoff/";
    public static final String app_showing_folder =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    public static final String draft_app_folder= app_hided_folder +"Draft/";


    public static final String uploading_video_thumb="uploading_video_thumb";


    public static String file_provider_path="com.hatzoff.fileprovider";

    public static String outputfile= app_hided_folder + "output.mp4";
    public static String watermark= app_hided_folder + "watermark.png";

    public static String stikerfile= app_hided_folder + "stiker.png";

    public static String outputfile2= app_hided_folder + "output2.mp4";
    public static String tempfile= app_hided_folder + "temp.mp4";
    public static String output_frontcamera= app_hided_folder + "output_frontcamera.mp4";

    public static String output_filter_file= app_hided_folder + "output-filtered.mp4";
    public static String gallery_trimed_video= app_hided_folder + "gallery_trimed_video.mp4";
    public static String gallery_resize_video= app_hided_folder + "gallery_resize_video.mp4";



    public static SharedPreferences sharedPreferences;
    public static String pref_name="pref_name";
    public static String u_id="u_id";
    public static String u_name="u_name";
    public static String u_pic="u_pic";
    public static String f_name="f_name";
    public static String l_name="l_name";
    public static String gender="u_gender";
    public static String islogin="is_login";
    public static String device_token="device_token";




    public static String Selected_sound_id="0";


    public static String gif_firstpart="https://media.giphy.com/media/";
    public static String gif_secondpart="/100w.gif";

    public static String gif_firstpart_chat="https://media.giphy.com/media/";
    public static String gif_secondpart_chat="/200w.gif";


    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);




    public static String user_id;
    public static String user_name;
    public static String user_pic;


    public static int permission_camera_code=786;
    public static int permission_write_data=788;
    public static int permission_Read_data=789;
    public static int permission_Recording_audio=790;





    public static String gif_api_key1="giphy_api_key_here";




    public static String domain="http//domain_url_of_api.com";
    public static String base_url="http//image_base_url.com";





    public static String SignUp =domain+"signup";
    public static String uploadVideo =domain+"uploadVideo";
    public static String showAllVideos =domain+"showAllVideos";
    public static String showMyAllVideos=domain+"showMyAllVideos";
    public static String likeDislikeVideo=domain+"likeDislikeVideo";

    public static String updateVideoView=domain+"updateVideoView";

    public static String allSounds=domain+"allSounds";
    public static String fav_sound=domain+"fav_sound";
    public static String my_FavSound=domain+"my_FavSound";
    public static String my_liked_video=domain+"my_liked_video";
    public static String follow_users=domain+"follow_users";


    public static String discover=domain+"discover";


    public static String showVideoComments=domain+"showVideoComments";
    public static String postComment=domain+"postComment";

    public static String edit_profile=domain+"edit_profile";
    public static String get_user_data=domain+"get_user_data";


    public static String get_followers=domain+"get_followers";
    public static String get_followings=domain+"get_followings";

    public static String SearchByHashTag=domain+"SearchByHashTag";


}
