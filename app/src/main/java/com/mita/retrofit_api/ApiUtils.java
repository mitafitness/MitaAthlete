package com.mita.retrofit_api;


public class ApiUtils {
    private ApiUtils() {
    }

    public static final String BASE_URL = "http://3.7.183.224:8585/"; // Dev Server
//    public static final String BASE_URL = "http://13.126.102.104:8585/"; // Client Server

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
