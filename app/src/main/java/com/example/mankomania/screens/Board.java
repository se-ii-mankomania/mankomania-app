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
import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.logik.Color;
import com.example.mankomania.logik.Player;

import android.animation.ObjectAnimator;

public class Board extends AppCompatActivity {
    Player[] players = new Player[4];
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


        Player playerBlue = new Player("BLUE", Color.BLUE );
        playerBlue.setCurrentField(fieldsHandler.getField(44));
        Player playerGreen = new Player("GREEN", Color.GREEN);
        playerGreen.setCurrentField(fieldsHandler.getField(45));
        Player playerRed = new Player("RED", Color.RED);
        playerRed.setCurrentField(fieldsHandler.getField(46));
        Player playerPurple = new Player("PURPLE", Color.PURPLE );
        playerPurple.setCurrentField(fieldsHandler.getField(47));
        players[0] = playerBlue;
        players[1] = playerGreen;
        players[2] = playerRed;
        players[3] = playerPurple;

        //Testing
        fieldsHandler.movePlayer(playerBlue, 5);
        fieldsHandler.movePlayer(playerGreen, 5);
        fieldsHandler.movePlayer(playerRed, 5);
        fieldsHandler.movePlayer(playerPurple, 5);

        updatePlayerPositions();


        //TESTING -> Button just for testing
        Button moveTest = findViewById(R.id.movetest);
        moveTest.setOnClickListener(v -> {

            fieldsHandler.movePlayer(playerBlue, 2);
            fieldsHandler.movePlayer(playerGreen, 2);
            fieldsHandler.movePlayer(playerRed, 2);
            fieldsHandler.movePlayer(playerPurple, 2);


            updatePlayerPositions();

            sendPositionUpdatesToServer();
        });




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
       for (Player player: players
            ) {
           ImageView playerView = null;
           int viewId = 0;
           switch (player.getColor()) {
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

               if (playerView != null) {
                   animateMove(playerView, playerView.getX(), playerView.getY(), player.getCurrentField().getX(), player.getCurrentField().getY());
               }
           }


       }

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