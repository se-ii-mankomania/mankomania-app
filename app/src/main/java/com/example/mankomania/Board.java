package com.example.mankomania;


import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Board extends AppCompatActivity {
    PlayerTest[] players = new PlayerTest[4];
    FieldsHandler fieldsHandler = new FieldsHandler();

    Cellposition[][] cellPositions = new Cellposition[14][14];


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_board);




        ZoomLayout zoomLayout = (ZoomLayout) findViewById(R.id.zoom_linear_layout);
        zoomLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                zoomLayout.init(Board.this);
                return false;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ToolbarFunctionalities toolbarFunctionalities=new ToolbarFunctionalities(this);
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


        PlayerTest playerblue = new PlayerTest(ColorTest.BLUE, fieldsHandler.fields[44] );
        PlayerTest playergreen = new PlayerTest(ColorTest.GREEN, fieldsHandler.fields[45]);
        PlayerTest playerRed = new PlayerTest(ColorTest.RED, fieldsHandler.fields[46]);
        PlayerTest playerPurple = new PlayerTest(ColorTest.PURPLE, fieldsHandler.fields[47]);
        players[0] = playerblue;
        players[1] = playergreen;
        players[2] = playerRed;
        players[3] = playerPurple;

//Testing
        fieldsHandler.movePlayer(playerblue, 15);
        fieldsHandler.movePlayer(playergreen, 15);
        fieldsHandler.movePlayer(playerRed, 15);
        fieldsHandler.movePlayer(playerPurple, 15);



        fieldsHandler.movePlayer(playerblue, 12);
        fieldsHandler.movePlayer(playergreen, 12);
        fieldsHandler.movePlayer(playerRed, 12);
        fieldsHandler.movePlayer(playerPurple, 12);
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
       for (PlayerTest player: players
            ) {
           if(player.color == ColorTest.BLUE){
               ImageView playerBlue = findViewById(R.id.player_blue);
               playerBlue.setX(player.currentField.x);
               playerBlue.setY(player.currentField.y);
           }
           if(player.color == ColorTest.RED){
               ImageView playerRed = findViewById(R.id.player_red);
               playerRed.setX(player.currentField.x);
               playerRed.setY(player.currentField.y);
           }
           if(player.color == ColorTest.GREEN){
               ImageView playerGreen = findViewById(R.id.player_green);
               playerGreen.setX(player.currentField.x);
               playerGreen.setY(player.currentField.y);
           }
           if(player.color == ColorTest.PURPLE){
               ImageView playerPurple = findViewById(R.id.player_purple);
               playerPurple.setX(player.currentField.x);
               playerPurple.setY(player.currentField.y);
           }
       }

   }
}