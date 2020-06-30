package com.mita.retrofit_api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mita.mqtt.athlete.model.CoachListResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static APIService service;

    public static Retrofit getClient(String baseUrl) {

        Gson gson = new GsonBuilder().setLenient().create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getOkHttpClient(new HttpLogger("Retrofit")))
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient(HttpLoggingInterceptor.Logger logger) {
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();

        // set conn timeout & read timeout
        mBuilder.connectTimeout(20000, TimeUnit.SECONDS);
        mBuilder.readTimeout(30000, TimeUnit.SECONDS);

        // add logging for all requests in our debug builds
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(logger);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // config builder
        mBuilder.addInterceptor(logging)
                .build();

        // build
        return mBuilder.build();
    }

}