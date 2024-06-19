package com.example.mankomania.apitests;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.mankomania.api.HttpClient;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;


class HttpClientTests {

    @Test
    void testGetHttpClient() {
        OkHttpClient httpClient = HttpClient.getHttpClient();
        assertEquals(OkHttpClient.class, httpClient.getClass());
    }

    @Test
    void testGetServer() {
        assertEquals("http://se2-demo.aau.at", HttpClient.getServer());
    }

    @Test
    void testGetPort() {
        assertEquals(53214, HttpClient.getPort());
    }

    @Test
    void testGetHttpClientSingleton() {
        OkHttpClient httpClient1 = HttpClient.getHttpClient();
        OkHttpClient httpClient2 = HttpClient.getHttpClient();
        assertEquals(httpClient1, httpClient2);
    }
}
