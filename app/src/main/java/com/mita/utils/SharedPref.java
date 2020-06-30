package com.mita.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lokesh on 27-04-2020.
 */
public class SharedPref {
    private static SharedPreferences mSharedPref;
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String PHONE = "PHONE";
    public static final String PHOTO_URL = "PHOTO_URL";
    public static final String ATH_USER_ID = "ATH_USER_ID";
    public static final String PlanExp = "PlanExp";

    // Athlete purchase Plan
    public static final String plan_id = "plan_id";
    public static final String plan_map_id = "plan_map_id";
    public static final String coach_id = "coach_id";
    public static final String RUN_ACTIVITY_ID = "RUN_ACTIVITY_ID";
    public static final String RUN_ACTIVITY_COACHId = "RUN_ACTIVITY_COACHId";
    public static final String RUN_MODE = "RUN_MODE";


    private SharedPref() {

    }

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {

        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }


    public static void writedouble(String keys, float values) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putFloat(keys, values);
        prefsEditor.apply();

    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }
}