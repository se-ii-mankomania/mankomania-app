package com.example.mankomania;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mankomania.gameboardfields.GameboardField;

public class Board extends AppCompatActivity {

    GameboardField[] fields = new GameboardField[40];
    final int CELL_SIZE = 30;
    final int CELLS_PER_ROW = 14;

    CellPosition[][] cellPositions = new CellPosition[14][14];
    private class CellPosition {
        public float x;
        public float y;

        public CellPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_board);
        loadBoard();
        initFields();

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

        ImageView player = findViewById(R.id.iv_player);
        GridLayout layout = findViewById(R.id.grid_board);
        for(int row = 0; row < CELLS_PER_ROW; row++) {
            for(int col = 0; col < CELLS_PER_ROW; col++) {
                cellPositions[row][col] = new CellPosition(layout.getX() + col * CELL_SIZE, layout.getY() + row * CELL_SIZE);
            }
        }

        float x = cellPositions[3][12].x;
        float y = cellPositions[3][12].y;

        player.setX(x);
        player.setY(y);
    }

    private void loadBoard() {
        GridLayout layout = findViewById(R.id.grid_board);
        Bitmap[][] bitmaps = createBitmapsFromBoardImage(R.drawable.gameboard);
        for(int row = 0; row < CELLS_PER_ROW; row++) {
            for(int col = 0; col < CELLS_PER_ROW; col++) {
                ImageView view = new ImageView(this);
                cellPositions[row][col] = new CellPosition(col * CELL_SIZE, row * CELL_SIZE);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.width = CELL_SIZE;
                params.height = CELL_SIZE;

                view.setLayoutParams(params);
                view.setImageBitmap(bitmaps[row][col]);
                layout.addView(view);
            }
        }
    }

    private Bitmap[][] createBitmapsFromBoardImage(int drawable) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable, options);
        int size = bitmap.getHeight() / CELLS_PER_ROW;

        Bitmap[][] bitmaps = new Bitmap[CELLS_PER_ROW][CELLS_PER_ROW];
        for(int row = 0; row < CELLS_PER_ROW; row++) {
            for(int col = 0; col < CELLS_PER_ROW; col++) {
                bitmaps[row][col] = Bitmap.createBitmap(bitmap, col * size, row * size, size, size);
            }
        }
        return bitmaps;
    }

    private void initFields() {
        fields[0] = new GameboardField(700, 300, 0);
    }
}