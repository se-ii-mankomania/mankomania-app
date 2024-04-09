package com.example.mankomania.api;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class Lobby {
    private static String[] lobbyNames;

    // interface to notify whether login is successful or not
    public interface LobbiesCallback {
        void onLobbiesSuccess(String[] lobbies);
        void onLobbiesFailure(String errorMessage);
    }

    public static void getLobbies(String token, final LobbiesCallback callback) {
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/lobby/getAll")
                .header("Authorisation", token)
                .build();

        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onLobbiesFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONArray responseArray = new JSONArray(responseBody);
                        lobbyNames = new String[responseArray.length()];

                        for(int i = 0; i < responseArray.length(); i++) {
                            JSONObject jsonLobby = responseArray.getJSONObject(i);
                            lobbyNames[i] = jsonLobby.getString("name");
                        }
                        callback.onLobbiesSuccess(lobbyNames);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onLobbiesFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    // TODO: wann??
                    callback.onLobbiesFailure("Fehler!");
                }
            }
        });
    }


}
