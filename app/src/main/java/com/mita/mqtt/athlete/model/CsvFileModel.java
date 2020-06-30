package com.mita.mqtt.athlete.model;

public class CsvFileModel {
    private String time_ms;
    private String distance_km;
    private String latitude;
    private String longitude;
    private String accuracy_m;
    private String altitude_m;
    private String steps;
    private String pace_min_km;
    private String cadence;
    private String heart_rate;
    private String abs_time;
    private String used;
    private String fix_time_s;
    private String step_sensor_time_s;
    private String max_acc;
    private String min_acc;
    private String elevationgap_m;

    public String getTime_ms() {
        return time_ms;
    }

    public void setTime_ms(String time_ms) {
        this.time_ms = time_ms;
    }

    public String getDistance_km() {
        return distance_km;
    }

    public void setDistance_km(String distance_km) {
        this.distance_km = distance_km;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAccuracy_m() {
        return accuracy_m;
    }

    public void setAccuracy_m(String accuracy_m) {
        this.accuracy_m = accuracy_m;
    }

    public String getAltitude_m() {
        return altitude_m;
    }

    public void setAltitude_m(String altitude_m) {
        this.altitude_m = altitude_m;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getPace_min_km() {
        return pace_min_km;
    }

    public void setPace_min_km(String pace_min_km) {
        this.pace_min_km = pace_min_km;
    }

    public String getCadence() {
        return cadence;
    }

    public void setCadence(String cadence) {
        this.cadence = cadence;
    }

    public String getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(String heart_rate) {
        this.heart_rate = heart_rate;
    }

    public String getAbs_time() {
        return abs_time;
    }

    public void setAbs_time(String abs_time) {
        this.abs_time = abs_time;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getFix_time_s() {
        return fix_time_s;
    }

    public void setFix_time_s(String fix_time_s) {
        this.fix_time_s = fix_time_s;
    }

    public String getStep_sensor_time_s() {
        return step_sensor_time_s;
    }

    public void setStep_sensor_time_s(String step_sensor_time_s) {
        this.step_sensor_time_s = step_sensor_time_s;
    }

    public String getMax_acc() {
        return max_acc;
    }

    public void setMax_acc(String max_acc) {
        this.max_acc = max_acc;
    }

    public String getMin_acc() {
        return min_acc;
    }

    public void setMin_acc(String min_acc) {
        this.min_acc = min_acc;
    }

    public String getElevationgap_m() {
        return elevationgap_m;
    }

    public void setElevationgap_m(String elevationgap_m) {
        this.elevationgap_m = elevationgap_m;
    }

    public String getRr_ms() {
        return rr_ms;
    }

    public void setRr_ms(String rr_ms) {
        this.rr_ms = rr_ms;
    }

    private String rr_ms;
}
