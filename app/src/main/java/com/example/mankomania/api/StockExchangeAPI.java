package com.example.mankomania.api;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class StockExchangeAPI {
    private static final String SERVER = HttpClient.getServer();
    private static final int PORT = HttpClient.getPort();
    private static final String RESPONSE_FAILURE_MESSAGE = "Fehler beim Lesen der Response: ";
    private static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    private static final String JSON_RESPONSE_MESSAGE_KEY = "message";

    public interface GetStockChangesCallback {
        void onGetStockChangesSuccess(String stockChanges);

        void onGetStockChangesFailure(String errorMessage);
    }
    public interface GetStockTrendCallback {
        void onGetStockTrendSuccess(String stocktrend);

        void onGetStockTrendFailure(String errorMessage);
    }
    public interface SetStockTrendCallback {
        void onSetStockTrendSuccess(String successMessage);

        void onSetStockTrendFailure(String errorMessage);
    }

    public interface StartStockExchangeCallback {
        void onStartStockExchangeSuccess(String successMessage);

        void onStartStockExchangeFailure(String errorMessage);
    }
    public interface StopStockExchangeCallback {
        void onStopStockExchangeSuccess(String successMessage);

        void onStopStockExchangeFailure(String errorMessage);
    }

    public static void getStockChangesByLobbyID(String token, UUID lobbyid, final GetStockChangesCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/stockexchange/getStockChanges/" + lobbyid.toString());

        // execute request
        executeRequest(HttpClient.getHttpClient(), request, callback);
    }

    public static void getStockTrendByLobbyID(String token, UUID lobbyid, final GetStockTrendCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/stockexchange/getStockTrendByLobbyID/" + lobbyid.toString());

        // execute request
        executeRequest(HttpClient.getHttpClient(), request, callback);
    }

    public static void startStockExchange(String token, UUID lobbyid, final StartStockExchangeCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/stockexchange/startStockExchange/" + lobbyid.toString());

        // execute request
        executeRequest(HttpClient.getHttpClient(), request, callback);
    }
    public static void stopStockExchange(String token, UUID lobbyid, final StopStockExchangeCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/stockexchange/stopStockExchange/" + lobbyid.toString());

        // execute request
        executeRequest(HttpClient.getHttpClient(), request, callback);
    }



    public static Request createGetRequest(String token, String path) {
        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .header(HEADER_AUTHORIZATION_KEY, token)
                .build();
    }

    public static void executeRequest(OkHttpClient okHttpClient, Request request, final GetStockChangesCallback callback) {
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
                            String stockChanges = getStockChanges(responseBody.string());
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
    public static void executeRequest(OkHttpClient okHttpClient, Request request, final GetStockTrendCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onGetStockTrendFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            String stocktrend = getStockTrend(responseBody.string());
                            callback.onGetStockTrendSuccess(stocktrend);
                        } else {
                            callback.onGetStockTrendFailure("Response Body ist leer!");
                        }
                    } catch (JSONException e) {
                        callback.onGetStockTrendFailure(RESPONSE_FAILURE_MESSAGE + e.getMessage());
                    }
                } else {
                    callback.onGetStockTrendFailure(response.message());
                }
            }
        });
    }

    public static void executeRequest(OkHttpClient okHttpClient, Request request, final StartStockExchangeCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onStartStockExchangeFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        String message = jsonResponse.getString(JSON_RESPONSE_MESSAGE_KEY);
                        callback.onStartStockExchangeSuccess(message);
                    } catch (JSONException e) {
                        callback.onStartStockExchangeFailure(RESPONSE_FAILURE_MESSAGE + e.getMessage());
                    }
                } else {
                    callback.onStartStockExchangeFailure(response.message());
                }
            }
        });
    }
    public static void executeRequest(OkHttpClient okHttpClient, Request request, final StopStockExchangeCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onStopStockExchangeFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        String message = jsonResponse.getString(JSON_RESPONSE_MESSAGE_KEY);
                        callback.onStopStockExchangeSuccess(message);
                    } catch (JSONException e) {
                        callback.onStopStockExchangeSuccess(RESPONSE_FAILURE_MESSAGE + e.getMessage());
                    }
                } else {
                    callback.onStopStockExchangeFailure(response.message());
                }
            }
        });
    }

    @NonNull
    public static String getStockTrend(String responseBodyString) throws JSONException {
        JSONObject stocktrend=new JSONObject(responseBodyString);
        return stocktrend.getString("stocktrend");
    }

    @NonNull
    public static String getStockChanges(String responseBodyString) throws JSONException {
        JSONObject stockchange=new JSONObject(responseBodyString);
        return stockchange.getString("stockChange");
    }

    public static void setStockTrend(String token, UUID lobbyid, String stockTrend, final SetStockTrendCallback callback) {
        // create JSONObject that holds color
        JSONObject jsonRequest = createJSONObject(stockTrend);

        // create request
        Request request = createPostRequest(jsonRequest, token, "/api/stockexchange/setStockTrend/" + lobbyid.toString());

        // execute request
        executeSetStockTrendRequest(HttpClient.getHttpClient(), request, callback);
    }
    public static JSONObject createJSONObject(String stocktrend) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("stocktrend", stocktrend);

        } catch (JSONException ignored) {

        }

        return jsonRequest;
    }

    public static Request createPostRequest(JSONObject jsonRequest, String token, String path) {
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));
        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .header(HEADER_AUTHORIZATION_KEY, token)
                .post(requestBody)
                .build();
    }

    public static void executeSetStockTrendRequest(OkHttpClient okHttpClient, Request request, SetStockTrendCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onSetStockTrendFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = Objects.requireNonNull(response.body()).string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // return the message
                        String successMessage = jsonResponse.getString(JSON_RESPONSE_MESSAGE_KEY);
                        callback.onSetStockTrendSuccess(successMessage);
                    } catch (JSONException e) {
                        callback.onSetStockTrendFailure(RESPONSE_FAILURE_MESSAGE + e.getMessage());
                    }
                } else {
                    callback.onSetStockTrendFailure(response.message());
                }
            }
        });
    }

}
