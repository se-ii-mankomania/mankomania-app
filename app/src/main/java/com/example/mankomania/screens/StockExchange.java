package com.example.mankomania.screens;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mankomania.R;

public class StockExchange extends AppCompatActivity implements SensorEventListener {
    
    int basc;
    int bdesc;
    int tasc;
    int tdesc;
    int kasc;
    int kdesc;
    int sonderzeichen;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean backPressedBlocked;

    private static final int SENSIBILITY_BORDER_FOR_SENSOR =10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blockBackButton();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_exchange);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeDrawables();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //den BackButton blockieren, damit Würfeln nicht abgebrochen werden kann
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedBlocked) {
                    Toast.makeText(StockExchange.this, "Bringe zuerst den Aktienkurs in Schwung!", Toast.LENGTH_SHORT).show();
                } else {
                    this.setEnabled(false);
                    StockExchange.super.finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        Button changeStockPricesButton=findViewById(R.id.StockExchange_ShakeButton);
        changeStockPricesButton.setOnClickListener(v -> changeStockPrices());
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
                //Wenn geschüttelt => Aktienkurs ändern
                changeStockPrices();
            }
        }
    }

    private void changeStockPrices() {
        //TODO API-Anbindung einfügen & Back-Button unblocken
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

    private void initializeDrawables(){
        basc =R.drawable.basc;
        bdesc=R.drawable.bdes;
        tasc=R.drawable.tasc;
        tdesc=R.drawable.tdes;
        kasc=R.drawable.kasc;
        kdesc=R.drawable.kdes;
        sonderzeichen=R.drawable.sonderzeichen;
    }
}