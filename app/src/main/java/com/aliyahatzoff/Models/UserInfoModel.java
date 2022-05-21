package com.aliyahatzoff.Models;

import com.google.gson.annotations.SerializedName;

public class UserInfoModel {
    @SerializedName("id")

    private Integer id;

    @SerializedName("userid")
    private String userid;

    @SerializedName("bio")
    private String bio;

    @SerializedName("name")
    private String name;
    @SerializedName("email")

    private String email;
    @SerializedName("email_verified_at")

    private Object emailVerifiedAt;
    @SerializedName("created_at")

    private String createdAt;
    @SerializedName("updated_at")

    private String updatedAt;
    @SerializedName("isactive")

    private String isactive;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("signup_type")

    private String signupType;
    @SerializedName("deviceid")

    private Object deviceid;
    @SerializedName("gender")

    private Object gender;
    @SerializedName("profilepic")

    private String profilepic;
    @SerializedName("birthday")

    private String birthday;
    @SerializedName("firebaseid")

    private Object firebaseid;
    @SerializedName("interest")

    private String interest;
    @SerializedName("otp")

    private Object otp;
    @SerializedName("paid")

    private String paid;

    public Integer getId() {
        return id;
    }

    public String getUserid() {
        return userid;
    }

    public String getBio() {
        return bio;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Object getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getIsactive() {
        return isactive;
    }

    public String getMobile() {
        return mobile;
    }

    public String getSignupType() {
        return signupType;
    }

    public Object getDeviceid() {
        return deviceid;
    }

    public Object getGender() {
        return gender;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public String getBirthday() {
        return birthday;
    }

    public Object getFirebaseid() {
        return firebaseid;
    }

    public String getInterest() {
        return interest;
    }

    public Object getOtp() {
        return otp;
    }

    public String getPaid() {
        return paid;
    }
}
