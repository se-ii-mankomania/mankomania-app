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

import com.example.mankomania.gameboardfields.GameboardField;

public class Board extends AppCompatActivity {

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

        //with every player
        ImageView player = findViewById(R.id.iv_player);
        player.getLayoutParams().height = cellHeight;
        player.getLayoutParams().width = cellWidth;
        player.requestLayout();

        //testing
        player.setX(fields[0].x);
        player.setY(fields[0].y);

    }
    //public move methode Player Objekt wird mit int übergeben
    //im PlayerObjekt eine FeldID -> dann ID plus Zahl

    public void movePlayer(int number){

    }

    private void initFields() {

        fields[0] = new GameboardField( cellPositions[13][13].x, cellPositions[13][13].y, 49);

    }
}