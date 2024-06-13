package com.example.mankomania.screens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.mankomania.R;
import com.example.mankomania.api.PlayerSession;
import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.logik.spieler.Color;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerScoresFragment extends Fragment implements SessionAPI.GetStatusByLobbyCallback {

    private TextView scoreLila, scoreGreen, scoreBlue, scoreRed, currentPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_scores, container, false);

        scoreLila = view.findViewById(R.id.Fragment_PlayerLila_Balance);
        scoreGreen = view.findViewById(R.id.Fragment_PlayerGreen_Balance);
        scoreBlue = view.findViewById(R.id.Fragment_PlayerBlue_Balance);
        scoreRed = view.findViewById(R.id.Fragment_PlayerRed_Balance);
        currentPlayer=view.findViewById(R.id.Fragment_PlayersTurn);


        try {
            MasterKey masterKey = new MasterKey.Builder(requireContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    requireContext(),
                    "MyPrefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String token = sharedPreferences.getString("token", null);
            String lobbyid=sharedPreferences.getString("lobbyid",null);
            UUID lobbyId = UUID.fromString(lobbyid);

            SessionAPI.getStatusByLobby(token, lobbyId, this);

        } catch (GeneralSecurityException | IOException ignored) {
            Toast.makeText(requireContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onGetStatusByLobbySuccess(HashMap<UUID, PlayerSession> sessions) {
        requireActivity().runOnUiThread(() -> {
            for (PlayerSession session : sessions.values()) {
                Color color = session.getColor();
                String colorString = getGermanColorString(color);

                int balance = session.getBalance();
                setBalanceViewForColor(color, balance);

                if (session.getIsPlayersTurn()) {
                    currentPlayer.setText(String.format("%s%s", getString(R.string.derzeit_and_der_reihe), colorString));
                }
            }
        });
    }

    private String getGermanColorString(Color color){
        switch (color){
            case LILA: return "lila";
            case GREEN: return "grün";
            case BLUE: return "blau";
            case RED: return "rot";
            default: return "";
        }
    }

    private void setBalanceViewForColor(Color color,int balance){
        switch (color){
            case LILA: scoreLila.setText(String.valueOf(balance)); return;
            case GREEN: scoreGreen.setText(String.valueOf(balance)); return;
            case BLUE: scoreBlue.setText(String.valueOf(balance)); return;
            case RED: scoreRed.setText(String.valueOf(balance));
        }
    }

    @Override
    public void onGetStatusByLobbyFailure(String errorMessage) {
        requireActivity().runOnUiThread(() -> currentPlayer.setText(errorMessage));
    }
}
