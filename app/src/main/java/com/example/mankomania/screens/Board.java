package com.example.mankomania.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mankomania.R;

import com.example.mankomania.api.SessionStatusService;
import com.example.mankomania.api.Session;
import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.gameboardfields.GameboardField;

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
        //zuerst Button enablen, zur Sicherheit
        Button rollDice = findViewById(R.id.Board_ButtonDice);
        rollDice.setEnabled(false);

        SharedPreferences sharedPreferencesLobbyId = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        UUID userId = UUID.fromString(sharedPreferencesLobbyId.getString("userId", null));


        Intent sessionStatusServiceIntent = new Intent(this, SessionStatusService.class);
        startService(sessionStatusServiceIntent);

        SessionStatusService sessionStatusService = SessionStatusService.getInstance();
        sessionStatusService.registerObserver((SessionStatusService.PlayersTurnObserver) (color, newTurn,userid) -> runOnUiThread(() -> {
            if(newTurn && userid.equals(userId)){
                rollDice.setEnabled(true);
            }else{
                rollDice.setEnabled(false);
            }
        }));

        rollDice.setOnClickListener(v -> {
            Intent toEventRollDice = new Intent(Board.this, EventRollDice.class);
            toEventRollDice.putExtra("fieldsHandler", fieldsHandler);
            startActivity(toEventRollDice);
        });
        //derweil zu Überprüfungszwecken
        TextView currentPlayer = findViewById(R.id.textView);
        sessionStatusService.registerObserver((SessionStatusService.PlayersTurnObserver) (color, newTurn1,userid) -> runOnUiThread(() -> {
            if (newTurn1 && userid.equals(userId)) {
                Log.wtf("HAAAALLLLOOOOOOOO","Ich werde aufgerufen");
                currentPlayer.setText(color);
            }
        }));


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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SessionStatusService sessionStatusService = SessionStatusService.getInstance();
        sessionStatusService.removeObserver((SessionStatusService.PlayersTurnObserver) (color, newTurn,userid) -> {
            //Könnte Ausgabe enthalten, ist aber nicht nötig
        });

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ImageView board = findViewById(R.id.gameboard);
        int cellWidth = board.getWidth() / 14;
        int cellHeight = board.getHeight() / 14;
        for (int row = 0; row < 14; row++) {
            for (int col = 0; col < 14; col++) {
                cellPositions[row][col] = new Cellposition((int) (col * cellWidth + board.getX()), (int) (row * cellHeight + board.getY()));
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

    private void stopSessionStatusService() {
        Intent sessionStatusServiceIntent = new Intent(this, SessionStatusService.class);
        stopService(sessionStatusServiceIntent);
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

    public void updatePlayerPositions() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        String lobbyId = sharedPreferences.getString("lobbyid", null);

        SessionAPI.getStatusByLobby(token, UUID.fromString(lobbyId), new SessionAPI.GetStatusByLobbyCallback() {

            @Override
            public void onGetStatusByLobbySuccess(HashMap<UUID, Session> sessions) {
                runOnUiThread(() -> {
                    (findViewById(R.id.player_blue)).setVisibility(View.INVISIBLE);
                    (findViewById(R.id.player_purple)).setVisibility(View.INVISIBLE);

                    (findViewById(R.id.player_red)).setVisibility(View.INVISIBLE);
                    (findViewById(R.id.player_green)).setVisibility(View.INVISIBLE);

                    for (Session session : sessions.values()) {
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
                                GameboardField gameboardField = fieldsHandler.getField(session.getCurrentPosition() - 1);
                                animateMove(playerView, playerView.getX(), playerView.getY(),
                                        gameboardField.getX(), gameboardField.getY());
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
}