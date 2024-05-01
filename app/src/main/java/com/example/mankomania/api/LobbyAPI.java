package com.example.mankomania.api;

import static org.json.JSONObject.NULL;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LobbyAPI {
    // must be changed later when server is deployed
    // 10.0.2.2 to reach localhost of development machine
    private static final String SERVER = "http://10.0.2.2";
    private static final int PORT = 3000;

    public interface GetLobbiesCallback {
        void onSuccess(String[] lobbies);
        void onFailure(String errorMessage);
    }

    public interface AddLobbyCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    /**
     * this method gets all lobbies from the db
     * and stores the names of the lobby into lobbyNames
     * TODO: other properties
     */
    public static void getLobbies(String token, final GetLobbiesCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/lobby/getAll", null);

        // execute request
        executeGetRequest(HttpClient.getHttpClient(), request, callback);
    }

    public static void getLobbiesByStatus(String token, Status status, final GetLobbiesCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/lobby/getByStatus/", status);

        // execute request
        executeGetRequest(HttpClient.getHttpClient(), request, callback);
    }

    /**
     * this method tries to add a lobby to the db
     */
    public static void addLobby(String token, String name, String password, boolean isPrivate, int maxPlayer, Status status, final AddLobbyCallback callback) {
        // create JSONObject for Lobby that should be added
        JSONObject jsonRequest = createJSONLobby(name, password, isPrivate, maxPlayer, status);

        // create request
        Request request = createPostRequest(jsonRequest, "/api/lobby/create", token);

        // execute request (at some point)
        executePostRequest(HttpClient.getHttpClient(), request, callback);
    }

    /**
     * this method takes a JSONObject that represents a lobby, creates an equivalent Lobby object
     * and adds it to the lobbies list
     */
    private static void addLobbyToList(JSONObject jsonLobby, List<Lobby> list) throws JSONException {
        UUID id = UUID.fromString(jsonLobby.getString("id"));
        String name = jsonLobby.getString("name");
        String password = jsonLobby.getString("password");
        boolean isPrivate = jsonLobby.getBoolean("isprivate");
        int maxPlayers = jsonLobby.getInt("maxplayers");
        Status status;

        switch (jsonLobby.getString("status")) {
            case "open":
                status = Status.open;
                break;

            case "starting":
                status = Status.starting;
                break;

            case "inGame":
                status = Status.inGame;
                break;

            case "finished":
                status = Status.finished;
                break;

            case "closed":
                status = Status.closed;
                break;

            default:
                throw new IllegalArgumentException("Ungültiger Status!");
        }

        Lobby lobby = new Lobby(id, name, password, isPrivate, maxPlayers, status);
        list.add(lobby);
    }

    private static String[] generateStringArray(List<Lobby> lobbies) {
        String[] lobbiesStringArray = new String[lobbies.size()];

        for (int i = 0; i < lobbies.size(); i++) {
            Lobby lobby = lobbies.get(i);
            String string = "";

            if(lobby.isPrivate()) {
                string += "P";
            } else {
                string += "O";
            }
            string += " | ";
            string += "x/" + lobby.getMaxPlayers();
            string += " | ";
            string += lobby.getName();

            lobbiesStringArray[i] = string;
        }

        return lobbiesStringArray;
    }

    private static JSONObject createJSONLobby(String name, String password, boolean isPrivate, int maxPlayer, Status status) {
        JSONObject jsonLobby = new JSONObject();
        try {
            jsonLobby.put("name", name);

            // JSONObjects need special NULL Value from library instead of standard null value
            // this if-else is needed to pass null value into DB for no-password-lobbies
            if(password == null) {
                jsonLobby.put("password", NULL);
            } else {
                jsonLobby.put("password", password);
            }

            jsonLobby.put("isPrivate", isPrivate);
            jsonLobby.put("maxPlayers", maxPlayer);
            jsonLobby.put("status", status);
        } catch (JSONException e) {
            // TODO: display error in some way
        }
        return jsonLobby;
    }

    private static Request createGetRequest(String token, String path, Status status) {
        // set up url depending on status
        String url = SERVER + ":" + PORT + path;
        if (status != null) {
            url += status.toString();
        }

        return new Request.Builder()
                .url(url)
                .header("Authorization", token)
                .build();
    }
    private static Request createPostRequest(JSONObject jsonRequest, String path, String token) {
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));

        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .header("Authorization", token)
                .post(requestBody)
                .build();
    }

    private static void executePostRequest(OkHttpClient okHttpClient, Request request, final AddLobbyCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // return the message
                        String message = jsonResponse.getString("message");
                        callback.onSuccess(message);
                    } catch (JSONException e) {
                        callback.onFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onFailure(response.message());
                }
            }
        });
    }

    private static void executeGetRequest(OkHttpClient okHttpClient, Request request, final GetLobbiesCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        // get JSONArray with lobbies
                        JSONArray responseArray = new JSONArray(responseBody);

                        // create list for storing them
                        List<Lobby> lobbies = new ArrayList<>();

                        // go through array
                        for (int i = 0; i < responseArray.length(); i++) {
                            // get JSONObject that represents single lobby
                            JSONObject jsonLobby = responseArray.getJSONObject(i);

                            // add to list
                            addLobbyToList(jsonLobby, lobbies);
                        }

                        // generate String array for displaying lobbies
                        String[] lobbiesStringArray = new String[responseArray.length()];
                        lobbiesStringArray = generateStringArray(lobbies);

                        callback.onSuccess(lobbiesStringArray);
                    } catch (JSONException e) {
                        callback.onFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onFailure(response.message());
                }
            }
        });
    }

}
