package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlansResponseModel {
    @SerializedName("code")
    private int code;

    @SerializedName("content")
    private List<PlansAllModel> content;


    public List<PlansAllModel> getContent() {
        return content;
    }

    public void setContent(List<PlansAllModel> content) {
        this.content = content;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}