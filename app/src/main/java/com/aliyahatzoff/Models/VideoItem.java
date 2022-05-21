package com.aliyahatzoff.Models;

import com.google.gson.annotations.SerializedName;

public class VideoItem {
    @SerializedName("id")
    int id;
    @SerializedName("user_id")
    int user_id;
    @SerializedName("name")
    String name;
    @SerializedName("profilepic")
    String profilepic;
    @SerializedName("interest")
    String interest;
    @SerializedName("follows")
    String follows;
    @SerializedName("following")
    String following;
    @SerializedName("hearts")
    String hearts;
    @SerializedName("speed")
    String speed;


    @SerializedName("caption")
    String caption;
    @SerializedName("category")
    String category;
    @SerializedName("fileurl")
    String fileurl;
    @SerializedName("diff")
    String gap;
    @SerializedName("soundname")
    String soundname;
    @SerializedName("soundid")
    int soundid;
    @SerializedName("signup_type")
    String signuptype;
    @SerializedName("like")
    int like;
    @SerializedName("totalview")
    int totalview;

    @SerializedName("comment")
    int comment;
    @SerializedName("share")
    int share;
    @SerializedName("follow")
    String follow;
    @SerializedName("islike")
    String islike;
    @SerializedName("isview")
    String isview;


    public String getInterest() {
        return interest;
    }

    public String getSpeed() {
        return speed;
    }

    public String getFollowing() {
        return following;
    }

    public String getHearts() {
        return hearts;
    }

    public String getFollows() {
        return follows;
    }

    public int getTotalview() {
        return totalview;
    }

    public String getIsview() {
        return isview;
    }

    public void setIsview(String isview) {
        this.isview = isview;
    }

    public String getIslike() {
        return islike;
    }

    public void setIslike(String islike) {
        this.islike = islike;
    }

    public String getFollow() {
        return follow;
    }

    public int getLike() {
        return like;
    }

    public int getComment() {
        return comment;
    }

    public int getShare() {
        return share;
    }

    public String getSignuptype() {
        return signuptype;
    }

    public String getName() {
        return name;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public String getSoundname() {
        return soundname;
    }

    public int getSoundid() {
        return soundid;
    }

    public String getGap() {
        return gap;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getCaption() {
        return caption;
    }

    public String getCategory() {
        return category;
    }

    public String getFileurl() {
        return fileurl;
    }
}
