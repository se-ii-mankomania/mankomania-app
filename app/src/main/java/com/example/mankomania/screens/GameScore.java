package com.example.mankomania.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mankomania.R;
import com.example.mankomania.api.Lobby;
import com.example.mankomania.api.LobbyAPI;

import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.api.Status;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;


public class GameScore extends AppCompatActivity implements SessionAPI.JoinSessionCallback,LobbyAPI.GetLobbiesCallback, LobbyAPI.GetLobbiesByStatusCallback {

    private ListView listOfGames;
    private List<Lobby> allLobbies;
    private UUID selectedLobbyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listOfGames = findViewById(R.id.GameScore_ListOfGames);

        // get token from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        // get Lobbies
        LobbyAPI.getLobbies(token, GameScore.this);

        Button resumeGame=findViewById(R.id.GameScore_ResumeGame);
        resumeGame.setOnClickListener(v -> {
            int checkedPosition=listOfGames.getCheckedItemPosition();
            if(checkedPosition!= AdapterView.INVALID_POSITION) {
                String selectedGame=(String) listOfGames.getItemAtPosition(checkedPosition);
                for(Lobby lobby:allLobbies){
                    if(lobby.getName().equals(selectedGame.substring(10))){
                        selectedLobbyId=lobby.getId();
                    }
                }
                //TODO selctedGame starten
                SessionAPI.joinSession(token, selectedLobbyId,GameScore.this);
            }else{
                Toast.makeText(GameScore.this, "Wähle ein Spiel aus.", Toast.LENGTH_SHORT).show();
            }
        });

        Button startNewGame=findViewById(R.id.GameScore_NewGame);
        startNewGame.setOnClickListener(v -> {
            Intent createNewLobbyIntent = new Intent(GameScore.this, CreateNewLobby.class);
            startActivity(createNewLobbyIntent);
        });
    }

    @Override
    public void onGetLobbiesFailure(String errorMessage) {
        runOnUiThread(() -> Toast.makeText(GameScore.this, "Fehler: " + errorMessage, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onGetLobbiesSuccess(String[] lobbies, List<Lobby> allLobbies) {
        //store lobbies
        this.allLobbies=allLobbies;
        // will display the following rows:
        // <P/O> | x/<m> | <name>
        // P.. private lobby, O.. non-private lobby
        // m.. max. players
        runOnUiThread(() -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(GameScore.this,
                    android.R.layout.simple_list_item_single_choice, lobbies);
            listOfGames.setAdapter(adapter);
        });
    }

    @Override
    public void onGetLobbiesByStatusSuccess(String[] lobbies) {
        // will display the following rows:
        // <P/O> | x/<m> | <name>
        // P.. private lobby, O.. non-private lobby
        // m.. max. players
        runOnUiThread(() -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(GameScore.this,
                    android.R.layout.simple_list_item_single_choice, lobbies);
            listOfGames.setAdapter(adapter);
        });
    }

    @Override
    public void onGetLobbiesByStatusFailure(String errorMessage) {
        runOnUiThread(() -> Toast.makeText(GameScore.this, "Fehler: " + errorMessage, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onJoinSessionSuccess(String successMessage) {
        // store lobbyID
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putString("lobbyid", selectedLobbyId.toString());
        editor.apply();

        Intent chooseYourCharacterIntent = new Intent(GameScore.this, ChooseYourCharacter.class);
        startActivity(chooseYourCharacterIntent);
    }

    @Override
    public void onJoinSessionFailure(String errorMessage) {
        runOnUiThread(() -> Toast.makeText(GameScore.this, "FehlerJoinSession: " + errorMessage, Toast.LENGTH_SHORT).show());
    }
}