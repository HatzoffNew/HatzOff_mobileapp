package com.aliyahatzoff.Models;

import com.google.gson.annotations.SerializedName;

public class FollowingsModel {

@SerializedName("id")

private Integer id;
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

private Object birthday;
@SerializedName("firebaseid")

private Object firebaseid;
@SerializedName("interest")

private String interest;
@SerializedName("otp")

private Object otp;
@SerializedName("user_id")

private Integer userId;
@SerializedName("followby")

private Integer followby;
@SerializedName("startdt")

private String startdt;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getEmail() {
return email;
}

public void setEmail(String email) {
this.email = email;
}

public Object getEmailVerifiedAt() {
return emailVerifiedAt;
}

public void setEmailVerifiedAt(Object emailVerifiedAt) {
this.emailVerifiedAt = emailVerifiedAt;
}

public String getCreatedAt() {
return createdAt;
}

public void setCreatedAt(String createdAt) {
this.createdAt = createdAt;
}

public String getUpdatedAt() {
return updatedAt;
}

public void setUpdatedAt(String updatedAt) {
this.updatedAt = updatedAt;
}

public String getIsactive() {
return isactive;
}

public void setIsactive(String isactive) {
this.isactive = isactive;
}

public String getMobile() {
return mobile;
}

public void setMobile(String mobile) {
this.mobile = mobile;
}

public String getSignupType() {
return signupType;
}

public void setSignupType(String signupType) {
this.signupType = signupType;
}

public Object getDeviceid() {
return deviceid;
}

public void setDeviceid(Object deviceid) {
this.deviceid = deviceid;
}

public Object getGender() {
return gender;
}

public void setGender(Object gender) {
this.gender = gender;
}

public String getProfilepic() {
return profilepic;
}

public void setProfilepic(String profilepic) {
this.profilepic = profilepic;
}

public Object getBirthday() {
return birthday;
}

public void setBirthday(Object birthday) {
this.birthday = birthday;
}

public Object getFirebaseid() {
return firebaseid;
}

public void setFirebaseid(Object firebaseid) {
this.firebaseid = firebaseid;
}

public String getInterest() {
return interest;
}

public void setInterest(String interest) {
this.interest = interest;
}

public Object getOtp() {
return otp;
}

public void setOtp(Object otp) {
this.otp = otp;
}

public Integer getUserId() {
return userId;
}

public void setUserId(Integer userId) {
this.userId = userId;
}

public Integer getFollowby() {
return followby;
}

public void setFollowby(Integer followby) {
this.followby = followby;
}

public String getStartdt() {
return startdt;
}

public void setStartdt(String startdt) {
this.startdt = startdt;
}

}