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
import com.example.mankomania.api.StockExchangeAPI;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

public class StockExchange extends AppCompatActivity implements SensorEventListener, StockExchangeAPI.GetStockChangesCallback {

    private String token;
    private UUID lobbyid;

    private ImageView stockExchangeImageView;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean backPressedBlocked;

    private static final int SENSIBILITY_BORDER_FOR_SENSOR =10;
    private static final int DELAY_MILLIS_BACK_TO_BOARD=4000;

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

        initSharedPreferences();

        stockExchangeImageView=findViewById(R.id.StockExchange_ImageView);

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
        changeStockPricesButton.setOnClickListener(v -> StockExchangeAPI.getStockChangesByLobbyID(token,lobbyid,this));
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
                StockExchangeAPI.getStockChangesByLobbyID(token,lobbyid,this);
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

    private int getDrawableId(String stockChanges){
        switch(stockChanges){
            case "basc": return R.drawable.basc;
            case "bdesc": return R.drawable.bdes;
            case "tasc": return R.drawable.tasc;
            case "tdesc": return R.drawable.tdes;
            case "kasc": return R.drawable.kasc;
            case "kdesc": return R.drawable.kdes;
            case "sonderzeichen": return R.drawable.sonderzeichen;
            default: return -1;
        }
    }

    @Override
    public void onGetStockChangesSuccess(String stockChanges) {
        stockChanges=stockChanges.trim();
        int imageViewId=getDrawableId(stockChanges);
        if(imageViewId!=-1) {
            runOnUiThread(() -> {
                stockExchangeImageView.setImageResource(imageViewId);
                navigateBackToBoard();
            });
        }else{
            runOnUiThread(() ->Toast.makeText(StockExchange.this, "Fehler:///", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onGetStockChangesFailure(String errorMessage) {
        runOnUiThread(() ->Toast.makeText(StockExchange.this, "Fehler:"+errorMessage, Toast.LENGTH_SHORT).show());
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
        } catch (GeneralSecurityException | IOException ignored) {
            Toast.makeText(getApplicationContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }
    }
    private void navigateBackToBoard(){
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent backToBoard = new Intent(StockExchange.this, Board.class);
            startActivity(backToBoard);
            //BackButton kann wieder freigegeben werden
            unblockBackButton();
        }, DELAY_MILLIS_BACK_TO_BOARD);
    }
}