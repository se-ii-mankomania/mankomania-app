package com.example.mankomania.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Boese1API {
    private static final String SERVER = HttpClient.getServer();
    private static final int PORT = HttpClient.getPort();
    private static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String sendBoeseRequest(String token, UUID lobbyid, int sum, int one) throws IOException, JSONException {
        // Create the request
        Request request = createPostRequest(token, "/api/boese1/" + lobbyid.toString(), sum, one);

        // Execute the request
        return executeBoeseRequest(HttpClient.getHttpClient(), request);
    }

    private static Request createPostRequest(String token, String path, int sum, int one) throws JSONException {
        // Create JSON object for the request body
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("sum", sum);
        jsonBody.put("one", one);

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        // Log request body
        Log.d("Boese1API", "Request body: " + jsonBody.toString());

        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .header(HEADER_AUTHORIZATION_KEY, token)
                .header("Content-Type", "application/json; charset=utf-8")
                .post(body)
                .build();
    }

    private static String executeBoeseRequest(OkHttpClient okHttpClient, Request request) throws IOException {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body() != null ? response.body().string() : "Response Body ist leer!";
            } else {
                return "Fehler: " + response.message();
            }
        } catch (IOException e){
            return "Fehler";
        }
    }
}