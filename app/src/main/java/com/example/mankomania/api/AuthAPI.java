package com.example.mankomania.api;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthAPI {
    // must be changed later when server is deployed
    // 10.0.2.2 to reach localhost of development machine
    private static final String SERVER = "http://10.0.2.2";
    private static final int PORT = 3000;

    // interface to notify whether auth operation was successful or not
    public interface AuthCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    /**
     * this method sends the user credentials to the server
     * on success, the server responds with a token, which is needed later for more requests
     */
    public static void login(String email, String password, final AuthCallback callback) {
        // create JSON Object that holds email and password
        JSONObject jsonRequest = createJSONRequest(email, password);

        // create Request
        Request request = createRequest(jsonRequest, "/api/auth/login");

        // execute request (at some point)
        executeRequest(HttpClient.getHttpClient(), request, "token", "Falsche Credentials!", callback);
    }

    /**
     * this method tries to register a user
     * on success, the new user gets added into the db
     * the server will provide a feedback
     */
    public static void register(String email, String password, final AuthCallback callback) {
        // create JSON Object that holds email and password
        JSONObject jsonRequest = createJSONRequest(email, password);

        // create Request
        Request request = createRequest(jsonRequest, "/api/auth/register");

        // execute request (at some point)
        executeRequest(HttpClient.getHttpClient(), request, "message", "User bereits registriert!", callback);
    }


    /**
     * Hier wird für Login/Registrierung ein JSONObject erstellt, das die benötigten Parameter enthält.
     * Für Auth werden email und password benötigt.
     * @param email: Benutzer-E-Mail (String)
     * @param password: Benutzer-Passwort (String)
     * @return jsonRequest (JSONObject)
     */
    @NonNull
    public static JSONObject createJSONRequest(String email, String password) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("email", email);
            jsonRequest.put("password", password);
        } catch (JSONException e) {
            // TODO: display error in some way
        }
        return jsonRequest;
    }

    /**
     * Hier wird ein Request-Object erzeugt, das die benötigten Informationen (Parameter email und
     * password im Body, Methode = POST, Pfad zur jeweiligen Schnittstelle) enthält.
     * Später wird diese Request über den HTTPClient ausgeführt.
     * @param jsonRequest: in createJSONRequest generiertes JSONObject mit email und password
     * @param path: Path für API
     * @return Request
     */
    public static Request createRequest(JSONObject jsonRequest, String path) {
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json"));

        return new Request.Builder()
                .url(SERVER + ":" + PORT + path)
                .post(requestBody)
                .build();
    }

    /**
     *
     * @param request
     * @param responseParameter
     * @param errorMessage
     * @param callback
     */
    public static void executeRequest(OkHttpClient okHttpClient, Request request, String responseParameter, String errorMessage, final AuthCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // return the token (login) or message (register)
                        String message = jsonResponse.getString(responseParameter);
                        callback.onSuccess(message);
                    } catch (JSONException e) {
                        callback.onFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onFailure(errorMessage);
                }
            }
        });
    }
}
