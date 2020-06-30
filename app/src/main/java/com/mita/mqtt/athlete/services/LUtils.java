package com.mita.mqtt.athlete.services;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import com.mita.mqtt.athlete.R;

import java.text.DateFormat;
import java.util.Date;

public class LUtils {

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    /**
     * Returns the {@code location} object as a human readable string.
     * @param location  The {@link Location}.
     */
    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }
    public static String getLocationText1(String duration, String distance, String pace) {
        return duration == null ? "Current RUN" :
                "Time : " + duration + " , Distance : " + distance + " , Pace : " + pace ;
    }

//    static String getLocationText1() {
//        return "service";
//    }

    static String getLocationTitle1(Context context) {
        return context.getString(R.string.service_destroy);
    }

    static String getLocationTitle(Context context) {
        return context.getString(R.string.run_activity,
                DateFormat.getDateTimeInstance().format(new Date()));
    }
}