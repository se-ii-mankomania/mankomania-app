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

public class Movement {

    public interface UpdatePositionCallback {
        void onUpdateSuccess(String message);
        void onUpdateFailure(String errorMessage);
    }
    public static void updatePlayerPosition(String token, String userID, int gameboardFieldID, final UpdatePositionCallback callback) {
        // create JSON object for request
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userID", userID);
            jsonRequest.put("gameboardFieldID", gameboardFieldID);
        } catch (JSONException e) {
            callback.onUpdateFailure("Request konnte nicht erstellt werden!");
            return;
        }

        // create Request
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/gameplay/updatePosition")
                .header("Authorization", token)
                .post(requestBody)
                .build();

        // execute request
        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onUpdateFailure("Keine Antwort vom Server!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // extract message from response
                        String message = jsonResponse.getString("message");
                        callback.onUpdateSuccess(message);
                    } catch (JSONException e) {
                        callback.onUpdateFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onUpdateFailure("Aktualisierung fehlgeschlagen, Antwortcode: " + response.code());
                }
            }
        });
    }

}
