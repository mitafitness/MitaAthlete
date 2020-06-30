package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoachListResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("content")
    private List<CoachAllModel> content;


    public List<CoachAllModel> getContent() {
        return content;
    }

    public void setContent(List<CoachAllModel> content) {
        this.content = content;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
