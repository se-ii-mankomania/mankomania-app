package com.example.mankomania.screens;

import androidx.lifecycle.ViewModel;

import androidx.lifecycle.MutableLiveData;

import com.example.mankomania.logik.spieler.Color;
import com.example.mankomania.logik.spieler.Player;

public class PlayerViewModel extends ViewModel {
    private final MutableLiveData<Player> playerLiveData = new MutableLiveData<>();

    public PlayerViewModel() {
        // Initialisieren Sie den Player mit Dummy-Daten
        Player dummy = new Player("Dummy", Color.BLUE);
        playerLiveData.setValue(dummy);
    }

    public MutableLiveData<Player> getPlayer() {
        return playerLiveData;
    }
}

