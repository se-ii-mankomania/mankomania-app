package com.example.mankomania.apitests;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.mankomania.logik.spieler.Color;
import com.example.mankomania.api.SessionAPI;

import java.util.UUID;

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
}
