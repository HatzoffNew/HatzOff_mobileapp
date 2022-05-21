package com.aliyahatzoff.Models;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String username;
    @SerializedName("comment")
    String comment;
    @SerializedName("profilepic")
    String profilepic;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public String getProfilepic() {
        return profilepic;
    }
}
