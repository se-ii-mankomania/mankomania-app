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
    private static final String RESPONSE_FAILURE_MESSAGE = "Fehler beim Lesen der Response: ";
   private static final String JSON_RESPONSE_MESSAGE_KEY = "message";
   private static final String HEADER_AUTHORIZATION_KEY = "Authorization";

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
        void onGetStatusByLobbySuccess(HashMap<UUID, PlayerSession> sessions);

        void onGetStatusByLobbyFailure(String errorMessage);
    }

    public interface SetColorCallback {
        void onSetColorSuccess(String successMessage);

        void onSetColorFailure(String errorMessage);
    }

    public static void updatePlayerPosition(String token, String userID, int currentposition, String lobbyID, final UpdatePositionCallback callback) {
        // create JSON object for request
        JSONObject jsonRequest = createJSONObject(userID, currentposition);

        // create Request
        Request request = createPostRequest(jsonRequest, token, "/api/session/setPosition/" + lobbyID);

        // execute request
        executeUpdatePlayerPosition(HttpClient.getHttpClient(), request, callback);
    }

    /**
     * call this method to join a session
     * @param token: authentication token (generated at login)
     * @param lobbyid: ID of selected lobby from screen
     * @param callback: use JoinSessionCallback
     */
    public static void joinSession(String token, UUID lobbyid, final SessionAPI.JoinSessionCallback callback) {
        // create JSONObject that holds lobbyID
        JSONObject jsonRequest = createJSONObject(lobbyid);

        // create request
        Request request = createPostRequest(jsonRequest, token, "/api/session/initialize");

        // execute request
        executeJoinSessionRequest(HttpClient.getHttpClient(), request, callback);
    }

    /**
     * call this method to see which colors are unavailable
     * @param token: authentication token (generated at login)
     * @param lobbyid: ID of selected lobby from shared preferences
     * @param callback: use GetUnavailableColorsByLobbyCallback
     */
    public static void getUnavailableColorsByLobby(String token, UUID lobbyid, final SessionAPI.GetUnavailableColorsByLobbyCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/session/unavailableColors/" + lobbyid.toString());

        // execute request
        executeGetUnavailableColorsByLobbyRequest(HttpClient.getHttpClient(), request, callback);
    }

    /**
     * call this method to get the current game status of the game
     * @param token: authentication token (generated at login)
     * @param lobbyid: ID of selected lobby from shared preferences
     * @param callback: use GetStatusByLobbyCallback
     */
    public static void getStatusByLobby(String token, UUID lobbyid, final SessionAPI.GetStatusByLobbyCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/session/status/" + lobbyid.toString());

        // execute request
        executeGetStatusByLobbyRequest(HttpClient.getHttpClient(), request, callback);
    }

    /**
     * call this method to choose a color
     * @param token: authentication token (generated at login)
     * @param lobbyid: ID of selected lobby from shared preferences
     * @param color: Color Object
     * @param callback: use SetColorCallback
     */
    public static void setColor(String token, UUID lobbyid, String color, final SessionAPI.SetColorCallback callback) {
        // create JSONObject that holds color
        JSONObject jsonRequest = createJSONObject(color);

        // create request
        Request request = createPostRequest(jsonRequest, token, "/api/session/setColor/" + lobbyid.toString());

        // execute request
        executeSetColorRequest(HttpClient.getHttpClient(), request, callback);
    }

    /**
     * converts a string to Color object
     * @param color: String ("blue", "red", "green", "lila")
     * @return accoring Color object
     */
    public static Color convertToEnums(String color) {
        try {
            return Color.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * get a list of colors from a String (generated off a ResponseBody Object)
     * @param responseBodyString: ResponseBody.string()
     * @return list of Color objects
     * @throws JSONException when JSONObject handling goes wrong
     */
    @NonNull
    public static List<Color> getColors(String responseBodyString) throws JSONException {
        JSONArray responseArray = new JSONArray(responseBodyString);
        List<Color> unavailableColors = new ArrayList<>();
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject jsonSession = responseArray.getJSONObject(i);
            String color = jsonSession.getString("color");
            Color enumValueOfColor = convertToEnums(color);
            unavailableColors.add(enumValueOfColor);
        }
        return unavailableColors;
    }

    /**
     * puts session represented by a JSONArray into a HashMap
     * @param responseArray: JSONArray containing sessions
     * @return HashMap containing sessions, mapped by userID
     * @throws JSONException when JSONObject handling goes wrong
     */
    public static HashMap<UUID, PlayerSession> createSessions(JSONArray responseArray) throws JSONException {
        HashMap<UUID, PlayerSession> sessions = new HashMap<>();
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject jsonSession = responseArray.getJSONObject(i);
            UUID userid = UUID.fromString(jsonSession.getString("userid"));
            String email = jsonSession.getString("email");
            String colorString = jsonSession.getString("color");
            Color color = convertToEnums(colorString);
            int currentPosition = jsonSession.getInt("currentposition");
            int balance = jsonSession.getInt("balance");
            boolean isPlayersTurn = jsonSession.getBoolean("isplayersturn");
            int minigame=jsonSession.getInt("minigame");
            int amountkvshares=jsonSession.getInt("amountkvshares");
            int amounttshares=jsonSession.getInt("amounttshares");
            int amountbshares= jsonSession.getInt("amountbshares");

            if (color != null) {
                PlayerSession playerSession = new PlayerSession(userid, email, color, currentPosition, balance, amountkvshares, amounttshares, amountbshares, isPlayersTurn);
                sessions.put(userid, playerSession);
                SessionStatusService sessionStatusService = SessionStatusService.getInstance();
                sessionStatusService.notifyUpdatesInSession(playerSession, userid);
                sessionStatusService.notifyUpdatesForMiniGames(minigame);
            }
        }
        return sessions;
    }

    /**
     * creates a JSONObject representing a lobby ID
     * @param lobbyid: UUID of a lobby
     * @return JSONObject representing a lobby ID
     */
    public static JSONObject createJSONObject(UUID lobbyid) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("lobbyid", lobbyid);

        } catch (JSONException ignored) {

        }

        return jsonRequest;
    }

    /**
     * creates a JSONObject representing a color
     * @param color: String representation of a Color object
     * @return JSONObject representing a color
     */
    public static JSONObject createJSONObject(String color) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("color", color);

        } catch (JSONException ignored) {

        }

        return jsonRequest;
    }

    /**
     * creates a JSONObject representing a userID and currentposition
     * @param userID: String representing userID
     * @param currentposition: Integer representing current position
     * @return JSONObject representing a userID and currentposition
     */
    public static JSONObject createJSONObject(String userID, int currentposition) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userID);
            jsonRequest.put("currentposition", currentposition);
        } catch (JSONException ignored) {

        }
        return jsonRequest;
    }

    /**
     * builds a GET request with authorisation header
     * @param token: authentication token (generated at login)
     * @param path: path to server endpoint
     * @return Request to be executed by execute[..](..)
     */
    public static Request createGetRequest(String token, String path) {
        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .header(HEADER_AUTHORIZATION_KEY, token)
                .build();
    }

    /**
     * builds a POST request
     * @param jsonRequest: data to be posted
     * @param token: authentication token (generated at login)
     * @param path: path to server endpoint
     * @return Request to be executed by execute[..](..)
     */
    public static Request createPostRequest(JSONObject jsonRequest, String token, String path) {
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));
        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .header(HEADER_AUTHORIZATION_KEY, token)
                .post(requestBody)
                .build();
    }

    /**
     * executes Request for joining sessions
     * @param okHttpClient: use HttpClient.getHttpClient() to get singleton instance
     * @param request: POST request with lobbyid
     * @param callback: JoinSessionCallback
     */
    public static void executeJoinSessionRequest(OkHttpClient okHttpClient, Request request, final JoinSessionCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onJoinSessionFailure("Keine Antwort!");
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
                        callback.onJoinSessionSuccess(successMessage);
                    } catch (JSONException e) {
                        callback.onJoinSessionFailure(RESPONSE_FAILURE_MESSAGE + e.getMessage());
                    }
                } else {
                    callback.onJoinSessionFailure(response.message());
                }
            }
        });
    }

    /**
     * executes Request for getting unavailable colors
     * @param okHttpClient: use HttpClient.getHttpClient() to get singleton instance
     * @param request: GET Request
     * @param callback: GetUnavailableColorsByLobbyCallback
     */
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
                        callback.onGetUnavailableColorsByLobbyFailure(RESPONSE_FAILURE_MESSAGE + e.getMessage());
                    }
                } else {
                    callback.onGetUnavailableColorsByLobbyFailure(response.message());
                }
            }
        });
    }

    /**
     * executes Request for getting game status of a game
     * @param okHttpClient: use HttpClient.getHttpClient() to get singleton instance
     * @param request: GET Request
     * @param callback: GetStatusByLobbyCallback
     */
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
                        JSONArray responseArray = new JSONArray(responseBody.string());
                        HashMap<UUID, PlayerSession> sessions = createSessions(responseArray);
                        callback.onGetStatusByLobbySuccess(sessions);
                    } else {
                        callback.onGetStatusByLobbyFailure(response.message());
                    }
                } catch (JSONException e) {
                    callback.onGetStatusByLobbyFailure(RESPONSE_FAILURE_MESSAGE + e.getMessage());
                }
            }
        });
    }

    /**
     * executes a request for choosing a color
     * @param okHttpClient: use HttpClient.getHttpClient() to get singleton instance
     * @param request: POST Request with color
     * @param callback: SetColorCallback
     */
    public static void executeSetColorRequest(OkHttpClient okHttpClient, Request request, SetColorCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onSetColorFailure("Keine Antwort!");
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
                        callback.onSetColorSuccess(successMessage);
                    } catch (JSONException e) {
                        callback.onSetColorFailure(RESPONSE_FAILURE_MESSAGE + e.getMessage());
                    }
                } else {
                    callback.onSetColorFailure(response.message());
                }
            }
        });
    }

    public static void executeUpdatePlayerPosition(OkHttpClient okHttpClient, Request request, UpdatePositionCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
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
                        String message = jsonResponse.getString(JSON_RESPONSE_MESSAGE_KEY);
                        callback.onUpdateSuccess(message);
                    } catch (JSONException e) {
                        callback.onUpdateFailure(RESPONSE_FAILURE_MESSAGE);
                    }
                } else {
                    callback.onUpdateFailure("Aktualisierung fehlgeschlagen, Antwortcode: " + response.code());
                }
            }
        });
    }
}
