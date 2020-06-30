package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PastRunAudioFeedBackListModel {

    @SerializedName("atheletDocument")
    @Expose
    private String atheletDocument;
    @SerializedName("msgTimeStamp")
    @Expose
    private String msgTimeStamp;
    @SerializedName("coachDocument")
    @Expose
    private String coachDocument;
    @SerializedName("feedBackStream")
    @Expose
    private String feedBackStream;
    @SerializedName("streDuration")
    @Expose
    private String streDuration;
    @SerializedName("activityRunId")
    @Expose
    private String activityRunId;
    @SerializedName("isRunActivtyVoice")
    @Expose
    private Boolean isRunActivtyVoice;
    @SerializedName("isNew")
    @Expose
    private Boolean isNew;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("absTime")
    @Expose
    private String absTime;

    public String getAtheletDocument() {
        return atheletDocument;
    }

    public void setAtheletDocument(String atheletDocument) {
        this.atheletDocument = atheletDocument;
    }

    public String getMsgTimeStamp() {
        return msgTimeStamp;
    }

    public void setMsgTimeStamp(String msgTimeStamp) {
        this.msgTimeStamp = msgTimeStamp;
    }

    public String getCoachDocument() {
        return coachDocument;
    }

    public void setCoachDocument(String coachDocument) {
        this.coachDocument = coachDocument;
    }

    public String getFeedBackStream() {
        return feedBackStream;
    }

    public void setFeedBackStream(String feedBackStream) {
        this.feedBackStream = feedBackStream;
    }

    public String getStreDuration() {
        return streDuration;
    }

    public void setStreDuration(String streDuration) {
        this.streDuration = streDuration;
    }

    public String getActivityRunId() {
        return activityRunId;
    }

    public void setActivityRunId(String activityRunId) {
        this.activityRunId = activityRunId;
    }

    public Boolean getIsRunActivtyVoice() {
        return isRunActivtyVoice;
    }

    public void setIsRunActivtyVoice(Boolean isRunActivtyVoice) {
        this.isRunActivtyVoice = isRunActivtyVoice;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbsTime() {
        return absTime;
    }

    public void setAbsTime(String absTime) {
        this.absTime = absTime;
    }
}
