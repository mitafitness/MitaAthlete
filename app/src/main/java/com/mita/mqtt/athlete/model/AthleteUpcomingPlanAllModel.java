package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AthleteUpcomingPlanAllModel {
    @SerializedName("atheletUserId")
    @Expose
    private String atheletUserId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("metaPlanId")
    @Expose
    private String metaPlanId;
    @SerializedName("coachId")
    @Expose
    private String coachId;
    @SerializedName("planGoal")
    @Expose
    private String planGoal;
    @SerializedName("realRun")
    @Expose
    private String realRun;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("pace")
    @Expose
    private String pace;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("upcomingTital")
    @Expose
    private String upcomingTital;
    @SerializedName("coachName")
    @Expose
    private String coachName;
    @SerializedName("coachPhone")
    @Expose
    private String coachPhone;

    public String getAthelRunPlanActId() {
        return athelRunPlanActId;
    }

    public void setAthelRunPlanActId(String athelRunPlanActId) {
        this.athelRunPlanActId = athelRunPlanActId;
    }

    @SerializedName("athelRunPlanActId")
    @Expose
    private String athelRunPlanActId;

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getCoachPhone() {
        return coachPhone;
    }

    public void setCoachPhone(String coachPhone) {
        this.coachPhone = coachPhone;
    }

    public String getCoachPhoto() {
        return coachPhoto;
    }

    public void setCoachPhoto(String coachPhoto) {
        this.coachPhoto = coachPhoto;
    }

    @SerializedName("coachPhoto")
    @Expose
    private String coachPhoto;

    public String getAtheletUserId() {
        return atheletUserId;
    }

    public void setAtheletUserId(String atheletUserId) {
        this.atheletUserId = atheletUserId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMetaPlanId() {
        return metaPlanId;
    }

    public void setMetaPlanId(String metaPlanId) {
        this.metaPlanId = metaPlanId;
    }

    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public String getPlanGoal() {
        return planGoal;
    }

    public void setPlanGoal(String planGoal) {
        this.planGoal = planGoal;
    }

    public String getRealRun() {
        return realRun;
    }

    public void setRealRun(String realRun) {
        this.realRun = realRun;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpcomingTital() {
        return upcomingTital;
    }

    public void setUpcomingTital(String upcomingTital) {
        this.upcomingTital = upcomingTital;
    }
}
