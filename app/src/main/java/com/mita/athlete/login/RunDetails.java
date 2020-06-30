package com.mita.athlete.login;

public class RunDetails {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAthRunPlanCalenderDate() {
        return athRunPlanCalenderDate;
    }

    public void setAthRunPlanCalenderDate(String athRunPlanCalenderDate) {
        this.athRunPlanCalenderDate = athRunPlanCalenderDate;
    }

    public String getTotActivityRun() {
        return totActivityRun;
    }

    public void setTotActivityRun(String totActivityRun) {
        this.totActivityRun = totActivityRun;
    }

    public String getTotActivityDuration() {
        return totActivityDuration;
    }

    public void setTotActivityDuration(String totActivityDuration) {
        this.totActivityDuration = totActivityDuration;
    }

    private String name;
    private String athRunPlanCalenderDate;
    private String totActivityRun;
    private String totActivityDuration;


}
