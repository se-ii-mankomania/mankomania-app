package com.example.mankomania.api;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Lobby {
    private static String[] lobbyNames;
    private static String message;

    // interface to notify whether login is successful or not
    public interface GetLobbiesCallback {
        void onGetLobbiesSuccess(String[] lobbies);
        void onGetLobbiesFailure(String errorMessage);
    }

    public interface AddLobbyCallback {
        void onAddLobbySuccess(String message);
        void onAddLobbyFailure(String errorMessage);
    }

    public static void getLobbies(String token, final GetLobbiesCallback callback) {
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/lobby/getAll")
                .header("Authorisation", token)
                .build();

        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onGetLobbiesFailure("Keine Antwort!");
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
                        callback.onGetLobbiesSuccess(lobbyNames);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onGetLobbiesFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onGetLobbiesFailure("Fehler!");
                }
            }
        });
    }

    public static void addLobby(String token, String name, String password, boolean isPrivate, int maxPlayer, Status status, final AddLobbyCallback callback) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("name", name);
            jsonRequest.put("password", password);
            jsonRequest.put("isPrivate", isPrivate);
            jsonRequest.put("maxPlayers", maxPlayer);
            jsonRequest.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onAddLobbyFailure("Request konnte nicht erstellt werden!");
        }

        // create request
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/lobby/create")
                .header("Authorization", token)
                .post(requestBody)
                .build();

        // execute request (at some point)
        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onAddLobbyFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // return the message
                        message = jsonResponse.getString("message");
                        callback.onAddLobbySuccess(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onAddLobbyFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onAddLobbyFailure("Fehler!");
                }
            }
        });
    }


}
