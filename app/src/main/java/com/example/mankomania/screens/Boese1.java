package com.example.mankomania.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.mankomania.R;
import com.example.mankomania.logik.spieler.Dice;
import com.example.mankomania.api.Boese1API;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

public class Boese1 extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean backPressedBlocked;
    private int sum = 0;

    private int oneCounter = 0;
    private boolean rolling = true;

    private String token;
    private UUID lobbyid;
    private UUID userId;
    private TextView resultSumTextView;

    private static final int SENSIBILITY_BORDER_FOR_SENSOR = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blockBackButton();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_boese1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initSharedPreferences();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        ToolbarFunctionalities.setUpToolbar(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedBlocked) {
                    Toast.makeText(Boese1.this, "Bitte würfeln!", Toast.LENGTH_SHORT).show();
                } else {
                    this.setEnabled(false);
                    Boese1.super.finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        resultSumTextView = findViewById(R.id.RollDice_Sum);

        Button rollDiceButton = findViewById(R.id.RollDice_RollingDiceButton);
        rollDiceButton.setOnClickListener(v -> rollDice());

        Button stopRollingButton = findViewById(R.id.StopRollingButton);
        stopRollingButton.setOnClickListener(v -> stopRolling());
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (rolling && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if ((Math.abs(x) > SENSIBILITY_BORDER_FOR_SENSOR || Math.abs(y) > SENSIBILITY_BORDER_FOR_SENSOR || Math.abs(z) > SENSIBILITY_BORDER_FOR_SENSOR)) {
                rollDice();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No need to handle accuracy changes for this implementation
    }

    private void blockBackButton() {
        this.backPressedBlocked = true;
    }

    private void unblockBackButton() {
        this.backPressedBlocked = false;
    }

    private void rollDice() {
        Dice dice = new Dice();
        int[] randomNumber = dice.throwDice();

        if (randomNumber[0] == 1 || randomNumber[1] == 1) {
            if (randomNumber[0] == 1) {
                oneCounter++;
            }
            if (randomNumber[1] == 1) {
                oneCounter++;
            }
            rolling = false;
            Toast.makeText(getApplicationContext(), "Du hast eine 1 gewürfelt!", Toast.LENGTH_SHORT).show();
            sendApiRequest(token, lobbyid, sum, oneCounter);
            unblockBackButton();
        }

        sum += randomNumber[0] + randomNumber[1];
        ImageView diceOne = findViewById(R.id.RollDice_diceOne);
        ImageView diceTwo = findViewById(R.id.RollDice_diceTwo);

        int sourceDiceOne = getDiceDrawable(randomNumber[0]);
        int sourceDiceTwo = getDiceDrawable(randomNumber[1]);

        diceOne.setImageResource(sourceDiceOne);
        diceTwo.setImageResource(sourceDiceTwo);

        resultSumTextView.setText("Dein Ergebnis lautet: " + sum);

        if (!rolling) {
            Button rollDiceButton = findViewById(R.id.RollDice_RollingDiceButton);
            rollDiceButton.setEnabled(false);
            navigateBackToBoard();
        }
    }


    private void stopRolling() {
        rolling = false;
        Toast.makeText(getApplicationContext(), "Du hast ein Summe von " + sum +" gewürfelt.", Toast.LENGTH_SHORT).show();
        sendApiRequest(token, lobbyid, sum, oneCounter);
        Button rollDiceButton = findViewById(R.id.RollDice_RollingDiceButton);
        rollDiceButton.setEnabled(false);
        unblockBackButton();
        navigateBackToBoard();
    }

    private int getDiceDrawable(int result) {
        switch (result) {
            case 1:
                return R.drawable.dice1;
            case 2:
                return R.drawable.dice2;
            case 3:
                return R.drawable.dice3;
            case 4:
                return R.drawable.dice4;
            case 5:
                return R.drawable.dice5;
            case 6:
                return R.drawable.dice6;
            default:
                return R.drawable.dice;
        }
    }
    private void navigateBackToBoard(){
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent backToBoard = new Intent(Boese1.this, Board.class);
            startActivity(backToBoard);
            unblockBackButton();
        }, 2000);
    }

    private void initSharedPreferences() {
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

            token = sharedPreferences.getString("token", null);
            String lobbyidString = sharedPreferences.getString("lobbyid", null);
            lobbyid=UUID.fromString(lobbyidString);
            String useridString = sharedPreferences.getString("userId", null);
            userId=UUID.fromString(useridString);
        } catch (GeneralSecurityException | IOException ignored) {
            Toast.makeText(getApplicationContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendApiRequest(String token, UUID lobbyid, int sum, int one){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Boese1API.sendBoeseRequest(token, lobbyid, sum, one);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Änderungen konnten nicht gespeichert werden.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        thread.start();
    }
}