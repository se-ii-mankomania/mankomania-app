package com.example.mankomania.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import com.example.mankomania.api.SessionStatusService;
import com.example.mankomania.api.StockExchangeAPI;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

public class StockExchange extends AppCompatActivity implements StockExchangeAPI.GetStockChangesCallback,StockExchangeAPI.GetStockTrendCallback,StockExchangeAPI.SetStockTrendCallback,StockExchangeAPI.StopStockExchangeCallback,SwipeGestureListener.OnSwipeListener {

    private String token;
    private UUID lobbyid;
    private UUID userId;

    private Handler handler;
    private ImageView stockExchangeImageView;

    private boolean backButtonblocked;
    private boolean isPlayersTurn;
    private GestureDetector gestureDetector;
    private static final int DELAY_MILLIS_STOCK_TREND_UPDATE=1000;
    private static final String ERROR_MESSAGE_INTRODUCTORY_STATEMENT="Fehler:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blockBackButton();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_exchange);

        isPlayersTurn=false;
        initSharedPreferences();
        registerObserver();


        handler = new Handler(Looper.getMainLooper());
        startRepeatingStockTrendUpdates();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        stockExchangeImageView=findViewById(R.id.StockExchange_ImageView);

        gestureDetector = new GestureDetector(this, new SwipeGestureListener(this));

        //den BackButton blockieren, damit Würfeln nicht abgebrochen werden kann
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backButtonblocked) {
                    Toast.makeText(StockExchange.this, "Bringe zuerst den Aktienkurs in Schwung!", Toast.LENGTH_SHORT).show();
                } else {
                    this.setEnabled(false);
                    StockExchange.super.finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void registerObserver() {
        SessionStatusService sessionStatusService=SessionStatusService.getInstance();

        sessionStatusService.registerObserver((SessionStatusService.ToBoardObserver)()-> runOnUiThread(() -> {
            Intent toBoard=new Intent(StockExchange.this, Board.class);
            startActivity(toBoard);
        }));

        sessionStatusService.registerObserver((color, newTurn, userid) -> runOnUiThread(() ->
                isPlayersTurn=newTurn && userid.equals(userId)

        ));
    }

    private void startRepeatingStockTrendUpdates() {handler.postDelayed(runnable, DELAY_MILLIS_STOCK_TREND_UPDATE);}
    private void stopRepeatingStockTrendUpdates() {handler.removeCallbacks(runnable);}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingStockTrendUpdates();

        SessionStatusService sessionStatusService=SessionStatusService.getInstance();
        sessionStatusService.removeObserver((SessionStatusService.ToBoardObserver)()->{});
        sessionStatusService.removeObserver((color, newTurn, userid) -> {});
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            StockExchangeAPI.getStockTrendByLobbyID(token,lobbyid,StockExchange.this);
            handler.postDelayed(this, DELAY_MILLIS_STOCK_TREND_UPDATE);
        }
    };
    
    private void blockBackButton(){
        this.backButtonblocked =true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
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
        StockExchangeAPI.setStockTrend(token,lobbyid,stockChanges,this);
        StockExchangeAPI.stopStockExchange(token,lobbyid,this);
        updateView(stockChanges);
    }

    @Override
    public void onGetStockChangesFailure(String errorMessage) {
        runOnUiThread(() ->Toast.makeText(StockExchange.this, ERROR_MESSAGE_INTRODUCTORY_STATEMENT+errorMessage, Toast.LENGTH_SHORT).show());
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
            String useridString=sharedPreferences.getString("userId",null);
            userId=UUID.fromString(useridString);
        } catch (GeneralSecurityException | IOException ignored) {
            Toast.makeText(getApplicationContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSwipeUp() {
        if(isPlayersTurn) {
            StockExchangeAPI.getStockChangesByLobbyID(token, lobbyid, this);
        }else{
            runOnUiThread(()-> Toast.makeText(getApplicationContext(),"Du bist nicht an der Reihe!",Toast.LENGTH_SHORT).show());
        }
    }

    private void updateView(String stockChanges){
        stockChanges=stockChanges.trim();
        int imageViewId=getDrawableId(stockChanges);
        if(imageViewId!=-1) {
            runOnUiThread(() -> stockExchangeImageView.setImageResource(imageViewId));
        }else{
            runOnUiThread(() ->Toast.makeText(StockExchange.this, ERROR_MESSAGE_INTRODUCTORY_STATEMENT, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onSetStockTrendSuccess(String successMessage) {
        runOnUiThread(() ->Toast.makeText(StockExchange.this, "Der Aktienkurs hat sich geändert", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onSetStockTrendFailure(String errorMessage) {
        runOnUiThread(() ->Toast.makeText(StockExchange.this, ERROR_MESSAGE_INTRODUCTORY_STATEMENT+errorMessage, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onGetStockTrendSuccess(String stocktrend) {
        updateView(stocktrend);
    }

    @Override
    public void onGetStockTrendFailure(String errorMessage) {
        runOnUiThread(() ->Toast.makeText(StockExchange.this, ERROR_MESSAGE_INTRODUCTORY_STATEMENT+errorMessage, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStopStockExchangeSuccess(String successMessage) {
        //toasting this information would be again ruining the user experience
    }

    @Override
    public void onStopStockExchangeFailure(String errorMessage) {
        runOnUiThread(() ->Toast.makeText(StockExchange.this, ERROR_MESSAGE_INTRODUCTORY_STATEMENT+errorMessage, Toast.LENGTH_SHORT).show());
    }
}