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

import com.example.mankomania.R;
import com.example.mankomania.api.Session;
import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.gameboardfields.GameboardField;
import com.example.mankomania.logik.Dice;
import com.example.mankomania.logik.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class EventRollDice extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean backPressedBlocked;

    private static final int SENSIBILITY_BORDER_FOR_SENSOR =10;

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
        FieldsHandler  fieldshandler = (FieldsHandler) getIntent().getSerializableExtra("fieldsHandler");

        sensorManager.unregisterListener(this);

        Dice dice=new Dice();
        int[] randomNumber=dice.throwDice();
        //TODO entsprechende Anzahl am Spielfeld weiterrücken
        String resultOfRollingDice = String.valueOf(randomNumber[0] + randomNumber[1]);

        //Ergebnis auf Würfel displayen
        ImageView diceOne=findViewById(R.id.RollDice_diceOne);
        ImageView diceTwo=findViewById(R.id.RollDice_diceTwo);

        int sourceDiceOne=getDiceDrawable(randomNumber[0]);
        int sourceDiceTwo=getDiceDrawable(randomNumber[1]);

        diceOne.setImageResource(sourceDiceOne);
        diceTwo.setImageResource(sourceDiceTwo);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        String token = sharedPreferences.getString("token", null);
        String lobbyId = sharedPreferences.getString("lobbyid", null);
        SessionAPI.getStatusByLobby(token, UUID.fromString(lobbyId), new SessionAPI.GetStatusByLobbyCallback() {

            @Override
            public void onGetStatusByLobbySuccess(HashMap<UUID, Session> sessions) {
                Session userSession = null;
                for(Session session: sessions.values()){
                    if(session.getUserId().equals(UUID.fromString(userId))){
                        userSession = session;
                        break;
                    }
                }
                Player player = new Player("", Objects.requireNonNull(userSession).getColor());
                GameboardField field = Objects.requireNonNull(fieldshandler).getField(userSession.getCurrentPosition()-1);
                player.setCurrentField(field);
                fieldshandler.movePlayer(player, randomNumber[0] + randomNumber[1]);

                SessionAPI.updatePlayerPosition(token, userId, player.getCurrentField().getId(), lobbyId, new SessionAPI.UpdatePositionCallback() {
                    @Override
                    public void onUpdateSuccess(String message) {
                        runOnUiThread(() -> {
                            int resource = getResId("field_" + player.getCurrentField().getId()+ "_description", R.string.class);
                            Toast.makeText(getApplicationContext(), getString(resource), Toast.LENGTH_LONG).show();

                        });

                    }

                    @Override
                    public void onUpdateFailure(String errorMessage) {
                        Toast.makeText(getApplicationContext(), "Error while updating positions", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            @Override
            public void onGetStatusByLobbyFailure(String errorMessage) {
                Toast.makeText(getApplicationContext(), "could not get lobbyStatus", Toast.LENGTH_SHORT).show();
            }
        });


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent backToBoard = new Intent(EventRollDice.this, Board.class);
            startActivity(backToBoard);
            //BackButton kann wieder freigegeben werden
            unblockBackButton();
        }, 2000);
        Toast.makeText(getApplicationContext(), "Deine Spielfigur zieht " + resultOfRollingDice + " Felder weiter.", Toast.LENGTH_SHORT).show();

    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
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
}