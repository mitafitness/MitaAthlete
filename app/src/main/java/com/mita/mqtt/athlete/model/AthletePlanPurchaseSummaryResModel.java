package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AthletePlanPurchaseSummaryResModel {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("content")
    @Expose
    private AthletePlanPurchaseSummaryModel content;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AthletePlanPurchaseSummaryModel getContent() {
        return content;
    }

    public void setContent(AthletePlanPurchaseSummaryModel content) {
        this.content = content;
    }
}
