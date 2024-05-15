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

    @Test
    void testConstructorAndGetters() {
        UUID id = UUID.randomUUID();
        String name = "Test Lobby";
        String password = "password";
        boolean isPrivate = true;
        int maxPlayers = 4;
        Status status = Status.open;

        Lobby lobby = new Lobby(id, name, password, isPrivate, maxPlayers, status);

        assertEquals(id, lobby.getId());
        assertEquals(name, lobby.getName());
        assertEquals(password, lobby.getPassword());
        assertTrue(lobby.isPrivate());
        assertEquals(maxPlayers, lobby.getMaxPlayers());
        assertEquals(status, lobby.getStatus());
    }

    @Test
    void testSetters() {
        UUID id = UUID.randomUUID();
        Lobby lobby = new Lobby(id, "Test Lobby", "", false, 2, Status.open);

        UUID newId = UUID.randomUUID();
        String newName = "New Lobby";
        String newPassword = "newPassword";
        boolean newIsPrivate = true;
        int newMaxPlayers = 3;
        Status newStatus = Status.closed;

        lobby.setId(newId);
        lobby.setName(newName);
        lobby.setPassword(newPassword);
        lobby.setPrivate(newIsPrivate);
        lobby.setMaxPlayers(newMaxPlayers);
        lobby.setStatus(newStatus);

        assertEquals(newId, lobby.getId());
        assertEquals(newName, lobby.getName());
        assertEquals(newPassword, lobby.getPassword());
        assertTrue(lobby.isPrivate());
        assertEquals(newMaxPlayers, lobby.getMaxPlayers());
        assertEquals(newStatus, lobby.getStatus());
    }
}

