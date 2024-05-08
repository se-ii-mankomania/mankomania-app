package com.example.mankomania.apitests;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.mankomania.logik.spieler.Color;
import com.example.mankomania.api.SessionAPI;

import java.util.UUID;

import okhttp3.Request;

public class SessionAPITests {
    @Test
    void testConvertToEnums() {
        assertEquals(Color.BLUE, SessionAPI.convertToEnums("blue"));
        assertEquals(Color.RED, SessionAPI.convertToEnums("red"));
        assertEquals(Color.GREEN, SessionAPI.convertToEnums("green"));
        assertEquals(Color.PURPLE, SessionAPI.convertToEnums("lila"));

        assertNull(SessionAPI.convertToEnums("invalid_color"));
    }

    @Test
    void testCreateJSONObject_UUID() {
        // prepare input
        UUID lobbyid = UUID.randomUUID();

        // call method
        JSONObject jsonObject = SessionAPI.createJSONObject(lobbyid);

        // assert
        assertTrue(jsonObject.has("lobbyid"));
    }

    @Test
    void testCreateJSONObject_Color() {
        // prepare input
        String color = "Rot";

        // call method
        JSONObject jsonObject = SessionAPI.createJSONObject(color);

        // assert
        assertTrue(jsonObject.has("color"));
    }

    @Test
    void testCreateGetRequest() {
        // set up
        String token = "test_token";
        String path = "/api/session/unavailableColors/1234";

        // call method
        Request request = SessionAPI.createGetRequest(token, path);

        // assert
        assertEquals("http://10.0.2.2:3000/api/session/unavailableColors/1234", request.url().toString());
    }

    @Test
    void testCreatePostRequest() throws JSONException {
        // set up
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("color", "Rot");

        String token = "test_token";
        String path = "/api/session/setColor/1234";

        // call method
        Request request = SessionAPI.createPostRequest(jsonObject, token, path);

        // assert
        assertEquals("http://10.0.2.2:3000/api/session/setColor/1234", request.url().toString());
    }
}
