package com.example.mankomania.api;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HorseRaceAPI {
    private static final String SERVER = HttpClient.getServer();
    private static final int PORT = HttpClient.getPort();
    private static final String RESPONSE_FAILURE_MESSAGE = "Fehler beim Lesen der Response: ";
    private static final String HEADER_AUTHORIZATION_KEY = "Authorization";

    public interface GetHorseRaceResultsCallback {
        void onGetHorseRaceResultsSuccess(int[] horsePlaces);
        void onGetHorseRaceResultsFailure(String errorMessage);
    }

    public static Request createPostRequest(String token, String path, JSONObject jsonObject) {
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .header(HEADER_AUTHORIZATION_KEY, token)
                .post(requestBody)
                .build();
    }

    public static void startHorseRace(String token, UUID lobbyid, String userId, int betValue, int pickedHorse, final HorseRaceAPI.GetHorseRaceResultsCallback callback) {
        JSONObject jsonObject = createJSONObject(userId, betValue, pickedHorse);
        Request request = createPostRequest(token, "/api/horserace/startHorseRace/" + lobbyid.toString(), jsonObject);
        executeRequest(HttpClient.getHttpClient(), request, callback);
    }

    private static JSONObject createJSONObject(String userId, int betValue, int pickedHorse) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userId);
            jsonRequest.put("betValue", betValue);
            jsonRequest.put("pickedHorse", pickedHorse);

        } catch (JSONException ignored) {
            Log.e("Json","JsonRequest failed.");
        }

        return jsonRequest;
    }

    public static void executeRequest(OkHttpClient okHttpClient, Request request, final HorseRaceAPI.GetHorseRaceResultsCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            JSONObject object = new JSONObject(responseBody.string());
                            JSONArray array = object.optJSONArray("horsePlaces");
                            int[] horsePlaces = new int[array.length()];
                            // Extract numbers from JSON array.
                            for (int i = 0; i < array.length(); ++i) {
                                horsePlaces[i] = array.optInt(i);
                            }
                            callback.onGetHorseRaceResultsSuccess(horsePlaces);
                        } else {
                            callback.onGetHorseRaceResultsFailure("Response Body ist leer!");
                        }
                    } catch (JSONException e) {
                        callback.onGetHorseRaceResultsFailure(RESPONSE_FAILURE_MESSAGE + e.getMessage());
                    }
                } else {
                    callback.onGetHorseRaceResultsFailure(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onGetHorseRaceResultsFailure("Keine Antwort!");
            }
        });
    }
}
