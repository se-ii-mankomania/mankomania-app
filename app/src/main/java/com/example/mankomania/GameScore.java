package com.example.mankomania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import com.example.mankomania.api.Lobby;

public class GameScore extends AppCompatActivity implements Lobby.GetLobbiesCallback {

    private ListView listOfGames;

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
        Lobby.getLobbies(token, GameScore.this);

        Button resumeGame=findViewById(R.id.GameScore_ResumeGame);
        resumeGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedPosition=listOfGames.getCheckedItemPosition();
                if(checkedPosition!= AdapterView.INVALID_POSITION) {
                    String selectedGame=(String) listOfGames.getItemAtPosition(checkedPosition);
                    //TODO selctedGame starten
                    Intent resumeGameIntent = new Intent(GameScore.this, Board.class);
                    startActivity(resumeGameIntent);
                }else{
                    Toast.makeText(GameScore.this, "Wähle ein Spiel aus.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button startNewGame=findViewById(R.id.GameScore_NewGame);
        startNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO neue Instanzen für neues Spiel erzeugen
                Intent createNewLobbyIntent = new Intent(GameScore.this, CreateNewLobby.class);
                startActivity(createNewLobbyIntent);
            }
        });
    }

    @Override
    public void onGetLobbiesFailure(String errorMessage) {
        // handle login failure
        runOnUiThread(() -> Toast.makeText(GameScore.this, "Fehler: " + errorMessage, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onGetLobbiesSuccess(String[] lobbies) {
        runOnUiThread(() -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(GameScore.this,
                    android.R.layout.simple_list_item_single_choice, lobbies);
            listOfGames.setAdapter(adapter);
        });
    }
}