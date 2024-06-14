package com.example.mankomania.api;

import androidx.annotation.NonNull;

import java.util.UUID;

public class Lobby {
    private UUID id;
    private String name;
    private String password;
    private boolean isPrivate;
    private int maxPlayers;
    private Status status;
    private String stockTrend;

    public Lobby(UUID id, String name, String password, boolean isPrivate, int maxPlayers, Status status, String stockTrend) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.isPrivate = isPrivate;
        this.maxPlayers = maxPlayers;
        this.status = status;
        this.stockTrend=stockTrend;
    }

    @NonNull
    @Override
    public String toString() {
        String string = "";
        string += "ID: " + this.id;
        string += "\nName: " + this.name;
        string += "\nPassword: " + this.password;
        string += "\nPrivate: " + this.isPrivate;
        string += "\nMax. Players: " + this.maxPlayers;
        string += "\nStatus: " + this.status;

        return string;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
