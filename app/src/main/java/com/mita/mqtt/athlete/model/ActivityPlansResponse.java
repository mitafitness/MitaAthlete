package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActivityPlansResponse {

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPreparedDate() {
        return preparedDate;
    }

    public void setPreparedDate(String preparedDate) {
        this.preparedDate = preparedDate;
    }

    public String getPlanTitleType() {
        return planTitleType;
    }

    public void setPlanTitleType(String planTitleType) {
        this.planTitleType = planTitleType;
    }

    public String getCustomPlanTitle() {
        return customPlanTitle;
    }

    public void setCustomPlanTitle(String customPlanTitle) {
        this.customPlanTitle = customPlanTitle;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public void setPlanDescription(String planDescription) {
        this.planDescription = planDescription;
    }

    public String getPlanDescription1() {
        return planDescription1;
    }

    public void setPlanDescription1(String planDescription1) {
        this.planDescription1 = planDescription1;
    }

    public String getPlanDescription2() {
        return planDescription2;
    }

    public void setPlanDescription2(String planDescription2) {
        this.planDescription2 = planDescription2;
    }

    public String getCoachPlanCtegory() {
        return coachPlanCtegory;
    }

    public void setCoachPlanCtegory(String coachPlanCtegory) {
        this.coachPlanCtegory = coachPlanCtegory;
    }



    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<ActivityAllPlans> getActivityCalender() {
        return activityCalender;
    }

    public void setActivityCalender(List<ActivityAllPlans> activityCalender) {
        this.activityCalender = activityCalender;
    }

    @SerializedName("code")
    private String code;
    @SerializedName("planId")
    private String planId;
    @SerializedName("preparedDate")
    private String preparedDate;
    @SerializedName("planTitleType")
    private String planTitleType;
    @SerializedName("customPlanTitle")
    private String customPlanTitle;
    @SerializedName("planDescription")
    private String planDescription;
    @SerializedName("planDescription1")
    private String planDescription1;
    @SerializedName("planDescription2")
    private String planDescription2;
    @SerializedName("coachPlanCtegory")
    private String coachPlanCtegory;
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("endDate")
    private String endDate;
    @SerializedName("planGoal")
    private String planGoal;
    @SerializedName("planDuration")
    private String planDuration;

    public String getPlanGoal() {
        return planGoal;
    }

    public void setPlanGoal(String planGoal) {
        this.planGoal = planGoal;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public void setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
    }

    public String getPlanAchievement() {
        return planAchievement;
    }

    public void setPlanAchievement(String planAchievement) {
        this.planAchievement = planAchievement;
    }

    @SerializedName("planAchievement")
    private String planAchievement;


    @SerializedName("activityCalender")
    private List<ActivityAllPlans> activityCalender;
}
