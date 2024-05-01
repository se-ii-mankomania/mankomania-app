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
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LobbyAPI {
    // must be changed later when server is deployed
    // 10.0.2.2 to reach localhost of development machine
    private static final String SERVER = "http://10.0.2.2";
    private static final int PORT = 3000;
    private static List<Lobby> allLobbies;
    private static List<Lobby> openLobbies;
    private static String[] allLobbiesDisplayStrings;
    private static String[] openLobbiesDisplayStrings;
    private static String message;

    // interface to notify whether login is successful or not
    public interface GetLobbiesCallback {
        void onGetLobbiesSuccess(String[] lobbies);
        void onGetLobbiesFailure(String errorMessage);
    }

    public interface GetLobbiesByStatusCallback {
        void onGetLobbiesByStatusSuccess(String[] lobbies);
        void onGetLobbiesByStatusFailure(String errorMessage);
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

        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onGetLobbiesFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONArray responseArray = new JSONArray(responseBody);
                        allLobbies = new ArrayList<>();
                        allLobbiesDisplayStrings = new String[responseArray.length()];

                        for(int i = 0; i < responseArray.length(); i++) {
                            JSONObject jsonLobby = responseArray.getJSONObject(i);
                            addLobbyToList(jsonLobby, allLobbies);

                            // TODO: replace x with actual value of players in lobby
                            // TODO: find a better way to make it actually look pretty (sprint 3?)
                            // generate string as display for GameScore.java
                            boolean isPrivate = jsonLobby.getBoolean("isprivate");
                            int maxPlayers = jsonLobby.getInt("maxplayers");
                            String name = jsonLobby.getString("name");
                            allLobbiesDisplayStrings[i] = generateString(isPrivate, maxPlayers, name);
                        }

                        callback.onGetLobbiesSuccess(allLobbiesDisplayStrings);
                    } catch (JSONException e) {
                        callback.onGetLobbiesFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onGetLobbiesFailure(response.message());
                }
            }
        });
    }

    public static void getLobbiesByStatus(String token, Status status, final GetLobbiesByStatusCallback callback) {
        // create request
        Request request = createGetRequest(token, "/api/lobby/getByStatus/", status);

        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onGetLobbiesByStatusFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONArray responseArray = new JSONArray(responseBody);
                        openLobbies = new ArrayList<>();
                        openLobbiesDisplayStrings = new String[responseArray.length()];

                        for(int i = 0; i < responseArray.length(); i++) {
                            JSONObject jsonLobby = responseArray.getJSONObject(i);
                            if(status == Status.open) {
                                addLobbyToList(jsonLobby, openLobbies);

                                // TODO: replace x with actual value of players in lobby
                                // TODO: find a better way to make it actually look pretty (sprint 3?)
                                // generate string as display for GameScore.java
                                boolean isPrivate = jsonLobby.getBoolean("isprivate");
                                int maxPlayers = jsonLobby.getInt("maxplayers");
                                String name = jsonLobby.getString("name");
                                openLobbiesDisplayStrings[i] = generateString(isPrivate, maxPlayers, name);
                            }
                        }

                        callback.onGetLobbiesByStatusSuccess(openLobbiesDisplayStrings);
                    } catch (JSONException e) {
                        callback.onGetLobbiesByStatusFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onGetLobbiesByStatusFailure(response.message());
                }
            }
        });
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
        HttpClient.getHttpClient().newCall(request).enqueue(new Callback() {
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
                        message = jsonResponse.getString("message");
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

    private static String generateString(boolean isPrivate, int maxPlayers, String name) {
        String string = "";

        if(isPrivate) {
            string += "P";
        } else {
            string += "O";
        }
        string += " | ";
        string += "x/" + maxPlayers;
        string += " | ";
        string += name;

        return string;
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

}
