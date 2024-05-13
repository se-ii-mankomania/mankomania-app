package com.example.mankomania.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mankomania.logik.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SessionAPI {
    private static String successMessage;
    private static List<Color> unavailableColors;
    private static HashMap<UUID, Session> sessions;

    public interface UpdatePositionCallback {
        void onUpdateSuccess(String message);
        void onUpdateFailure(String errorMessage);
    }

    public interface JoinSessionCallback {
        void onJoinSessionSuccess(String successMessage);
        void onJoinSessionFailure(String errorMessage);
    }

    public interface GetUnavailableColorsByLobbyCallback {
        void onGetUnavailableColorsByLobbySuccess(List<Color> colors);
        void onGetUnavailableColorsByLobbyFailure(String errorMessage);
    }

    public interface GetStatusByLobbyCallback {
        void onGetStatusByLobbySuccess(HashMap<UUID,Session> sessions);
        void onGetStatusByLobbyFailure(String errorMessage);
    }
    public interface SetColorCallback {
        void onSetColorSuccess(String successMessage);
        void onSetColorFailure(String errorMessage);
    }

    public static void updatePlayerPosition(String token, String userID, int currentposition, String lobbyID, final UpdatePositionCallback callback) {
        // create JSON object for request
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userID);
            jsonRequest.put("currentposition", currentposition);
        } catch (JSONException e) {
            callback.onUpdateFailure("Request konnte nicht erstellt werden!");
            return;
        }

        // create Request
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/session/setPosition/" + lobbyID)
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


    public static void joinSession(String token, UUID lobbyid,final SessionAPI.JoinSessionCallback callback) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("lobbyid", lobbyid);

        } catch (JSONException e) {
            callback.onJoinSessionFailure("Request konnte nicht erstellt werden!");
        }
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/session/initialize")
                .header("Authorization", token)
                .post(requestBody)
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

                        sessions = new HashMap<>();

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
                if (response.isSuccessful()) {
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            String responseBodyString = responseBody.string();
                            JSONArray responseArray = new JSONArray(responseBodyString);
                            unavailableColors=new ArrayList<>();
                            for(int i = 0; i < responseArray.length(); i++) {
                                JSONObject jsonSession = responseArray.getJSONObject(i);
                                String color = jsonSession.getString("color");
                                Color enumValueOfColor=convertToEnums(color);
                                unavailableColors.add(enumValueOfColor);
                            }
                            callback.onGetUnavailableColorsByLobbySuccess(unavailableColors);
                        } else {
                            callback.onGetUnavailableColorsByLobbyFailure("Response Body ist leer!");
                        }
                    } catch (JSONException e) {
                        callback.onGetUnavailableColorsByLobbyFailure("Fehler beim Lesen der Response: " + e.getMessage());
                    }
                } else {
                    callback.onGetUnavailableColorsByLobbyFailure(response.message());
                }
            }
        });
    }
    private static Color convertToEnums(String color){
        switch (color){
            case "blue": return Color.BLUE;
            case "red": return Color.RED;
            case "green": return Color.GREEN;
            case "lila":  return Color.PURPLE;
            default: return null;
        }
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
                if (response.isSuccessful()) {
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            String responseString = responseBody.string();
                            JSONArray responseArray = new JSONArray(responseString);
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject jsonSession = responseArray.getJSONObject(i);
                                UUID userid = UUID.fromString(jsonSession.getString("userid"));
                                String email = jsonSession.getString("email");
                                String colorString=jsonSession.getString("color");
                                Log.wtf("HAAALLLOOOOO",colorString);
                                Color color = convertToEnums(colorString);
                                Log.wtf("HAAALLLOOOOOOOENUM", Objects.requireNonNull(color).toString());
                                int currentPosition = jsonSession.getInt("currentposition");
                                int balance = jsonSession.getInt("balance");
                                boolean isPlayersTurn = jsonSession.getBoolean("isplayersturn");

                                Session session = new Session(userid, email, color, currentPosition, balance, 0, 0, 0, isPlayersTurn);
                                Session formerSession=sessions.get(userid);
                                sessions.put(userid, session);
                                SessionStatusService sessionStatusService = SessionStatusService.getInstance();
                                sessionStatusService.notifyUpdatesInSession(formerSession, session,userid);
                            }

                            callback.onGetStatusByLobbySuccess(sessions);
                        } else {
                            callback.onGetStatusByLobbyFailure(response.message());
                        }
                    } catch (JSONException e) {
                        callback.onGetStatusByLobbyFailure("Fehler beim Lesen der Response!");
                    }
                }
            }});

    }
    public static void setColor(String token, UUID lobbyid, String color,final SessionAPI.SetColorCallback callback) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("color", color);

        } catch (JSONException e) {
            callback.onSetColorFailure("Request konnte nicht erstellt werden!");
        }
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(HttpClient.getServer() + ":" + HttpClient.getPort() + "/api/session/setColor/"+ lobbyid.toString())
                .header("Authorization", token)
                .post(requestBody)
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
