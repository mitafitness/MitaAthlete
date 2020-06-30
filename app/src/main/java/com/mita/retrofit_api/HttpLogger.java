package com.mita.retrofit_api;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;


public class HttpLogger implements HttpLoggingInterceptor.Logger {
    private final String mTag;

    public HttpLogger(String tag) {
        mTag = tag;
    }

    @Override
    public void log(String message) {
        Log.d(mTag, message);
    }
}