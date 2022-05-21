package com.aliyahatzoff.Models;

import com.google.gson.annotations.SerializedName;

public class DiscoverModel {

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
}