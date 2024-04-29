package com.example.mankomania.api;

import androidx.annotation.NonNull;

import java.util.UUID;

public class Session {

    private final UUID id;
    private final UUID lobbyId;
    private final UUID userId;

    private final String color;

    private final int currentPosition;
    private final int balance;
    private final int amountKVShares;
    private final int amountTShares;
    private final int amountBShares;
    private final boolean isPlayersTurn;

    public Session(UUID id, UUID lobbyId, UUID userId, String color, int currentPosition, int balance, int amountKVShares, int amountTShares, int amountBShares, boolean isPlayersTurn) {
        this.id = id;
        this.lobbyId = lobbyId;
        this.userId = userId;
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
                "id=" + id +
                ", lobbyId=" + lobbyId +
                ", userId=" + userId +
                ", color='" + color + '\'' +
                ", currentPosition=" + currentPosition +
                ", balance=" + balance +
                ", amountKVShares=" + amountKVShares +
                ", amountTShares=" + amountTShares +
                ", amountBShares=" + amountBShares +
                ", isPlayersTurn=" + isPlayersTurn +
                '}';
    }
}
