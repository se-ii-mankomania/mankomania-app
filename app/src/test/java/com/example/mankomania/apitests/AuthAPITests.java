package com.example.mankomania.apitests;

import com.example.mankomania.api.AuthAPI;

import static org.junit.jupiter.api.Assertions.*;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class AuthAPITests {

    @ParameterizedTest
    @ValueSource(strings = {"test@test.at, my_password", ", my_password", "test@test.at, ", ","})
    void testCreateJSONRequest(String emailAndPassword) throws JSONException {
        // splitting generated with ChatGPT since i couldn't figure it out myself :(
        String[] parts = emailAndPassword.split(",", 2);
        String email = parts.length > 0 ? parts[0].trim() : "";
        String password = parts.length > 1 ? parts[1].trim() : "";

        JSONObject result = AuthAPI.createJSONRequest(email, password);

        assertNotNull(result);

        assertEquals(email, result.getString("email"));
        assertEquals(password, result.getString("password"));
    }

    @Test
    void testCreateJSONRequestNull() throws JSONException {
        String email = null;
        String password = null;

        JSONObject result = AuthAPI.createJSONRequest(email, password);

        assertNotNull(result);

        assertFalse(result.has("email"));
        assertFalse(result.has("password"));
    }

    @Test
    void testCreateRequest() throws JSONException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", "test@test.at");
        jsonRequest.put("password", "my_password");
        String path = "/some/path";

        Request request = AuthAPI.createRequest(jsonRequest, path);

        // creating a Request expectedRequest and calling assertEquals(expectedRequest, request);
        // does not work here (AssertionFailedError since the references differ)

        assertEquals("http://10.0.2.2:3000/some/path", request.url().toString());
        assertEquals("POST", request.method());
        assertNotNull(request.body());
    }
}
