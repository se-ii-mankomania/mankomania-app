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

    public Lobby(UUID id, String name, String password, boolean isPrivate, int maxPlayers, Status status) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.isPrivate = isPrivate;
        this.maxPlayers = maxPlayers;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }
    public String getName(){
        return name;
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
}
