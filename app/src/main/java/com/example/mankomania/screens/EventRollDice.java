package com.example.mankomania.screens;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mankomania.R;
import com.example.mankomania.logik.Dice;

public class EventRollDice extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            if ((Math.abs(x) > 10 || Math.abs(y) > 10 || Math.abs(z) > 10)) {
                rollDice();
            }
        }
    }

    private void rollDice() {
        sensorManager.unregisterListener(this);
        Dice dice=new Dice();
        int[] randomNumber=dice.throwDice();
        //TODO entsprechende Anzahl am Spielfeld weiterrücken
        String resultOfRollingDice = String.valueOf(randomNumber[0] + randomNumber[1]);
        TextView result=findViewById(R.id.RollDice_resultAnswer);
        result.setText(resultOfRollingDice);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent backToBoard = new Intent(EventRollDice.this, Board.class);
            startActivity(backToBoard);
        }, 1000);

        Toast.makeText(getApplicationContext(), "Deine Spielfigur zieht " + resultOfRollingDice + " Felder weiter.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}