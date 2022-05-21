package com.aliyahatzoff.Api;

import com.aliyahatzoff.Models.Comment;
import com.aliyahatzoff.Models.DiscoverModel;
import com.aliyahatzoff.Models.MessageOtpModel;
import com.aliyahatzoff.Models.RecentModel;
import com.aliyahatzoff.Models.Soundlist;
import com.aliyahatzoff.Models.User;
import com.aliyahatzoff.Models.UserInfoModel;
import com.aliyahatzoff.Models.VideoItem;
import com.aliyahatzoff.Models.VideoPage;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("soundlist/{mtype}")
    Call<List<Soundlist>> getsoundlist(@Path("mtype") String musictype);

    @GET("videolist")
    Call<List<VideoItem>> getvideolist();

    @GET("videolist2")
    Call<VideoPage> getvideolist2(@Query("page") int page);
    @GET("videolist2")
    Call<VideoPage> get_trending(@Query("page") int page);
    @GET("videolist2")
    Call<VideoPage> get_recent(@Query("page") int page);


    @GET("uservideolist/{userid}")
    Call<List<VideoItem>> getuservideolist(@Path("userid") String userid);

    @GET("sharevideo/{videoid}")
    Call<ResponseBody> sharevideo(@Path("videoid") String videoid);

    @GET("likevideo/{videoid}")
    Call<ResponseBody> likevideo(@Path("videoid") String videoid);

    @GET("viewvideo/{videoid}/{macid}")
    Call<ResponseBody> viewvideo(@Path("videoid") String videoid, @Path("macid") String macid);

    @GET("deletepost/{videoid}")
    Call<ResponseBody> deletepost(@Path("videoid") String videoid);

    @GET("searchusers/{search}")
    Call<List<User>> searchuser(@Path("search") String search);

    @FormUrlEncoded
    @POST("commentvideo")
    Call<ResponseBody> comment(@Field("video_id") String videoid,
                               @Field("comment") String comment);

    @GET("getcomments/{videoid}")
    Call<List<Comment>> gecomment(@Path("videoid") String videoid);

    @GET("followvideo/{videoid}")
    Call<ResponseBody> followvideo(@Path("videoid") String videoid);


    @FormUrlEncoded
    @POST("createpost")
    Call<ResponseBody> createPost(@Field("hastagid") String hastag,
                                  @Field("soundid") int soundid,
                                  @Field("category") String category,
                                  @Field("caption") String caption,
                                  @Field("location") String location,
                                  @Field("comments") int comments,
                                  @Field("duet") int duet,
                                  @Field("isactive") String isactive,
                                  @Field("viewtype") String viewtype,
                                  @Field("filename") String filename,@Field("speed") String speed);

    @FormUrlEncoded
    @POST("userhcreate")
    Call<ResponseBody> createuser(@Field("name") String name,
                                  @Field("mobile") String mobile,
                                  @Field("signup_type") String signuptype);

    @GET("addinterest/{interest}")
    Call<ResponseBody> addinterest(@Path("interest") String interest);


    @FormUrlEncoded
    @POST("userhlogin")
    Call<User> loginuser(@Field("logintype") String logintype,
                         @Field("name") String name,
                         @Field("fbid") String fbid,
                         @Field("profilepic") String profilepic,
                         @Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("social_signup")
    Call<User> social_signUp(
            @Field("name") String name,
            @Field("email") String email,
            @Field("logintype") String logintype,
            @Field("fid") String fid,
            @Field("profilepic") String profilepic,
            @Field("deviceid") String deviceid);

    @POST("logout1")
    Call<ResponseBody> logout();

    @Multipart
    @POST("changepicture")
    Call<ResponseBody> changepic(@Part MultipartBody.Part filename);

    @GET("my_following")
    Call<List<User>> followings();

    @GET("my_followers")
    Call<List<User>> followersList();

    @GET("get_popular_UserVideo")
    Call<VideoPage> getpopularvideo();

    @FormUrlEncoded
    @POST("update_userinfo")
    Call<ResponseBody> updateUserInfo(@Field("name") String name,
                                      @Field("email") String email,
                                      @Field("mobile") String mobile,
                                      @Field("dob") String dob,
                                      @Field("category") String category);

    @GET("get_trending_Video")
    Call<List<DiscoverModel>> trendingList();

    @GET("get_recent_videoList")
    Call<List<RecentModel>> recentList();

    @FormUrlEncoded
    @POST("participate_incontest")
    Call<ResponseBody> participateForm(@Field("category") String category,
                                       @Field("amount") double amount);

    @GET("getuser_info")
    Call<UserInfoModel> userInfo();

    @FormUrlEncoded
    @POST("update_info")
    Call<ResponseBody> updateprofile(@Field("name") String name,
                                      @Field("email") String email,
                                      @Field("mobile") String mobile,
                                      @Field("dob") String dob,
                                      @Field("bio") String bio,
                                      @Field("userid") String userid);


    //===================================================================
    @GET("+91{users_phone_no}/AUTOGEN")
    Call<MessageOtpModel> requestOTPSMS(@Path("users_phone_no") String phone_no);

    @GET("VERIFY/{session_id}/{otp_entered_by_user}")
    Call<MessageOtpModel> verifyRequestedOTP(@Path("session_id") String session_id, @Path("otp_entered_by_user") String otp_entered_by_user);

//====================================================================


}
