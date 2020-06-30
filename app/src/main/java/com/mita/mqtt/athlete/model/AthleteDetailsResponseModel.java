package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AthleteDetailsResponseModel {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("content")
    @Expose
    private AthleteDetailsModel content;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AthleteDetailsModel getContent() {
        return content;
    }

    public void setContent(AthleteDetailsModel content) {
        this.content = content;
    }


}
