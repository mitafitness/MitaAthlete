package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AthleteTodaysRunResponseModel {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("content")
    @Expose
    private AthleteTodaysRunContentModel content;

    public AthleteTodaysRunContentModel getTodayCard() {
        return todayCard;
    }

    public void setTodayCard(AthleteTodaysRunContentModel todayCard) {
        this.todayCard = todayCard;
    }

    @SerializedName("todayCard")
    @Expose
    private AthleteTodaysRunContentModel todayCard;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AthleteTodaysRunContentModel getContent() {
        return content;
    }

    public void setContent(AthleteTodaysRunContentModel content) {
        this.content = content;
    }
}
