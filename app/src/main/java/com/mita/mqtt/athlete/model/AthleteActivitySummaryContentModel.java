package com.mita.mqtt.athlete.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AthleteActivitySummaryContentModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("sensorId")
    @Expose
    private String sensorId;
    @SerializedName("althRunActivityId")
    @Expose
    private String althRunActivityId;
    @SerializedName("athUserId")
    @Expose
    private String athUserId;
    @SerializedName("absTimeStamp")
    @Expose
    private String absTimeStamp;
    @SerializedName("hrRRTime")
    @Expose
    private Integer hrRRTime;
    @SerializedName("elevation")
    @Expose
    private Double elevation;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("accuracy")
    @Expose
    private Double accuracy;
    @SerializedName("altitude")
    @Expose
    private Double altitude;
    @SerializedName("steps")
    @Expose
    private Integer steps;
    @SerializedName("pace")
    @Expose
    private String pace;
    @SerializedName("cadence")
    @Expose
    private Integer cadence;
    @SerializedName("heartRate")
    @Expose
    private Integer heartRate;
    @SerializedName("absTime")
    @Expose
    private String absTime;
    @SerializedName("run")
    @Expose
    private Object run;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("used")
    @Expose
    private String used;
    @SerializedName("fixtedTime")
    @Expose
    private String fixtedTime;
    @SerializedName("stepSensorTime")
    @Expose
    private String stepSensorTime;
    @SerializedName("maxAcc")
    @Expose
    private String maxAcc;
    @SerializedName("minAcc")
    @Expose
    private String minAcc;


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
    private Object feedBackStream;
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

    public Object getFeedBackStream() {
        return feedBackStream;
    }

    public void setFeedBackStream(Object feedBackStream) {
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

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getAlthRunActivityId() {
        return althRunActivityId;
    }

    public void setAlthRunActivityId(String althRunActivityId) {
        this.althRunActivityId = althRunActivityId;
    }

    public String getAthUserId() {
        return athUserId;
    }

    public void setAthUserId(String athUserId) {
        this.athUserId = athUserId;
    }

    public String getAbsTimeStamp() {
        return absTimeStamp;
    }

    public void setAbsTimeStamp(String absTimeStamp) {
        this.absTimeStamp = absTimeStamp;
    }

    public Integer getHrRRTime() {
        return hrRRTime;
    }

    public void setHrRRTime(Integer hrRRTime) {
        this.hrRRTime = hrRRTime;
    }

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public Integer getCadence() {
        return cadence;
    }

    public void setCadence(Integer cadence) {
        this.cadence = cadence;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public String getAbsTime() {
        return absTime;
    }

    public void setAbsTime(String absTime) {
        this.absTime = absTime;
    }

    public Object getRun() {
        return run;
    }

    public void setRun(Object run) {
        this.run = run;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getFixtedTime() {
        return fixtedTime;
    }

    public void setFixtedTime(String fixtedTime) {
        this.fixtedTime = fixtedTime;
    }

    public String getStepSensorTime() {
        return stepSensorTime;
    }

    public void setStepSensorTime(String stepSensorTime) {
        this.stepSensorTime = stepSensorTime;
    }

    public String getMaxAcc() {
        return maxAcc;
    }

    public void setMaxAcc(String maxAcc) {
        this.maxAcc = maxAcc;
    }

    public String getMinAcc() {
        return minAcc;
    }

    public void setMinAcc(String minAcc) {
        this.minAcc = minAcc;
    }

}