package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AthletePlansResponseModel {
    @SerializedName("code")
    private String code;

    @SerializedName("content")
    private List<AthletePnalsModel> content;


    public List<AthletePnalsModel> getContent() {
        return content;
    }

    public void setContent(List<AthletePnalsModel> content) {
        this.content = content;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
