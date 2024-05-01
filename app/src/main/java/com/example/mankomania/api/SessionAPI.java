package com.example.mankomania.api;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class SessionAPI {
    private static String successMessage; //zweite?
    private static String[] unavailableColorsDisplayString;
    private static String[] statusDisplayString;

    public interface JoinSessionCallback {
        void onJoinSessionSuccess(String successMessage);
        void onJoinSessionFailure(String errorMessage);
    }

    public interface GetUnavailableColorsByLobbyCallback {
        void onGetUnavailableColorsByLobbySuccess(String[] colors);
        void onGetUnavailableColorsByLobbyFailure(String errorMessage);
    }

    public interface GetStatusByLobbyCallback {
        void onGetStatusByLobbySuccess(String[] status);
        void onGetStatusByLobbyFailure(String errorMessage);
    }
    public interface SetColorCallback {
        void onSetColorSuccess(String successMessage);
        void onSetColorFailure(String errorMessage);
    }

    public static void joinSession(String token, UUID lobbyid,final SessionAPI.JoinSessionCallback callback) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("lobbyid", lobbyid);

        } catch (JSONException e) {
            callback.onJoinSessionFailure("Request konnte nicht erstellt werden!");
        }

        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/session/initialize"+ lobbyid.toString())
                .header("Authorization", token)
                .build();

        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onJoinSessionFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseBody = Objects.requireNonNull(response.body()).string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // return the message
                        successMessage = jsonResponse.getString("message");
                        callback.onJoinSessionSuccess(successMessage);
                    } catch (JSONException e) {
                        callback.onJoinSessionFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onJoinSessionFailure(response.message());
                }
            }
        });
    }
    public static void getUnavailableColorsByLobby(String token, UUID lobbyid, final SessionAPI.GetUnavailableColorsByLobbyCallback callback) {
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/session/unavailableColors/" + lobbyid.toString())
                .header("Authorization", token)
                .build();

        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onGetUnavailableColorsByLobbyFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseBody = Objects.requireNonNull(response.body()).string();

                    try {
                        JSONArray responseArray = new JSONArray(responseBody);
                        unavailableColorsDisplayString = new String[responseArray.length()];

                        for(int i = 0; i < responseArray.length(); i++) {
                            JSONObject jsonSession = responseArray.getJSONObject(i);
                            UUID jsonLobbyId=UUID.fromString(jsonSession.getString("lobbyid"));
                            if(jsonLobbyId==lobbyid) {
                                unavailableColorsDisplayString[i] = jsonSession.getString("color");
                            }
                        }

                        callback.onGetUnavailableColorsByLobbySuccess(unavailableColorsDisplayString);
                    } catch (JSONException e) {
                        callback.onGetUnavailableColorsByLobbyFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onGetUnavailableColorsByLobbyFailure(response.message());
                }
            }
        });
    }
    public static void getStatusByLobby(String token, UUID lobbyid, final SessionAPI.GetStatusByLobbyCallback callback) {
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/session/status/" + lobbyid.toString())
                .header("Authorization", token)
                .build();

        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onGetStatusByLobbyFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONArray responseArray = new JSONArray(responseBody);
                        statusDisplayString = new String[responseArray.length()];

                        for(int i = 0; i < responseArray.length(); i++) {
                            JSONObject jsonSession = responseArray.getJSONObject(i);
                            statusDisplayString[i] = jsonSession.getString("status");
                        }

                        callback.onGetStatusByLobbySuccess(statusDisplayString);
                    } catch (JSONException e) {
                        callback.onGetStatusByLobbyFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onGetStatusByLobbyFailure(response.message());
                }
            }
        });
    }
    public static void setColor(String token, UUID lobbyid, String color,final SessionAPI.SetColorCallback callback) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("color", color);

        } catch (JSONException e) {
            callback.onSetColorFailure("Request konnte nicht erstellt werden!");
        }

        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/session/setColor/"+ lobbyid.toString())
                .header("Authorization", token)
                .build();

        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onSetColorFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseBody = Objects.requireNonNull(response.body()).string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // return the message
                        successMessage = jsonResponse.getString("message");
                        callback.onSetColorSuccess(successMessage);
                    } catch (JSONException e) {
                        callback.onSetColorFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onSetColorFailure(response.message());
                }
            }
        });
    }
}
