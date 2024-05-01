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

    // must be changed later when server is deployed
    // 10.0.2.2 to reach localhost of development machine
    private static final String SERVER = "http://10.0.2.2";
    private static final int PORT = 3000;

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
        // create JSON Object that holds email and password
        JSONObject jsonRequest = createJSONRequest(email, password);

        // create Request
        Request request = createRequest(jsonRequest, "/api/auth/login");

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
        // create JSON Object that holds email and password
        JSONObject jsonRequest = createJSONRequest(email, password);

        // create Request
        Request request = createRequest(jsonRequest, "/api/auth/register");

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

    @NonNull
    public static JSONObject createJSONRequest(String email, String password) {
        // create JSON object for request
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("email", email);
            jsonRequest.put("password", password);
        } catch (JSONException e) {
            // TODO: display error in some way
        }
        return jsonRequest;
    }

    public static Request createRequest(JSONObject jsonRequest, String path) {
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));

        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .post(requestBody)
                .build();
    }
}