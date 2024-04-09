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

import com.example.mankomania.gameboardfields.ActivityField;
import com.example.mankomania.gameboardfields.GameboardField;
import com.example.mankomania.gameboardfields.GoToField;
import com.example.mankomania.gameboardfields.HotelField;
import com.example.mankomania.gameboardfields.ProfitField;

public class Board extends AppCompatActivity {
    Player [] players = new Player[2];


    GameboardField[] fields = new GameboardField[52];

    CellPosition[][] cellPositions = new CellPosition[14][14];
    private class CellPosition {
        public int x;
        public int y;

        public CellPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

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
                cellPositions[row][col] = new CellPosition((int) (col*cellWidth + board.getX()), (int) (row*cellHeight + board.getY()));
            }
        }

        initFields();

        Player playerblue = new Player(Color.BLUE, fields[7] );
        Player playergreen = new Player(Color.GREEN, fields[7]);
        players[0] = playerblue;
        players[1] = playergreen;
        movePlayer(playerblue, 5);
        movePlayer(playergreen, 5);
        udatePlayerPositions();



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

        //testing
   /*     player_blue.setX(fields[0].x);
        player_blue.setY(fields[0].y);
        player_green.setX(fields[1].x);
        player_green.setY(fields[1].y);
        player_red.setX(fields[2].x);
        player_red.setY(fields[1].y);


    */
        //player_purple.setX(fields[3].x);
        //player_purple.setY(fields[1].y);






    }
   public void udatePlayerPositions(){
       for (Player player: players
            ) {
           if(player.color == Color.BLUE){
               ImageView playerBlue = findViewById(R.id.player_blue);
               playerBlue.setX(player.currentField.x);
               playerBlue.setY(player.currentField.y);
           }
           if(player.color == Color.RED){
               ImageView playerRed = findViewById(R.id.player_red);
               playerRed.setX(player.currentField.x);
               playerRed.setY(player.currentField.y);
           }
           if(player.color == Color.GREEN){
               ImageView playerGreen = findViewById(R.id.player_green);
               playerGreen.setX(player.currentField.x);
               playerGreen.setY(player.currentField.y);
           }
           if(player.color == Color.YELLOW){
               ImageView playerPurple = findViewById(R.id.player_purple);
               playerPurple.setX(player.currentField.x);
               playerPurple.setY(player.currentField.y);
           }
       }


   }
    public void movePlayer(Player player, int diceNr){
        //innerhalb des Quadrats

        //>= 1 oder 0
        if(player.currentField.id>=1 && player.currentField.id <= 43){
            //neue Runde
            if(player.currentField.id + diceNr > 43){
                int calc = 43 - player.currentField.id;
                int newID = diceNr - calc;
                player.setGameboardField(fields[newID]);
            }
            player.setGameboardField(fields[player.currentField.id + diceNr -1]);
        }

        //Starts
        //links oben
        else if(player.currentField.id == 48){
                int newID = diceNr;
                player.setGameboardField(fields[newID]);

        }
        //rechts oben
        else if(player.currentField.id == 49){
            int newID = 10 + diceNr ;
            player.setGameboardField(fields[newID]);
        }
        //rechts unten
       else if(player.currentField.id == 50){
            int newID = 21 + diceNr ;
            player.setGameboardField(fields[newID]);
        }
       //links unten
       else if(player.currentField.id == 51){
            if(diceNr > 11){
                int calc = diceNr - 12;
                int newID =calc;
                player.setGameboardField(fields[newID]);
            }
            int newID = 32 + diceNr ;
            player.setGameboardField(fields[newID]);
        }

        //ActivityFelder
        // Pferderennen
        else if(player.currentField.id == 46){
            player.setGameboardField(fields[ 14 + diceNr]);
        }
        //Börse
        else if(player.currentField.id == 47){
            player.setGameboardField(fields[ 19 + diceNr]);
        }
        //Casino
        else if(player.currentField.id == 48){
            player.setGameboardField(fields[ 36 + diceNr]);
            if(diceNr > 8){
                int calc = diceNr - 8;
                int newID =calc;
                player.setGameboardField(fields[newID]);
            }
        }
        //Auktionshaus
        else if(player.currentField.id == 45){
            if(diceNr > 3){
                int calc = diceNr - 3;
                int newID =calc;
                player.setGameboardField(fields[newID]);
            }
            player.setGameboardField(fields[ 41 + diceNr]);
        }


    }

    public void movePlayer(int number){

    }

    private void initFields() {

        //fields[0] = new GameboardField( cellPositions[13][13].x, cellPositions[13][13].y, 49);
        fields[44] = new ActivityField( cellPositions[4][2].x, cellPositions[4][2].y, 45, "Auktionshaus");
        fields[45] = new ActivityField( cellPositions[4][11].x, cellPositions[4][11].y, 46, "Pferderennen");
        fields[46] = new ActivityField( cellPositions[9][11].x, cellPositions[9][11].y, 47, "Börse" );
        fields[47] = new ActivityField( cellPositions[9][2].x, cellPositions[9][2].y, 48, "Casino");
        fields[24] = new ActivityField( cellPositions[12][10].x, cellPositions[12][10].y, 25, "Lotterie");
        fields[6] = new ActivityField( cellPositions[1][7].x, cellPositions[1][7].y, 7, "Böse 1");

        fields[0] = new GoToField( cellPositions[1][1].x, cellPositions[1][1].y, 1, fields[44]);
        fields[1] = new GoToField( cellPositions[1][2].x, cellPositions[1][2].y, 2, fields[45]);
        fields[2] = new ProfitField( cellPositions[1][3].x, cellPositions[1][3].y, 3, 10000);
        fields[3] = new ProfitField( cellPositions[1][4].x, cellPositions[1][4].y, 4, 5000);
        fields[4] = new GoToField( cellPositions[1][5].x, cellPositions[1][5].y, 5, fields[46]);
        fields[5] = new ProfitField( cellPositions[1][6].x, cellPositions[1][6].y, 6, 7000);
        fields[7] = new ProfitField( cellPositions[1][8].x, cellPositions[1][8].y, 8, 20000);
        fields[8] = new GoToField( cellPositions[1][9].x, cellPositions[1][9].y, 9, fields[47]);
        fields[9] = new HotelField( cellPositions[1][10].x, cellPositions[1][10].y, 10, 10000, 70000, null);
        fields[10] = new ProfitField( cellPositions[1][11].x, cellPositions[1][11].y, 11, 10000);
        fields[11] = new GoToField( cellPositions[1][12].x, cellPositions[1][12].y, 12, fields[6]);

        fields[12] = new ProfitField( cellPositions[2][12].x, cellPositions[2][12].y, 13, 8000);
        fields[13] = new GoToField( cellPositions[3][12].x, cellPositions[3][12].y, 14, fields[45]);
        fields[14] = new ProfitField( cellPositions[4][12].x, cellPositions[4][12].y, 15, 7000);
        fields[15] = new ProfitField( cellPositions[5][12].x, cellPositions[5][12].y, 16, 15000);
        fields[16] = new GoToField( cellPositions[6][12].x, cellPositions[6][12].y, 17, fields[46]);
        fields[17] = new HotelField( cellPositions[7][12].x, cellPositions[7][12].y, 18, 13000, 100000, null);
        fields[18] = new GoToField( cellPositions[8][12].x, cellPositions[8][12].y, 19, fields[47]);
        fields[19] = new ProfitField( cellPositions[9][12].x, cellPositions[9][12].y, 20, 20000);
        fields[20] = new ProfitField( cellPositions[10][12].x, cellPositions[10][12].y, 21, 15000);
        fields[21] = new ProfitField( cellPositions[11][12].x, cellPositions[11][12].y, 22, 10000); //starter3
        fields[22] = new GoToField( cellPositions[12][12].x, cellPositions[12][12].y, 23, fields[44]);

        fields[23] = new ProfitField( cellPositions[12][11].x, cellPositions[12][11].y, 24, 11000);
        fields[25] = new ProfitField( cellPositions[12][9].x, cellPositions[12][9].y, 26, 13000);
        fields[26] = new ProfitField( cellPositions[12][8].x, cellPositions[12][8].y, 27, 20000);
        fields[27] = new HotelField( cellPositions[12][7].x, cellPositions[12][7].y, 28, 20000, 120000, null);
        fields[28] = new ProfitField(cellPositions[12][6].x, cellPositions[12][6].y, 29, 10000);
        fields[29] = new ProfitField( cellPositions[12][5].x, cellPositions[12][5].y, 30, 15000);
        fields[30] = new ProfitField( cellPositions[12][4].x, cellPositions[12][4].y, 31, 20000);
        fields[31] = new GoToField( cellPositions[12][3].x, cellPositions[12][3].y, 32, fields[45]);
        fields[32] = new ProfitField( cellPositions[12][2].x, cellPositions[12][2].y, 33, 10000); //starter4
        fields[33] = new GoToField( cellPositions[12][1].x, cellPositions[12][1].y, 34, fields[46]);

        fields[34] = new ProfitField( cellPositions[11][1].x, cellPositions[11][1].y, 35, 5000);
        fields[35] = new ProfitField( cellPositions[10][1].x, cellPositions[10][1].y, 36, 30000);
        fields[36] = new GoToField( cellPositions[9][1].x, cellPositions[9][1].y, 37, fields[24]);
        fields[37] = new ProfitField( cellPositions[8][1].x, cellPositions[8][1].y, 38, 5000);
        fields[38] = new ProfitField( cellPositions[7][1].x, cellPositions[7][1].y, 39, 12000);
        fields[39] = new ProfitField( cellPositions[6][1].x, cellPositions[6][1].y, 40, 10000);
        fields[40] = new HotelField( cellPositions[5][1].x, cellPositions[5][1].y, 41, 5000, 40000, null);
        fields[41] = new ProfitField( cellPositions[4][1].x, cellPositions[4][1].y, 42, 6000);
        fields[42] = new GoToField( cellPositions[3][1].x, cellPositions[3][1].y, 43, fields[44]);
        fields[43] = new ProfitField( cellPositions[2][1].x, cellPositions[2][1].y, 44, 18000);


        fields[48] = new GameboardField( cellPositions[0][0].x, cellPositions[0][0].y, 49);
        fields[49] = new GameboardField( cellPositions[0][13].x, cellPositions[0][13].y, 50);
        fields[50] = new GameboardField( cellPositions[13][13].x, cellPositions[13][13].y, 51);
        fields[51] = new GameboardField( cellPositions[13][0].x, cellPositions[11][1].y, 52);


    }
}