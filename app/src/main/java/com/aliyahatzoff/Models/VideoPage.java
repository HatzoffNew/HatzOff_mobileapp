package com.aliyahatzoff.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoPage {
    @SerializedName("current_page")
    int current_page;
    @SerializedName("last_page")
    int last_page;
    @SerializedName("total")
    int total;
    @SerializedName("to")
    int to;
    @SerializedName("data")
    List<VideoItem> videoItems;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public List<VideoItem> getVideoItems() {
        return videoItems;
    }

    public void setVideoItems(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
    }
}
