package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AthleteActivitySummaryModel {

    @SerializedName("code")
    private String code;

    @SerializedName("content")
    private List<AthleteActivitySummaryContentModel> content;

    @SerializedName("feedBack")
    @Expose
    private List<AthleteActivitySummaryContentModel> feedBack = null;

    public List<AthleteActivitySummaryContentModel> getContent() {
        return content;
    }

    public void setContent(List<AthleteActivitySummaryContentModel> content) {
        this.content = content;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public List<AthleteActivitySummaryContentModel> getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(List<AthleteActivitySummaryContentModel> feedBack) {
        this.feedBack = feedBack;
    }


}
