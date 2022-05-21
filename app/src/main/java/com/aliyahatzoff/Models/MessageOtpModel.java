package com.aliyahatzoff.Models;

import com.google.gson.annotations.SerializedName;

public class MessageOtpModel {
    @SerializedName("Status")
    String Status;
     @SerializedName("Details")
    String Details;

    public String getStatus() {
        return Status;
    }

    public String getDetails() {
        return Details;
    }
}
