package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AthletePlanPurchaseSummaryModel {
    @SerializedName("metaCoachAlthMapId")
    @Expose
    private Integer metaCoachAlthMapId;
    @SerializedName("coachId")
    @Expose
    private String coachId;
    @SerializedName("coachname")
    @Expose
    private String coachname;
    @SerializedName("noOfCoachs")
    @Expose
    private String noOfCoachs;
    @SerializedName("description1")
    @Expose
    private String description1;
    @SerializedName("description2")
    @Expose
    private String description2;
    @SerializedName("description3")
    @Expose
    private String description3;
    @SerializedName("metaCustomPlanTitle")
    @Expose
    private String metaCustomPlanTitle;
    @SerializedName("metaPlanTitle")
    @Expose
    private String metaPlanTitle;
    @SerializedName("metaPlanInfoId")
    @Expose
    private String metaPlanInfoId;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("capacity")
    @Expose
    private Integer capacity;
    @SerializedName("planStatus")
    @Expose
    private String planStatus;
    @SerializedName("weeks")
    @Expose
    private Integer weeks;

    public String getCoachPhotoUrl() {
        return coachPhotoUrl;
    }

    @SerializedName("coachPhotoUrl")
    @Expose
    private String coachPhotoUrl;

    public String getCoachEmail() {
        return coachEmail;
    }

    public void setCoachEmail(String coachEmail) {
        this.coachEmail = coachEmail;
    }

    @SerializedName("coachEmail")
    @Expose
    private String coachEmail;

    public String getCoachAddress() {
        return coachAddress;
    }

    public void setCoachAddress(String coachAddress) {
        this.coachAddress = coachAddress;
    }

    @SerializedName("coachAddress")
    @Expose
    private String coachAddress;

    public Integer getMetaCoachAlthMapId() {
        return metaCoachAlthMapId;
    }

    public void setMetaCoachAlthMapId(Integer metaCoachAlthMapId) {
        this.metaCoachAlthMapId = metaCoachAlthMapId;
    }

    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public String getCoachname() {
        return coachname;
    }

    public void setCoachname(String coachname) {
        this.coachname = coachname;
    }

    public String getNoOfCoachs() {
        return noOfCoachs;
    }

    public void setNoOfCoachs(String noOfCoachs) {
        this.noOfCoachs = noOfCoachs;
    }

    public String getDescription1() {
        return description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getDescription3() {
        return description3;
    }

    public void setDescription3(String description3) {
        this.description3 = description3;
    }

    public String getMetaCustomPlanTitle() {
        return metaCustomPlanTitle;
    }

    public void setMetaCustomPlanTitle(String metaCustomPlanTitle) {
        this.metaCustomPlanTitle = metaCustomPlanTitle;
    }

    public String getMetaPlanTitle() {
        return metaPlanTitle;
    }

    public void setMetaPlanTitle(String metaPlanTitle) {
        this.metaPlanTitle = metaPlanTitle;
    }

    public String getMetaPlanInfoId() {
        return metaPlanInfoId;
    }

    public void setMetaPlanInfoId(String metaPlanInfoId) {
        this.metaPlanInfoId = metaPlanInfoId;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }

    public Integer getWeeks() {
        return weeks;
    }

    public void setWeeks(Integer weeks) {
        this.weeks = weeks;
    }

}
