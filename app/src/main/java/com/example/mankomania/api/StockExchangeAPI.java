package com.example.mankomania.api;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class StockExchangeAPI {
    private static final String SERVER = HttpClient.getServer();
    private static final int PORT = HttpClient.getPort();
    private static final String RESPONSE_FAILURE_MESSAGE = "Fehler beim Lesen der Response: ";
    private static final String HEADER_AUTHORIZATION_KEY = "Authorization";

    public interface GetStockChanges {
        void onGetStockChangesSuccess(String stockChanges);

        void onGetStockChangesFailure(String errorMessage);
    }

    public static void getStockChangesByLobbyID(String token, UUID lobbyid, final StockExchangeAPI.GetStockChanges callback) {
        // create request
        Request request = createGetRequest(token, "/api/stockexchange/getStockChanges/" + lobbyid.toString());

        // execute request
        executeRequest(HttpClient.getHttpClient(), request, callback);
    }

    public static Request createGetRequest(String token, String path) {
        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .header(HEADER_AUTHORIZATION_KEY, token)
                .build();
    }

    public static void executeRequest(OkHttpClient okHttpClient, Request request, final StockExchangeAPI.GetStockChanges callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onGetStockChangesFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            String stockChanges = getColors(responseBody.string());
                            callback.onGetStockChangesSuccess(stockChanges);
                        } else {
                            callback.onGetStockChangesFailure("Response Body ist leer!");
                        }
                    } catch (JSONException e) {
                        callback.onGetStockChangesFailure(RESPONSE_FAILURE_MESSAGE + e.getMessage());
                    }
                } else {
                    callback.onGetStockChangesFailure(response.message());
                }
            }
        });
    }

    @NonNull
    public static String getColors(String responseBodyString) throws JSONException {
        JSONArray responseArray = new JSONArray(responseBodyString);
        String stockChanges = "";
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject jsonSession = responseArray.getJSONObject(i);
            stockChanges = jsonSession.getString("stockChange");
        }
        return stockChanges;
    }

}
