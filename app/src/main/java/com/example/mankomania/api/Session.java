package com.example.mankomania.api;

import androidx.annotation.NonNull;

import com.example.mankomania.logik.spieler.Color;

import java.util.UUID;

public class Session {
    private final UUID userId;
    private final String email;
    private final Color color;
    private final int currentPosition;
    private final int balance;
    private final int amountKVShares;
    private final int amountTShares;
    private final int amountBShares;
    private final boolean isPlayersTurn;

    // fixme rename class to clarify it reflects player information
    public Session(UUID userId, String email, Color color, int currentPosition, int balance, int amountKVShares, int amountTShares, int amountBShares, boolean isPlayersTurn) {
        this.userId = userId;
        this.email=email;
        this.color = color;
        this.currentPosition = currentPosition;
        this.balance = balance;
        this.amountKVShares = amountKVShares;
        this.amountTShares = amountTShares;
        this.amountBShares = amountBShares;
        this.isPlayersTurn = isPlayersTurn;
    }

    @NonNull
    @Override
    public String toString() {
        return "Session{" +
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
    }


    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Color getColor() {
        return color;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getBalance() {
        return balance;
    }

    public int getAmountKVShares() {
        return amountKVShares;
    }

    public int getAmountTShares() {
        return amountTShares;
    }

    public int getAmountBShares() {
        return amountBShares;
    }

    public boolean getIsPlayersTurn() {
        return isPlayersTurn;
    }
}
