package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AthleteDetailsModel {


    @SerializedName("athUserId")
    @Expose
    private Integer athUserId;
    @SerializedName("athJoingDate")
    @Expose
    private String athJoingDate;
    @SerializedName("profileType")
    @Expose
    private String profileType;
    @SerializedName("profileURL")
    @Expose
    private String profileURL;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("photoUrl")
    @Expose
    private String photoUrl;
    @SerializedName("decription")
    @Expose
    private String decription;
    @SerializedName("shareParmissionPhoto")
    @Expose
    private Boolean shareParmissionPhoto;
    @SerializedName("sharePermissionVoice")
    @Expose
    private Boolean sharePermissionVoice;
    @SerializedName("athRunPlanId")
    @Expose
    private Object athRunPlanId;
    @SerializedName("acceptPolicy")
    @Expose
    private Boolean acceptPolicy;

    public Integer getAthUserId() {
        return athUserId;
    }

    public void setAthUserId(Integer athUserId) {
        this.athUserId = athUserId;
    }

    public String getAthJoingDate() {
        return athJoingDate;
    }

    public void setAthJoingDate(String athJoingDate) {
        this.athJoingDate = athJoingDate;
    }


    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }


    public Object getAthRunPlanId() {
        return athRunPlanId;
    }

    public void setAthRunPlanId(Object athRunPlanId) {
        this.athRunPlanId = athRunPlanId;
    }

    public Boolean getAcceptPolicy() {
        return acceptPolicy;
    }

    public void setAcceptPolicy(Boolean acceptPolicy) {
        this.acceptPolicy = acceptPolicy;
    }

    @SerializedName("coachUserId")
    @Expose
    private Integer coachUserId;

    public String getNoOfAlthICoached() {
        return noOfAlthICoached;
    }

    public void setNoOfAlthICoached(String noOfAlthICoached) {
        this.noOfAlthICoached = noOfAlthICoached;
    }

    @SerializedName("noOfAlthICoached")
    @Expose
    private String noOfAlthICoached;
    @SerializedName("coachJoingDate")
    @Expose
    private String coachJoingDate;


    @SerializedName("specializesIn")
    @Expose
    private String specializesIn;
    @SerializedName("coachingScince")
    @Expose
    private String coachingScince;
    @SerializedName("favriteDistance")
    @Expose
    private String favriteDistance;
    @SerializedName("favrteEvent")
    @Expose
    private String favrteEvent;
    @SerializedName("pramotionalVideios")
    @Expose
    private String pramotionalVideios;
    @SerializedName("profileCertification")
    @Expose
    private String profileCertification;
    @SerializedName("maxAthCountAllowed")
    @Expose
    private Integer maxAthCountAllowed;
    @SerializedName("currentAthCount")
    @Expose
    private Integer currentAthCount;
    @SerializedName("coachAthMap")
    @Expose
    private String coachAthMap;
    @SerializedName("coachIsActive")
    @Expose
    private Boolean coachIsActive;

    public Boolean getPlanExp() {
        return isPlanExp;
    }

    public void setPlanExp(Boolean planExp) {
        isPlanExp = planExp;
    }

    @SerializedName("isPlanExp")
    @Expose
    private Boolean isPlanExp;

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @SerializedName("profileUrl")
    @Expose
    private String profileUrl;

    public Integer getCoachUserId() {
        return coachUserId;
    }

    public void setCoachUserId(Integer coachUserId) {
        this.coachUserId = coachUserId;
    }

    public String getCoachJoingDate() {
        return coachJoingDate;
    }

    public void setCoachJoingDate(String coachJoingDate) {
        this.coachJoingDate = coachJoingDate;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public String getSpecializesIn() {
        return specializesIn;
    }

    public void setSpecializesIn(String specializesIn) {
        this.specializesIn = specializesIn;
    }

    public String getCoachingScince() {
        return coachingScince;
    }

    public void setCoachingScince(String coachingScince) {
        this.coachingScince = coachingScince;
    }

    public String getFavriteDistance() {
        return favriteDistance;
    }

    public void setFavriteDistance(String favriteDistance) {
        this.favriteDistance = favriteDistance;
    }

    public String getFavrteEvent() {
        return favrteEvent;
    }

    public void setFavrteEvent(String favrteEvent) {
        this.favrteEvent = favrteEvent;
    }

    public String getPramotionalVideios() {
        return pramotionalVideios;
    }

    public void setPramotionalVideios(String pramotionalVideios) {
        this.pramotionalVideios = pramotionalVideios;
    }

    public Boolean getShareParmissionPhoto() {
        return shareParmissionPhoto;
    }

    public void setShareParmissionPhoto(Boolean shareParmissionPhoto) {
        this.shareParmissionPhoto = shareParmissionPhoto;
    }

    public Boolean getSharePermissionVoice() {
        return sharePermissionVoice;
    }

    public void setSharePermissionVoice(Boolean sharePermissionVoice) {
        this.sharePermissionVoice = sharePermissionVoice;
    }

    public String getProfileCertification() {
        return profileCertification;
    }

    public void setProfileCertification(String profileCertification) {
        this.profileCertification = profileCertification;
    }

    public Integer getMaxAthCountAllowed() {
        return maxAthCountAllowed;
    }

    public void setMaxAthCountAllowed(Integer maxAthCountAllowed) {
        this.maxAthCountAllowed = maxAthCountAllowed;
    }

    public Integer getCurrentAthCount() {
        return currentAthCount;
    }

    public void setCurrentAthCount(Integer currentAthCount) {
        this.currentAthCount = currentAthCount;
    }

    public String getCoachAthMap() {
        return coachAthMap;
    }

    public void setCoachAthMap(String coachAthMap) {
        this.coachAthMap = coachAthMap;
    }

    public Boolean getCoachIsActive() {
        return coachIsActive;
    }

    public void setCoachIsActive(Boolean coachIsActive) {
        this.coachIsActive = coachIsActive;
    }

}
