package com.example.mankomania.apitests;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.mankomania.api.HttpClient;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;


public class HttpClientTests {

    @Test
    public void testGetHttpClient() {
        OkHttpClient httpClient = HttpClient.getHttpClient();
        assertEquals(OkHttpClient.class, httpClient.getClass());
    }

    @Test
    public void testGetServer() {
        assertEquals("http://10.0.2.2", HttpClient.getServer());
    }

    @Test
    public void testGetPort() {
        assertEquals(3000, HttpClient.getPort());
    }

    @Test
    public void testGetHttpClientSingleton() {
        OkHttpClient httpClient1 = HttpClient.getHttpClient();
        OkHttpClient httpClient2 = HttpClient.getHttpClient();
        assertEquals(httpClient1, httpClient2);
    }
}
