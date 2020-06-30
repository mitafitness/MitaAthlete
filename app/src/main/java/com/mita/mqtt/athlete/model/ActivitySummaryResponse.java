package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.SerializedName;
import com.mita.athlete.login.RunDetails;

import java.util.List;

public class ActivitySummaryResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("content")
    private List<RunDetails> content;


    public List<RunDetails> getContent() {
        return content;
    }

    public void setContent(List<RunDetails> content) {
        this.content = content;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
