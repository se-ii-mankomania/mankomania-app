package com.example.mankomania.api;

import androidx.annotation.NonNull;

import com.example.mankomania.logik.spieler.Color;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SessionAPI {
    private static final String SERVER = HttpClient.getServer();
    private static final int PORT = HttpClient.getPort();

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

    public static void joinSession(String token, UUID lobbyid,final SessionAPI.JoinSessionCallback callback) {
        // create JSONObject that holds lobbyID
        JSONObject jsonRequest = createJSONObject(lobbyid);

        // create request
        Request request = createPostRequest(jsonRequest, token, "/api/session/initialize");

        // execute request
        executeJoinSessionRequest(HttpClient.getHttpClient(), request, callback);
    }
    public static void getUnavailableColorsByLobby(String token, UUID lobbyid, final SessionAPI.GetUnavailableColorsByLobbyCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/session/unavailableColors/" + lobbyid.toString());

        // execute request
        executeGetUnavailableColorsByLobbyRequest(HttpClient.getHttpClient(), request, callback);
    }
    public static Color convertToEnums(String color){
        switch (color){
            case "blue": return Color.BLUE;
            case "red": return Color.RED;
            case "green": return Color.GREEN;
            case "lila":  return Color.PURPLE;
            default: return null;
        }
    }
    public static void getStatusByLobby(String token, UUID lobbyid, final SessionAPI.GetStatusByLobbyCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/session/status/" + lobbyid.toString());

        // execute request
        executeGetStatusByLobbyRequest(HttpClient.getHttpClient(), request, callback);
    }
    public static void setColor(String token, UUID lobbyid, String color,final SessionAPI.SetColorCallback callback) {
        // create JSONObject that holds color
        JSONObject jsonRequest = createJSONObject(color);

        // create request
        Request request = createPostRequest(jsonRequest, token, "/api/session/setColor/"+ lobbyid.toString());

        // execute request
        executeSetColorRequest(HttpClient.getHttpClient(), request, callback);
    }

    public static JSONObject createJSONObject(UUID lobbyid) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("lobbyid", lobbyid);

        } catch (JSONException e) {
            // callback.onJoinSessionFailure("Request konnte nicht erstellt werden!");
        }

        return jsonRequest;
    }

    public static JSONObject createJSONObject(String color) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("color", color);

        } catch (JSONException e) {
            // callback.onSetColorFailure("Request konnte nicht erstellt werden!");
        }

        return jsonRequest;
    }

    public static Request createGetRequest(String token, String path) {
        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .header("Authorization", token)
                .build();
    }

    public static Request createPostRequest(JSONObject jsonRequest, String token, String path) {
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));
        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .header("Authorization", token)
                .post(requestBody)
                .build();
    }

    public static void executeJoinSessionRequest(OkHttpClient okHttpClient, Request request, final JoinSessionCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
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
                        String successMessage = jsonResponse.getString("message");
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

    public static void executeGetUnavailableColorsByLobbyRequest(OkHttpClient okHttpClient, Request request, final GetUnavailableColorsByLobbyCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onGetUnavailableColorsByLobbyFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            List<Color> unavailableColors = getColors(responseBody.string());
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

    public static void executeGetStatusByLobbyRequest(OkHttpClient okHttpClient, Request request, GetStatusByLobbyCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onGetStatusByLobbyFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String responseString = responseBody.string();
                        JSONArray responseArray = new JSONArray(responseString);
                        HashMap<UUID, Session> sessions = new HashMap<>();
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject jsonSession = responseArray.getJSONObject(i);
                            UUID userid = UUID.fromString(jsonSession.getString("userid"));
                            String email = jsonSession.getString("email");
                            Color color = convertToEnums(jsonSession.getString("color"));
                            int currentPosition = jsonSession.getInt("currentposition");
                            int balance = jsonSession.getInt("balance");
                            boolean isPlayersTurn = jsonSession.getBoolean("isplayersturn");
                            Session session = new Session(userid, email, color, currentPosition, balance, 0, 0, 0, isPlayersTurn);
                            sessions.put(userid, session);
                        }
                        callback.onGetStatusByLobbySuccess(sessions);
                    } else {
                        callback.onGetStatusByLobbyFailure(response.message());
                    }
                } catch (JSONException e) {
                    callback.onGetStatusByLobbyFailure("Fehler beim Lesen der Response!");
                }
            }
        });
    }

    public static void executeSetColorRequest(OkHttpClient okHttpClient, Request request, SetColorCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
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
                        String successMessage = jsonResponse.getString("message");
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

    @NonNull
    private static List<Color> getColors(String responseBodyString) throws JSONException {
        JSONArray responseArray = new JSONArray(responseBodyString);
        List<Color> unavailableColors=new ArrayList<>();
        for(int i = 0; i < responseArray.length(); i++) {
            JSONObject jsonSession = responseArray.getJSONObject(i);
            String color = jsonSession.getString("color");
            Color enumValueOfColor=convertToEnums(color);
            unavailableColors.add(enumValueOfColor);
        }
        return unavailableColors;
    }
}
