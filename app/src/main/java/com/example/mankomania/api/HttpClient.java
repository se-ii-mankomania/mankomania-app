package com.example.mankomania.api;

import okhttp3.OkHttpClient;

public class HttpClient {
    private static OkHttpClient httpClientInstance;

    // must be changed later when server is deployed
    // 10.0.2.2 to reach localhost of development machine
    private static final String SERVER = "http://10.0.2.2";
    private static final int PORT = 3000;

    // private constructor to prevent instantiation from outside
    private HttpClient() {}

    public static OkHttpClient getHttpClient() {
        if(httpClientInstance == null) {
            httpClientInstance = new OkHttpClient();
        }

        return httpClientInstance;
    }

    public static String getServer() {
        return SERVER;
    }

    public static int getPort() {
        return PORT;
    }
}
