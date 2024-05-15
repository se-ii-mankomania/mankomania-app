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
    private static final String SERVER = HttpClient.getServer();
    private static final int PORT = HttpClient.getPort();

    // interface to notify whether auth operation was successful or not
    public interface LoginCallback {
        void onLoginSuccess(String token, String userId);
        void onLoginFailure(String errorMessage);
    }

    public interface RegisterCallback {
        void onRegisterSuccess(String message);
        void onRegisterFailure(String errorMessage);
    }

    /**
     * this method sends the user credentials to the server
     * on success, the server responds with a token, which is needed later for more requests
     */
    public static void login(String email, String password, final LoginCallback callback) {
        // create JSON Object that holds email and password
        JSONObject jsonRequest = createJSONRequest(email, password);

        // create Request
        Request request = createRequest(jsonRequest, "/api/auth/login");

        // execute request (at some point)
        executeLoginRequest(HttpClient.getHttpClient(), request, callback);
    }

    /**
     * this method tries to register a user
     * on success, the new user gets added into the db
     * the server will provide a feedback
     */
    public static void register(String email, String password, final RegisterCallback callback) {
        // create JSON Object that holds email and password
        JSONObject jsonRequest = createJSONRequest(email, password);

        // create Request
        Request request = createRequest(jsonRequest, "/api/auth/register");

        // execute request (at some point)
        executeRegisterRequest(HttpClient.getHttpClient(), request, callback);
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
        } catch (JSONException ignored) {

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
     * executes the login request
     * @param okHttpClient: use HttpClient.getHttpClient() to get singleton instance
     * @param request: prepared Request object
     * @param callback: use LoginCallback
     */
    public static void executeLoginRequest(OkHttpClient okHttpClient, Request request, final LoginCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onLoginFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // get token and userId
                        String token = jsonResponse.getString("token");
                        String userId= jsonResponse.getString("userId");
                        callback.onLoginSuccess(token, userId);
                    } catch (JSONException e) {
                        callback.onLoginFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onLoginFailure("Falsche Credentials!");
                }
            }
        });
    }

    /**
     * executes the register request
     * @param okHttpClient: use HttpClient.getHttpClient() to get singleton instance
     * @param request: prepared Request object
     * @param callback: use RegisterCallback
     */
    public static void executeRegisterRequest(OkHttpClient okHttpClient, Request request, final RegisterCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onRegisterFailure("Keine Antwort!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        // create JSON object for response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // get message
                        String message = jsonResponse.getString("message");

                        callback.onRegisterSuccess(message);
                    } catch (JSONException e) {
                        callback.onRegisterFailure("Fehler beim Lesen der Response!");
                    }
                } else {
                    callback.onRegisterFailure("User bereits registriert!");
                }
            }
        });
    }
}
