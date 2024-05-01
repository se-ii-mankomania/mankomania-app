package com.example.mankomania.apitests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.example.mankomania.api.Lobby;
import com.example.mankomania.api.LobbyAPI;
import com.example.mankomania.api.Status;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class LobbyAPITests {

    @Test
    void testGenerateStringArray() {
        // Create sample Lobby objects
        List<Lobby> lobbies = new ArrayList<>();
        lobbies.add(new Lobby(null, "Lobby 1", "password1234", true, 4, Status.open));
        lobbies.add(new Lobby(null, "Lobby 2", "", false, 2, Status.open));
        lobbies.add(new Lobby(null, "Lobby 3", "password5678", true, 3, Status.open));

        // Generate string array
        String[] expectedArray = {
                "P | x/4 | Lobby 1",
                "O | x/2 | Lobby 2",
                "P | x/3 | Lobby 3"
        };
        String[] resultArray = LobbyAPI.generateStringArray(lobbies);

        // Verify the generated string array
        assertArrayEquals(expectedArray, resultArray);
    }
}
