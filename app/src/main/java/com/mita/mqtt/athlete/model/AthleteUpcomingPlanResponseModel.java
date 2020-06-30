package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AthleteUpcomingPlanResponseModel {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("content")
    @Expose
    private List<AthleteUpcomingPlanAllModel> content = null;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AthleteUpcomingPlanAllModel> getContent() {
        return content;
    }

    public void setContent(List<AthleteUpcomingPlanAllModel> content) {
        this.content = content;
    }
}
