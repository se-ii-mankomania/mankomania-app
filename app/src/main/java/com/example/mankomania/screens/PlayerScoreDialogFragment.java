package com.example.mankomania.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mankomania.R;
import com.example.mankomania.api.PlayerSession;
import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.logik.spieler.Color;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerScoreDialogFragment extends DialogFragment implements SessionAPI.GetStatusByLobbyCallback {

    private ScoresAdapter adapter;
    private TextView playersTurn;

    public interface OnCloseDialogListener {
        void onCloseDialog();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCloseDialogListener) {
            OnCloseDialogListener onCloseDialogListener = (OnCloseDialogListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnCloseDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_player_score_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        playersTurn = view.findViewById(R.id.Fragment_PlayersTurn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScoresAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        fetchScoresFromAPI();
    }

    private void fetchScoresFromAPI() {
        try {
            MasterKey masterKey = new MasterKey.Builder(getContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    getContext(),
                    "MyPrefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String token = sharedPreferences.getString("token", null);
            String lobbyIdString = sharedPreferences.getString("lobbyid", null);
            UUID lobbyId = UUID.fromString(lobbyIdString);

            SessionAPI.getStatusByLobby(token, lobbyId, this);

        } catch (GeneralSecurityException | IOException e) {
            Toast.makeText(getContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }

        adapter.updateScores(new ArrayList<>());
        playersTurn.setText("");
    }

    @Override
    public void onGetStatusByLobbySuccess(HashMap<UUID, PlayerSession> sessions) {
        List<String> playerScores=new ArrayList<>();
        String currentPlayerTxt="";
        for(PlayerSession session:sessions.values()){
            Color color=session.getColor();
            if(session.getIsPlayersTurn()){
                currentPlayerTxt=getColorName(color);
            }
            playerScores.add(getColorName(color));
            playerScores.add(String.valueOf(session.getBalance()));
        }

        adapter.updateScores(playerScores);
        playersTurn.setText(currentPlayerTxt);
    }

    private String getColorName(Color color){
        switch (color){
            case RED: return "Player Rot";
            case BLUE: return "Player Blau";
            case GREEN: return "Player Grün";
            case LILA:return "Player Lila";
            default: return "";
        }
    }

    @Override
    public void onGetStatusByLobbyFailure(String errorMessage) {
        playersTurn.setText(errorMessage);
    }
}
