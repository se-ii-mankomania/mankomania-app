package com.example.mankomania.screens;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.mankomania.R;

import com.example.mankomania.api.SessionStatusService;
import com.example.mankomania.api.Session;
import com.example.mankomania.gameboardfields.GameboardField;

import android.animation.ObjectAnimator;
import android.widget.Toast;

import java.io.IOException;
import java.security.GeneralSecurityException;
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

        Intent sessionStatusServiceIntent = new Intent(this, SessionStatusService.class);
        startService(sessionStatusServiceIntent);

        UUID userId;
        SessionStatusService sessionStatusService = SessionStatusService.getInstance();
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "MyPrefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String useridString = sharedPreferences.getString("userId", null);
            userId=UUID.fromString(useridString);

            sessionStatusService.registerObserver((SessionStatusService.PlayersTurnObserver) (color, newTurn,userid) -> runOnUiThread(() -> {
                rollDice.setEnabled(newTurn && userid.equals(userId));

            }));

            sessionStatusService.registerObserver((SessionStatusService.PositionObserver) session -> runOnUiThread(() ->
                updatePlayerPosition(session)
            ));

        } catch (GeneralSecurityException | IOException ignored) {
            Toast.makeText(getApplicationContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }

        rollDice.setOnClickListener(v -> {
            Intent toEventRollDice = new Intent(Board.this, EventRollDice.class);
            toEventRollDice.putExtra("fieldsHandler", fieldsHandler);
            startActivity(toEventRollDice);
        });


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

        sessionStatusService.registerObserver((SessionStatusService.BalanceBelowThresholdObserver) (userIdWinner, colorWinner) -> runOnUiThread(() -> {
            stopSessionStatusService();
            Intent toEndWinner = new Intent(Board.this, EndWinner.class);
            toEndWinner.putExtra("Winner",colorWinner);
            startActivity(toEndWinner);
            finish();
        }));


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

        ImageView playerBlueImage = findViewById(R.id.player_blue);
        playerBlueImage.getLayoutParams().height = cellHeight;
        playerBlueImage.getLayoutParams().width = cellWidth;
        playerBlueImage.setVisibility(View.INVISIBLE);
        playerBlueImage.requestLayout();

        ImageView playerGreenImage = findViewById(R.id.player_green);
        playerGreenImage.getLayoutParams().height = cellHeight;
        playerGreenImage.getLayoutParams().width = cellWidth;
        playerGreenImage.setVisibility(View.INVISIBLE);
        playerGreenImage.requestLayout();

        ImageView playerPurpleImage = findViewById(R.id.player_purple);
        playerPurpleImage.getLayoutParams().height = cellHeight;
        playerPurpleImage.getLayoutParams().width = cellWidth;
        playerPurpleImage.setVisibility(View.INVISIBLE);
        playerPurpleImage.requestLayout();

        ImageView playerRedImage = findViewById(R.id.player_red);
        playerRedImage.getLayoutParams().height = cellHeight;
        playerRedImage.getLayoutParams().width = cellWidth;
        playerRedImage.setVisibility(View.INVISIBLE);
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
    }

    private void updatePlayerPosition(Session session) {
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
            ImageView playerView = findViewById(viewId);
            GameboardField gameboardField = fieldsHandler.getField(session.getCurrentPosition() - 1);
            if(playerView.getVisibility() == View.VISIBLE) {
                animateMove(playerView, playerView.getX(), playerView.getY(),
                        gameboardField.getX(), gameboardField.getY());
            } else {
                playerView.setX(gameboardField.getX());
                playerView.setY(gameboardField.getY());
                playerView.setVisibility(View.VISIBLE);
            }
        }
    }
}