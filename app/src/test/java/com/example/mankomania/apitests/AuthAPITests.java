package com.example.mankomania.apitests;

import com.example.mankomania.api.AuthAPI;

import static org.junit.jupiter.api.Assertions.*;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class AuthAPITests {
    @Test
    void testCreateJSONRequestNormal() throws JSONException {
        String email = "test@test.at";
        String password = "my_password";

        JSONObject result = AuthAPI.createJSONRequest(email, password);

        assertNotNull(result);

        assertEquals(email, result.getString("email"));
        assertEquals(password, result.getString("password"));
    }

    @Test
    void testCreateJSONRequestEmptyPassword() throws JSONException {
        String email = "test@test.at";
        String password = "";

        JSONObject result = AuthAPI.createJSONRequest(email, password);

        assertNotNull(result);

        assertEquals(email, result.getString("email"));
        assertEquals(password, result.getString("password"));
    }

    @Test
    void testCreateJSONRequestEmptyEmail() throws JSONException {
        String email = "";
        String password = "my_password";

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
