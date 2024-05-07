package com.example.mankomania.screens;


import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mankomania.R;

import com.example.mankomania.api.SessionStatusService;
import com.example.mankomania.logik.Color;
import com.example.mankomania.logik.Player;

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

        //Wenn der Back-Button betätigt wird, wird der Polling-Service für den Status gestoppt
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                stopSessionStatusService();
                if (isEnabled()) {
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        Button rollDice=findViewById(R.id.Board_ButtonDice);
        rollDice.setOnClickListener(v -> {
           Intent toEventRollDice=new Intent(Board.this,EventRollDice.class);
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
   public void updatePlayerPositions() {
       for (Player player : players
       ) {
           if (player.getColor() == Color.BLUE) {
               ImageView playerBlue = findViewById(R.id.player_blue);
               playerBlue.setX(player.getCurrentField().getX());
               playerBlue.setY(player.getCurrentField().getY());
           }
           if (player.getColor() == Color.RED) {
               ImageView playerRed = findViewById(R.id.player_red);
               playerRed.setX(player.getCurrentField().getX());
               playerRed.setY(player.getCurrentField().getY());
           }
           if (player.getColor() == Color.GREEN) {
               ImageView playerGreen = findViewById(R.id.player_green);
               playerGreen.setX(player.getCurrentField().getX());
               playerGreen.setY(player.getCurrentField().getY());
           }
           if (player.getColor() == Color.PURPLE) {
               ImageView playerPurple = findViewById(R.id.player_purple);
               playerPurple.setX(player.getCurrentField().getX());
               playerPurple.setY(player.getCurrentField().getY());
           }
       }
   }

   private void stopSessionStatusService() {
        Intent sessionStatusServiceIntent=new Intent(this, SessionStatusService.class);
        stopService(sessionStatusServiceIntent);
   }

}