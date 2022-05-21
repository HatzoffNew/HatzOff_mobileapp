package com.aliyahatzoff.Models;

import com.google.gson.annotations.SerializedName;

public class RecentModel {

@SerializedName("id")

private Integer id;
@SerializedName("user_id")

private Integer userId;
@SerializedName("caption")

private String caption;
@SerializedName("category")

private String category;
@SerializedName("fileurl")

private String fileurl;
@SerializedName("name")

private String name;
@SerializedName("profilepic")

private String profilepic;
@SerializedName("soundid")

private Integer soundid;
@SerializedName("soundname")

private String soundname;
@SerializedName("signup_type")

private String signupType;
@SerializedName("upload_date")

private String uploadDate;
@SerializedName("upload_time")

private String uploadTime;
@SerializedName("diff")

private String diff;
@SerializedName("like")

private Integer like;
@SerializedName("comment")

private Integer comment;
@SerializedName("share")

private Integer share;
@SerializedName("totalview")

private Integer totalview;
@SerializedName("follow")

private String follow;
@SerializedName("islike")

private String islike;
@SerializedName("isview")

private String isview;

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
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

    public String getName() {
        return name;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public Integer getSoundid() {
        return soundid;
    }

    public String getSoundname() {
        return soundname;
    }

    public String getSignupType() {
        return signupType;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public String getDiff() {
        return diff;
    }

    public Integer getLike() {
        return like;
    }

    public Integer getComment() {
        return comment;
    }

    public Integer getShare() {
        return share;
    }

    public Integer getTotalview() {
        return totalview;
    }

    public String getFollow() {
        return follow;
    }

    public String getIslike() {
        return islike;
    }

    public String getIsview() {
        return isview;
    }
}