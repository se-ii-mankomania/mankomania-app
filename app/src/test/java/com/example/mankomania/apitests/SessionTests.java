package com.example.mankomania.apitests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.mankomania.api.Session;
import com.example.mankomania.logik.spieler.Color;

import java.util.UUID;

public class SessionTests {

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
        Session session = new Session(userId, email, color, currentPosition, balance, amountKVShares, amountTShares, amountBShares, isPlayersTurn);

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
        assertEquals(expectedToString, session.toString());
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
        Session session = new Session(userId, email, color, currentPosition, balance, amountKVShares, amountTShares, amountBShares, isPlayersTurn);

        // assert
        assertEquals(userId, session.getUserId());
        assertEquals(email, session.getEmail());
        assertEquals(color, session.getColor());
        assertEquals(currentPosition, session.getCurrentPosition());
        assertEquals(balance, session.getBalance());
        assertEquals(amountKVShares, session.getAmountKVShares());
        assertEquals(amountTShares, session.getAmountTShares());
        assertEquals(amountBShares, session.getAmountBShares());
        assertEquals(isPlayersTurn, session.isPlayersTurn());
    }
}
