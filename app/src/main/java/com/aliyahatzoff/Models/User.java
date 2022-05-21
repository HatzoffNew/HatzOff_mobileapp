package com.aliyahatzoff.Models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("email")
    String email;
    @SerializedName("mobile")
    String mobile;
    @SerializedName("signup_type")
    String signup_type;

    @SerializedName("profilepic")
    String profile_pic;
    @SerializedName("fbid")
    String fbid;
    @SerializedName("googleid")
    String googleid;
    @SerializedName("twitid")
    String twitid;
    @SerializedName("token")
    String token;

    @SerializedName("interest")
    String interest;

    @SerializedName("follow")
    String follow;

    @SerializedName("like")
    String like;

    @SerializedName("following")
    String following;

    public String getFollowing() {
        return following;
    }

    public String getInterest() {
        return interest;
    }

    public String getFollow() {
        return follow;
    }

    public String getLike() {
        return like;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getSignup_type() {
        return signup_type;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getFbid() {
        return fbid;
    }

    public String getGoogleid() {
        return googleid;
    }

    public String getTwitid() {
        return twitid;
    }

    public String getToken() {
        return token;
    }
}
