package com.example.mankomania.screens;


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

import android.animation.ObjectAnimator;
import android.widget.ImageView;

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
        playerblue.setCurrentField(fieldsHandler.fields[48]);
        Player playergreen = new Player("GREEN", Color.GREEN);
        playergreen.setCurrentField(fieldsHandler.fields[49]);
        Player playerRed = new Player("RED", Color.RED);
        playerRed.setCurrentField(fieldsHandler.fields[50]);
        Player playerPurple = new Player("PURPLE", Color.PURPLE );
        playerPurple.setCurrentField(fieldsHandler.fields[51]);
        players[0] = playerblue;
        players[1] = playergreen;
        players[2] = playerRed;
        players[3] = playerPurple;
        updatePlayerPositions();


        //TESTING -> Button just for testing
        Button moveTest = findViewById(R.id.movetest);
        moveTest.setOnClickListener(v -> {
            fieldsHandler.movePlayer(playerblue, 2);
            fieldsHandler.movePlayer(playergreen, 2);
            fieldsHandler.movePlayer(playerRed, 2);
            fieldsHandler.movePlayer(playerPurple, 2);
            updatePlayerPositions();
        });

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
                   animateMove(playerView, playerView.getX(), playerView.getY(), player.getCurrentField().x, player.getCurrentField().y);
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
    }
}