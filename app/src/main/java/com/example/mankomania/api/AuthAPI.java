package com.example.mankomania.api;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthAPI {
    private static String token;

    private static String message;

    // interface to notify whether login is successful or not
    public interface LoginCallback {
        void onLoginSuccess(String token);
        void onLoginFailure(String errorMessage);
    }

    // interface to notify whether register is successful or not
    public interface RegisterCallback {
        void onRegisterSuccess(String message);
        void onRegisterFailure(String errorMessage);
    }

    /**
     * this method sends the user credentials to the server
     * on success, the server responds with a token, which is needed later for more requests
     */
    public static void login(String email, String password, final LoginCallback callback) {
        // create JSON object for request
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("email", email);
            jsonRequest.put("password", password);
        } catch (JSONException e) {
            callback.onLoginFailure("Request konnte nicht erstellt werden!");
        }

        // create Request
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/auth/login")
                .post(requestBody)
                .build();

        // execute request (at some point)
        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onLoginFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // return the token
                        token = jsonResponse.getString("token");
                        callback.onLoginSuccess(token);
                    } catch (JSONException e) {
                        callback.onLoginFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onLoginFailure("Email oder Passwort falsch!");
                }
            }
        });
    }

    /**
     * this method tries to register a user
     * on success, the new user gets added into the db
     * the server will provide a feedback
     */
    public static void register(String email, String password, final RegisterCallback callback) {
        // create JSON object for request
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("email", email);
            jsonRequest.put("password", password);
        } catch (JSONException e) {
            callback.onRegisterFailure("Request konnte nicht erstellt werden!");
        }

        // create Request
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/auth/register")
                .post(requestBody)
                .build();

        // execute request (at some point)
        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onRegisterFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // return the message
                        message = jsonResponse.getString("message");
                        callback.onRegisterSuccess(message);
                    } catch (JSONException e) {
                        callback.onRegisterFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onRegisterFailure("User bereits registriert!");
                }
            }
        });
    }
}
