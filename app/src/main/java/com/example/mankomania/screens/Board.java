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

        Button rollDice = setupViews();

        startSessionService();

        setupRollDiceButton();

        setupZoomLayout();

        setupWindowInsets();

        setupToolbar();

        setupBackButton();

        UUID userId = initSharedPreferences();

        registerObservers(userId, rollDice);


    }

    private Button setupViews(){
        //zuerst Button enablen, zur Sicherheit
        Button rollDice = findViewById(R.id.Board_ButtonDice);
        rollDice.setEnabled(false);
        return rollDice;
    }
    private void startSessionService(){
        Intent sessionStatusServiceIntent = new Intent(this, SessionStatusService.class);
        startService(sessionStatusServiceIntent);
    }

    private void setupRollDiceButton(){
        Button rollDice = findViewById(R.id.Board_ButtonDice);
        rollDice.setOnClickListener(v -> {
            Intent toEventRollDice = new Intent(Board.this, EventRollDice.class);
            toEventRollDice.putExtra("fieldsHandler", fieldsHandler);
            startActivity(toEventRollDice);
        });
    }
    private void setupZoomLayout(){
        ZoomLayout zoomLayout = findViewById(R.id.zoom_linear_layout);
        zoomLayout.setOnTouchListener((View v, MotionEvent event) -> {
            zoomLayout.init(Board.this);
            return false;
        });
    }

    private void setupWindowInsets(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void setupToolbar(){
        ToolbarFunctionalities.setUpToolbar(this);
    }

    private void setupBackButton(){
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

    private UUID initSharedPreferences(){
        UUID userId  = null;

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


        } catch (GeneralSecurityException | IOException ignored) {
            Toast.makeText(getApplicationContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }
    private void registerObservers(UUID userId, Button rollDice){
        SessionStatusService sessionStatusService = SessionStatusService.getInstance();

        sessionStatusService.registerObserver((SessionStatusService.PlayersTurnObserver) (color, newTurn,userid) -> runOnUiThread(() -> {
            rollDice.setEnabled(newTurn && userid.equals(userId));

        }));

        sessionStatusService.registerObserver((SessionStatusService.PositionObserver) session -> runOnUiThread(() ->
                updatePlayerPosition(session)
        ));

        sessionStatusService.registerObserver((SessionStatusService.BalanceBelowThresholdObserver) (userIdWinner, colorWinner) -> runOnUiThread(() -> {
            stopSessionStatusService();
            Intent toEndWinner = new Intent(Board.this, EndWinner.class);
            toEndWinner.putExtra("Winner",colorWinner);
            startActivity(toEndWinner);
            finish();
        }));
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

        setupPlayerImages(findViewById(R.id.player_blue), cellWidth, cellHeight);
        setupPlayerImages(findViewById(R.id.player_green), cellWidth, cellHeight);
        setupPlayerImages(findViewById(R.id.player_purple), cellWidth, cellHeight);
        setupPlayerImages(findViewById(R.id.player_red), cellWidth, cellHeight);

    }

    private void setupPlayerImages(ImageView playerImage, int cellWidth, int cellHeight){
        playerImage.getLayoutParams().height = cellHeight;
        playerImage.getLayoutParams().width = cellWidth;
        playerImage.setVisibility(View.INVISIBLE);
        playerImage.requestLayout();
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