package com.example.mankomania.screens;


import android.content.Intent;
import android.os.Bundle;
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


        Player playerblue = new Player("Blue", Color.BLUE );
        playerblue.setCurrentField(fieldsHandler.fields[44]);
        Player playergreen = new Player("GREEN", Color.GREEN);
        playergreen.setCurrentField(fieldsHandler.fields[45]);
        Player playerRed = new Player("RED", Color.RED);
        playerRed.setCurrentField(fieldsHandler.fields[46]);
        Player playerPurple = new Player("PURPLE", Color.PURPLE );
        playerPurple.setCurrentField(fieldsHandler.fields[47]);
        players[0] = playerblue;
        players[1] = playergreen;
        players[2] = playerRed;
        players[3] = playerPurple;

        //Testing
        fieldsHandler.movePlayer(playerblue, 5);
        fieldsHandler.movePlayer(playergreen, 5);
        fieldsHandler.movePlayer(playerRed, 5);
        fieldsHandler.movePlayer(playerPurple, 5);

        updatePlayerPositions();



        ImageView player_blue = findViewById(R.id.player_blue);
        player_blue.getLayoutParams().height = cellHeight;
        player_blue.getLayoutParams().width = cellWidth;
        player_blue.requestLayout();

        ImageView player_green = findViewById(R.id.player_green);
        player_green.getLayoutParams().height = cellHeight;
        player_green.getLayoutParams().width = cellWidth;
        player_green.requestLayout();

        ImageView player_purple = findViewById(R.id.player_purple);
        player_purple.getLayoutParams().height = cellHeight;
        player_purple.getLayoutParams().width = cellWidth;
        player_purple.requestLayout();

        ImageView player_red = findViewById(R.id.player_red);
        player_red.getLayoutParams().height = cellHeight;
        player_red.getLayoutParams().width = cellWidth;
        player_red.requestLayout();


    }
   public void updatePlayerPositions(){
       for (Player player: players
            ) {
           if(player.getColor() == Color.BLUE){
               ImageView playerBlue = findViewById(R.id.player_blue);
               playerBlue.setX(player.getCurrentField().getX());
               playerBlue.setY(player.getCurrentField().getY());
           }
           if(player.getColor() == Color.RED){
               ImageView playerRed = findViewById(R.id.player_red);
               playerRed.setX(player.getCurrentField().getX());
               playerRed.setY(player.getCurrentField().getY());
           }
           if(player.getColor() == Color.GREEN){
               ImageView playerGreen = findViewById(R.id.player_green);
               playerGreen.setX(player.getCurrentField().getX());
               playerGreen.setY(player.getCurrentField().getY());
           }
           if(player.getColor() == Color.PURPLE){
               ImageView playerPurple = findViewById(R.id.player_purple);
               playerPurple.setX(player.getCurrentField().getX());
               playerPurple.setY(player.getCurrentField().getY());
           }
       }

   }
}