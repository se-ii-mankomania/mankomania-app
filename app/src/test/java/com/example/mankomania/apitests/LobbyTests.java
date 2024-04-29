package com.example.mankomania.apitests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.mankomania.api.Lobby;
import com.example.mankomania.api.Status;

import java.util.UUID;

class LobbyTests {

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        String name = "Test Lobby";
        String password = "password123";
        boolean isPrivate = true;
        int maxPlayers = 4;
        Status status = Status.open;
        Lobby lobby = new Lobby(id, name, password, isPrivate, maxPlayers, status);

        String expectedToString = "ID: " + id.toString() + "\n" +
                "Name: " + name + "\n" +
                "Password: " + password + "\n" +
                "Private: " + isPrivate + "\n" +
                "Max. Players: " + maxPlayers + "\n" +
                "Status: " + status;

        assertEquals(expectedToString, lobby.toString());
    }
}

