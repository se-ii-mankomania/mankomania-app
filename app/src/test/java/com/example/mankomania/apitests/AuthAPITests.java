package com.example.mankomania.apitests;

import com.example.mankomania.api.AuthAPI;

import static org.junit.jupiter.api.Assertions.*;

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
}
