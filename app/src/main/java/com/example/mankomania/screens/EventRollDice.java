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
import com.example.mankomania.api.PlayerSession;
import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.api.StockExchangeAPI;
import com.example.mankomania.gameboardfields.GameboardField;
import com.example.mankomania.logik.spieler.Dice;
import com.example.mankomania.logik.spieler.Player;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class EventRollDice extends AppCompatActivity implements SensorEventListener,StockExchangeAPI.StartStockExchangeCallback{

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean backPressedBlocked;

    private String token;
    private String lobbyId;
    private String userId;

    private static final int SENSIBILITY_BORDER_FOR_SENSOR =10;
    private static final int DELAY_MILLIS_BACK_TO_BOARD=2000;
    private Player player;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blockBackButton();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_roll_dice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        ToolbarFunctionalities.setUpToolbar(this);

        //den BackButton blockieren, damit Würfeln nicht abgebrochen werden kann
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedBlocked) {
                    Toast.makeText(EventRollDice.this, "Bitte zuerst würfeln!", Toast.LENGTH_SHORT).show();
                } else {
                    this.setEnabled(false);
                    EventRollDice.super.finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        Button rollDiceButton=findViewById(R.id.RollDice_RollingDiceButton);
        rollDiceButton.setOnClickListener(v -> rollDice());
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if ((Math.abs(x) > SENSIBILITY_BORDER_FOR_SENSOR || Math.abs(y) > SENSIBILITY_BORDER_FOR_SENSOR || Math.abs(z) > SENSIBILITY_BORDER_FOR_SENSOR)) {
                //Wenn geschüttelt => Würfeln
                rollDice();
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //muss nicht überschrieben werden, ist aber notwendig, um BackButton zu blockieren
    }
    private void unblockBackButton() {
        this.backPressedBlocked=false;
    }
    private void blockBackButton(){
        this.backPressedBlocked=true;
    }

    private void rollDice() {
        FieldsHandler fieldshandler = (FieldsHandler) getIntent().getSerializableExtra("fieldsHandler");
        if (fieldshandler == null) {
            runOnUiThread(() -> Toast.makeText(EventRollDice.this, "FieldsHandler is missing", Toast.LENGTH_SHORT).show());
            return;
        }

        int[] randomNumber = rollAndDisplayDice();

        SharedPreferences sharedPreferences = setupSharedPreferences();
        if (sharedPreferences == null) {
            runOnUiThread(()->Toast.makeText(EventRollDice.this, "Failed to setup SharedPreferences", Toast.LENGTH_SHORT).show());
            return;
        }
        updateUserPosition(sharedPreferences, randomNumber, fieldshandler);
        navigateBackToBoard();
        sensorManager.unregisterListener(this);
    }

    private int [] rollAndDisplayDice(){
        Dice dice=new Dice();
        int[] randomNumber=dice.throwDice();

        //Ergebnis auf Würfel displayen
        ImageView diceOne=findViewById(R.id.RollDice_diceOne);
        ImageView diceTwo=findViewById(R.id.RollDice_diceTwo);

        int sourceDiceOne=getDiceDrawable(randomNumber[0]);
        int sourceDiceTwo=getDiceDrawable(randomNumber[1]);

        diceOne.setImageResource(sourceDiceOne);
        diceTwo.setImageResource(sourceDiceTwo);

        String resultOfRollingDice = String.valueOf(randomNumber[0] + randomNumber[1]);
        runOnUiThread(()-> Toast.makeText(getApplicationContext(), "Deine Spielfigur zieht " + resultOfRollingDice + " Felder weiter.", Toast.LENGTH_SHORT).show());
        return randomNumber;
    }

    private SharedPreferences setupSharedPreferences(){
        sharedPreferences = null;

        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "MyPrefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (GeneralSecurityException | IOException ignored) {
            runOnUiThread(() ->Toast.makeText(getApplicationContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show());
        }
        return sharedPreferences;

    }

    private void updateUserPosition(SharedPreferences sharedPreferences, int [] randomNumber, FieldsHandler fieldshandler){
        userId = sharedPreferences.getString("userId", null);
        token = sharedPreferences.getString("token", null);
        lobbyId = sharedPreferences.getString("lobbyid", null);

        SessionAPI.getStatusByLobby(token, UUID.fromString(lobbyId), new SessionAPI.GetStatusByLobbyCallback() {

            @Override
            public void onGetStatusByLobbySuccess(HashMap<UUID, PlayerSession> sessions) {
                PlayerSession userPlayerSession = null;
                for(PlayerSession playerSession : sessions.values()){
                    if(playerSession.getUserId().equals(UUID.fromString(userId))){
                        userPlayerSession = playerSession;
                        break;
                    }
                }
                player = new Player("", Objects.requireNonNull(userPlayerSession).getColor());
                GameboardField field = Objects.requireNonNull(fieldshandler).getField(userPlayerSession.getCurrentPosition()-1);
                player.setCurrentField(field);

                int goToFieldId=fieldshandler.movePlayer(player, randomNumber[0] + randomNumber[1]);

                if(goToFieldId!=-1){
                    toastFieldDescription(player,0);
                }

                SessionAPI.updatePlayerPosition(token, userId, player.getCurrentField().getId(), lobbyId, new SessionAPI.UpdatePositionCallback() {
                    @Override
                    public void onUpdateSuccess(String message) {
                        toastFieldDescription(player,1000);
                    }

                    @Override
                    public void onUpdateFailure(String errorMessage) {
                        runOnUiThread(() ->Toast.makeText(getApplicationContext(), "Error while updating positions", Toast.LENGTH_SHORT).show());
                    }
                });
            }
            @Override
            public void onGetStatusByLobbyFailure(String errorMessage) {
                runOnUiThread(() ->Toast.makeText(getApplicationContext(), "could not get lobbyStatus", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void checkIfRedirectingIsNecessary(GameboardField field) {
        switch (field.getId()){
            case 45:
                goToBoese1();
                break;
            case 46:
                Intent startHorseRace = new Intent(EventRollDice.this, HorseRace.class);
                startActivity(startHorseRace);
                break;
            case 47:
                StockExchangeAPI.startStockExchange(token, UUID.fromString(lobbyId),this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("previousUserID", userId);
                editor.apply();
                break;
            case 48:
                //TODO add Start-Call for Casino
                break;
            default:
        }
    }

    private void goToBoese1(){
        Intent boese1 = new Intent(EventRollDice.this, Boese1.class);
        startActivity(boese1);
    }

    private void toastFieldDescription(Player player, int delayMillis){
        new Handler(Looper.getMainLooper()).postDelayed(() -> runOnUiThread(() -> {
            int resource = getResId("field_" + player.getCurrentField().getId() + "_description", R.string.class);
            if (resource == -1) {
                Toast.makeText(getApplicationContext(), "Field description could not be found", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(resource), Toast.LENGTH_LONG).show();
            }
        }), delayMillis);
    }

    private void navigateBackToBoard(){
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent backToBoard = new Intent(EventRollDice.this, Board.class);
            startActivity(backToBoard);
            //BackButton kann wieder freigegeben werden
            unblockBackButton();
            checkIfRedirectingIsNecessary(player.getCurrentField());
        }, DELAY_MILLIS_BACK_TO_BOARD);
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            return -1;
        }
    }
    /**
     * Diese Methode ordnet dem gewürfelten Ergenbnis das passende Image zu
     * @param result Ergebnis des Würfelwurfs
     * @return R.drawable mit richtigem Image für Würfelergebnis
     */
    private int getDiceDrawable(int result){
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

    @Override
    public void onStartStockExchangeSuccess(String successMessage) {
        //toast of this information would rather interfere with the user experience
    }

    @Override
    public void onStartStockExchangeFailure(String errorMessage) {
        runOnUiThread(() ->Toast.makeText(EventRollDice.this, "Fehler:"+errorMessage, Toast.LENGTH_SHORT).show());
    }
}