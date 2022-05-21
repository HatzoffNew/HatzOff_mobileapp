package com.aliyahatzoff.Models;

import com.google.gson.annotations.SerializedName;
public class Soundlist {
    @SerializedName("id")
    int id;
    @SerializedName("mp3path")
    String mp3path;
    @SerializedName("soundname")
    String soundname;
    @SerializedName("fileurl")
    String fileurl;
    @SerializedName("thumb")
    String thumb;

    public String getThumb() {
        return thumb;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMp3path(String mp3path) {
        this.mp3path = mp3path;
    }

    public void setSoundname(String soundname) {
        this.soundname = soundname;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getId() {
        return id;
    }

    public String getFileurl() {
        return fileurl;
    }

    public String getMp3path() {
        return mp3path;
    }

    public String getSoundname() {
        return soundname;
    }
}
