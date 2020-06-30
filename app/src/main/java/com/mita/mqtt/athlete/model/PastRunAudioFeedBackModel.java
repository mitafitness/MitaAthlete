package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PastRunAudioFeedBackModel {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("meassage")
    @Expose
    private String meassage;
    @SerializedName("content")
    @Expose
    private PastRunAudioFeedBackListModel content;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMeassage() {
        return meassage;
    }

    public void setMeassage(String meassage) {
        this.meassage = meassage;
    }

    public PastRunAudioFeedBackListModel getContent() {
        return content;
    }

    public void setContent(PastRunAudioFeedBackListModel content) {
        this.content = content;
    }
}
