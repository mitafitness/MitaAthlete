package com.mita.utils;

import static com.mita.retrofit_api.ApiUtils.BASE_URL;

public class GlobalConstants {

    static float SMALLEST_DISPLACCEMENT = 0.1f;
    static int UPDATE_INTERVAL = 5000;
    static int FASTEST_INTERVAL = 5000;
    static int API_LOAD_TIME = 3000;
    final static String PHOTO_PATH_ATHELETE =BASE_URL+"mitaphotos/athlete/";
    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;
    public static double getPastRunLatitude() {
        return PAST_RUN_LATITUDE;
    }
    public static double getPastRunLongitude() {
        return PAST_RUN_LONGITUDE;
    }
    public static final double PAST_RUN_LATITUDE = 12.9716;
    public static final double PAST_RUN_LONGITUDE = 77.5946;
    public static  int HEARTRATE_THRESOLD = 150;
    public static  double PACE_THRESOLD = 100000.00;

    public static double getPaceThresold() {
        return PACE_THRESOLD;
    }

    public static int getHeartrateThresold() {
        return HEARTRATE_THRESOLD;
    }

    public static String getPhotoPathCoach() {
        return PHOTO_PATH_COACH;
    }

    final static String PHOTO_PATH_COACH =BASE_URL+"mitaphotos/coach/";

    public static String getPhotoPathAthelete() {
        return PHOTO_PATH_ATHELETE;
    }

    public static int getApiLoadTime() {
        return API_LOAD_TIME;
    }

    public static float getSmallestDisplaccement() {
        return SMALLEST_DISPLACCEMENT;
    }

    public static int getUpdateInterval() {
        return UPDATE_INTERVAL;
    }

    public static int getFastestInterval() {
        return FASTEST_INTERVAL;
    }

}
