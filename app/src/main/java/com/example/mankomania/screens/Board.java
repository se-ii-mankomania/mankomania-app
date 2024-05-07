package com.example.mankomania.screens;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mankomania.R;

import com.example.mankomania.api.AuthAPI;
import com.example.mankomania.api.Session;
import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.gameboardfields.GameboardField;
import com.example.mankomania.logik.Color;
import com.example.mankomania.logik.Player;

import android.animation.ObjectAnimator;
import android.widget.Toast;

import java.util.HashMap;
import java.util.UUID;

public class Board extends AppCompatActivity {

    FieldsHandler fieldsHandler = new FieldsHandler();

    Cellposition[][] cellPositions = new Cellposition[14][14];


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_board);


        ZoomLayout zoomLayout = findViewById(R.id.zoom_linear_layout);
        zoomLayout.setOnTouchListener((View v, MotionEvent event) -> {
            zoomLayout.init(Board.this);
            return false;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ToolbarFunctionalities.setUpToolbar(this);

        Button rollDice=findViewById(R.id.Board_ButtonDice);
        rollDice.setOnClickListener(v -> {
           Intent toEventRollDice=new Intent(Board.this,EventRollDice.class);
           toEventRollDice.putExtra("fieldsHandler", fieldsHandler);
           startActivity(toEventRollDice);
        });

    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ImageView board = findViewById(R.id.gameboard);
        int cellWidth = board.getWidth() / 14;
        int cellHeight = board.getHeight()/14;
        for (int row = 0; row<14; row++){
            for(int col = 0; col<14; col++){
                cellPositions[row][col] = new Cellposition((int) (col*cellWidth + board.getX()), (int) (row*cellHeight + board.getY()));
            }
        }

        fieldsHandler.initFields(cellPositions);

        updatePlayerPositions();

        ImageView playerBlueImage = findViewById(R.id.player_blue);
        playerBlueImage.getLayoutParams().height = cellHeight;
        playerBlueImage.getLayoutParams().width = cellWidth;
        playerBlueImage.requestLayout();

        ImageView playerGreenImage = findViewById(R.id.player_green);
        playerGreenImage.getLayoutParams().height = cellHeight;
        playerGreenImage.getLayoutParams().width = cellWidth;
        playerGreenImage.requestLayout();

        ImageView playerPurpleImage = findViewById(R.id.player_purple);
        playerPurpleImage.getLayoutParams().height = cellHeight;
        playerPurpleImage.getLayoutParams().width = cellWidth;
        playerPurpleImage.requestLayout();

        ImageView playerRedImage = findViewById(R.id.player_red);
        playerRedImage.getLayoutParams().height = cellHeight;
        playerRedImage.getLayoutParams().width = cellWidth;
        playerRedImage.requestLayout();


    }

    // Methode zum Senden der Spielerpositionen an den Server
    private void sendPositionUpdatesToServer() {
        //TODO just hardcoded for now -> change to actual userID
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        String token = sharedPreferences.getString("token", null);
        String lobbyId = sharedPreferences.getString("lobbyid", null);
            SessionAPI.updatePlayerPosition(token, userId, 24, lobbyId, new SessionAPI.UpdatePositionCallback() {
                @Override
                public void onUpdateSuccess(String message) {
                    // Erfolgsmeldung, evtl. Loggen oder Nutzer benachrichtigen
                    Log.d("UpdatePosition", "Position erfolgreich aktualisiert: " + message);
                }

                @Override
                public void onUpdateFailure(String errorMessage) {
                    // Fehlerbehandlung, evtl. Fehler dem Nutzer anzeigen
                    Log.e("UpdatePosition", "Fehler beim Aktualisieren der Position: " + errorMessage);
                }
            });

    }

   public void updatePlayerPositions(){
       SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
       String token = sharedPreferences.getString("token", null);
       String lobbyId = sharedPreferences.getString("lobbyid", null);

       SessionAPI.getStatusByLobby(token, UUID.fromString(lobbyId), new SessionAPI.GetStatusByLobbyCallback() {

           @Override
           public void onGetStatusByLobbySuccess(HashMap<UUID, Session> sessions) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       (findViewById(R.id.player_blue)).setVisibility(View.INVISIBLE);
                       (findViewById(R.id.player_purple)).setVisibility(View.INVISIBLE);

                       (findViewById(R.id.player_red)).setVisibility(View.INVISIBLE);
                       (findViewById(R.id.player_green)).setVisibility(View.INVISIBLE);

                       for(Session session: sessions.values()){
                           ImageView playerView = null;
                           int viewId = 0;
                           switch (session.getColor()) {
                               case BLUE:
                                   viewId = R.id.player_blue;
                                   break;
                               case RED:
                                   viewId = R.id.player_red;
                                   break;
                               case GREEN:
                                   viewId = R.id.player_green;
                                   break;
                               case PURPLE:
                                   viewId = R.id.player_purple;
                                   break;
                           }
                           if (viewId != 0) {
                               playerView = findViewById(viewId);
                               playerView.setVisibility(View.VISIBLE);

                               if (playerView != null) {
                                   GameboardField gameboardField = fieldsHandler.getField(session.getCurrentPosition()-1);
                                   animateMove(playerView, playerView.getX(), playerView.getY(),
                                           gameboardField.getX(), gameboardField.getY());
                               }
                           }

                       }
                   }
               });

           }

           @Override
           public void onGetStatusByLobbyFailure(String errorMessage) {
               Toast.makeText(getApplicationContext(), "Could not get sessionLobbyStatus", Toast.LENGTH_SHORT).show();
           }
       });

   }
    private void animateMove(ImageView imageView, float startX, float startY, float endX, float endY) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
        animatorX.setDuration(500); // 500ms
        animatorY.setDuration(500);
        animatorX.start();
        animatorY.start();
       // Toast.makeText(getApplicationContext(), auf datenbank zugreifne??)
    }
}