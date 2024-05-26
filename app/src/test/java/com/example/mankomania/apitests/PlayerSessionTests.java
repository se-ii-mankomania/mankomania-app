package com.example.mankomania.apitests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.mankomania.api.PlayerSession;
import com.example.mankomania.logik.spieler.Color;

import java.util.UUID;

class PlayerSessionTests {

    @Test
    void testToString() {
        // set data
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        Color color = Color.RED;
        int currentPosition = 10;
        int balance = 1000;
        int amountKVShares = 5;
        int amountTShares = 3;
        int amountBShares = 2;
        boolean isPlayersTurn = true;

        // create session
        PlayerSession playerSession = new PlayerSession(userId, email, color, currentPosition, balance, amountKVShares, amountTShares, amountBShares, isPlayersTurn);

        // get string
        String expectedToString = "Session{" +
                "userId=" + userId +
                ", email='" + email +
                ", color='" + color + '\'' +
                ", currentPosition=" + currentPosition +
                ", balance=" + balance +
                ", amountKVShares=" + amountKVShares +
                ", amountTShares=" + amountTShares +
                ", amountBShares=" + amountBShares +
                ", isPlayersTurn=" + isPlayersTurn +
                '}';

        // assert
        assertEquals(expectedToString, playerSession.toString());
    }

    @Test
    void testSessionConstructorAndGetters() {
        // set data
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        Color color = Color.RED;
        int currentPosition = 10;
        int balance = 1000;
        int amountKVShares = 5;
        int amountTShares = 3;
        int amountBShares = 2;
        boolean isPlayersTurn = true;

        // create session
        PlayerSession playerSession = new PlayerSession(userId, email, color, currentPosition, balance, amountKVShares, amountTShares, amountBShares, isPlayersTurn);

        // assert
        assertEquals(userId, playerSession.getUserId());
        assertEquals(email, playerSession.getEmail());
        assertEquals(color, playerSession.getColor());
        assertEquals(currentPosition, playerSession.getCurrentPosition());
        assertEquals(balance, playerSession.getBalance());
        assertEquals(amountKVShares, playerSession.getAmountKVShares());
        assertEquals(amountTShares, playerSession.getAmountTShares());
        assertEquals(amountBShares, playerSession.getAmountBShares());
        assertEquals(isPlayersTurn, playerSession.getIsPlayersTurn());
    }
}
