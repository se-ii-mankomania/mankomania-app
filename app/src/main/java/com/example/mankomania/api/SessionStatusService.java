package com.example.mankomania.api;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.mankomania.logik.spieler.Color;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SessionStatusService extends Service {
    private static final long INTERVAL_MS = 3500; // 3.5 Sekunden
    private final Set<PositionObserver> positionObservers = new HashSet<>();
    private final Set<BalanceObserver> balanceObservers = new HashSet<>();
    private final Set<PlayersTurnObserver> playersTurnObservers = new HashSet<>();
    private final Set<BalanceBelowThresholdObserver> balanceBelowThresholdObservers = new HashSet<>();
    private final Set<ToBoardObserver> toBoardObservers = new HashSet<>();
    private final Set<ToStockExchangeObserver> toStockExchangeObservers = new HashSet<>();
    private final Set<ToCasinoObserver> toCasinoObservers = new HashSet<>();
    private HandlerThread handlerThread;
    private Handler handler;
    private Runnable runnable;
    private String token;
    private UUID lobbyId;
    private int formerMiniGameID=0;
    private final String ERROR_TAG="SessionStatusServiceError";

    private static SessionStatusService instance;

    public static SessionStatusService getInstance() {
        if (instance == null) {
            instance = new SessionStatusService();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferencesInit();

        handlerThread = new HandlerThread("SessionStatusServiceThread");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());

        runnable = new Runnable() {
            @Override
            public void run() {
                sendStatusRequest();
                handler.postDelayed(this, INTERVAL_MS);
            }
        };
    }

    public void sharedPreferencesInit(){
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
            String lobbyid=sharedPreferences.getString("lobbyid",null);
            lobbyId= UUID.fromString(lobbyid);
        } catch (GeneralSecurityException | IOException ignored) {
            Log.e(ERROR_TAG,"SharedPreferences konnten nicht geladen werden.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRepeatingTask();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();

        if (handlerThread != null) {
            handlerThread.quit();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startRepeatingTask() {
        handler.postDelayed(runnable, INTERVAL_MS);
    }

    private void stopRepeatingTask() {
        handler.removeCallbacks(runnable);
    }

    private void sendStatusRequest() {
        if (lobbyId != null) {
            SessionAPI.getStatusByLobby(token, lobbyId, new SessionAPI.GetStatusByLobbyCallback() {
                @Override
                public void onGetStatusByLobbySuccess(HashMap<UUID, PlayerSession> sessions) {
                    // brauchi no was?
                }

                @Override
                public void onGetStatusByLobbyFailure(String errorMessage) {
                    Log.e(ERROR_TAG,"Fehler: " + errorMessage);
                }
            });
        } else {
            Log.e(ERROR_TAG,"Lobby-ID ist null");
        }
    }

    public void notifyUpdatesInSession(PlayerSession newPlayerSession, UUID userId){
        notifyPositionChanged(newPlayerSession);

        if(newPlayerSession.getIsPlayersTurn()){
            notifyTurnChanged(convertEnumToStringColor(newPlayerSession.getColor()), newPlayerSession.getIsPlayersTurn(),userId);
        }
        if(newPlayerSession.getBalance()<=0){
            notifyBalanceBelowThreshold(userId,convertEnumToStringColor(newPlayerSession.getColor()));
        }
    }

    public String convertEnumToStringColor(Color color){
        switch(color){
            case BLUE: return "blau";
            case RED: return "rot";
            case LILA: return "lila";
            case GREEN: return "grün";
            default: return "";
        }
    }

    public void notifyUpdatesForMiniGames(int miniGameID){
        boolean noIDMatch=false;
        if (formerMiniGameID!=miniGameID){
            switch (miniGameID){
                case 0:
                    notifyToBoard();
                    break;
                case 47:
                    notifyToStockExchange();
                    break;
                case 48:
                    notifyToCasino();
                    break;
                default:
                    noIDMatch=true;
            }
            if(!noIDMatch){
                formerMiniGameID=miniGameID;
            }
        }
    }
    public interface ToBoardObserver {
        void onToBoard();
    }
    public interface ToStockExchangeObserver {
        void onToStockExchange();
    }
    public interface ToCasinoObserver {
        void onToCasino();
    }
    public interface BalanceBelowThresholdObserver {
        void onBalanceBelowThreshold(UUID userId,String color);
    }
    public interface PositionObserver {
        void onPositionChanged(PlayerSession playerSession);
    }
    public interface BalanceObserver{
        void onBalanceChanged(UUID userId, int newBalance);
    }
    public interface PlayersTurnObserver{
        void onTurnChanged(String color, boolean newTurn,UUID userid);
    }

    //TODO register and remove Observer toBoard in your MiniGame
    public void registerObserver(SessionStatusService.ToBoardObserver observer) {
        toBoardObservers.add(observer);
    }

    public void removeObserver(SessionStatusService.ToBoardObserver observer) {
        toBoardObservers.remove(observer);
    }
    public void registerObserver(SessionStatusService.ToStockExchangeObserver observer) {
        toStockExchangeObservers.add(observer);
    }

    public void removeObserver(SessionStatusService.ToStockExchangeObserver observer) {
        toStockExchangeObservers.remove(observer);
    }
    public void registerObserver(SessionStatusService.ToCasinoObserver observer) {
        toCasinoObservers.add(observer);
    }

    public void removeObserver(SessionStatusService.ToCasinoObserver observer) {
        toCasinoObservers.remove(observer);
    }
    public void registerObserver(SessionStatusService.PositionObserver observer) {
        positionObservers.add(observer);
    }

    public void removeObserver(SessionStatusService.PositionObserver observer) {
        positionObservers.remove(observer);
    }
    public void registerObserver(SessionStatusService.BalanceObserver observer) {
        balanceObservers.add(observer);
    }

    public void removeObserver(SessionStatusService.BalanceObserver observer) {
        balanceObservers.remove(observer);
    }
    public void registerObserver(SessionStatusService.PlayersTurnObserver observer) {
        playersTurnObservers.add(observer);
    }

    public void removeObserver(SessionStatusService.PlayersTurnObserver observer) {
        playersTurnObservers.remove(observer);
    }
    public void notifyToBoard() {
        for (SessionStatusService.ToBoardObserver observer : toBoardObservers) {
            observer.onToBoard();
        }
    }
    public void notifyToStockExchange() {
        for (SessionStatusService.ToStockExchangeObserver observer : toStockExchangeObservers) {
            observer.onToStockExchange();
        }
    }
    public void notifyToCasino() {
        for (SessionStatusService.ToCasinoObserver observer : toCasinoObservers) {
            observer.onToCasino();
        }
    }
    public void notifyPositionChanged(PlayerSession playerSession) {
        for (SessionStatusService.PositionObserver observer : positionObservers) {
            observer.onPositionChanged(playerSession);
        }
    }

    public void notifyBalanceChanged(UUID userId,int newBalance) {
        for (SessionStatusService.BalanceObserver observer : balanceObservers) {
            observer.onBalanceChanged(userId, newBalance);
        }
    }

    public void notifyTurnChanged(String color, boolean newTurn, UUID userId) {
        for (SessionStatusService.PlayersTurnObserver observer : playersTurnObservers) {
            observer.onTurnChanged(color, newTurn,userId);
        }
    }
    public void registerObserver(BalanceBelowThresholdObserver observer) {
        balanceBelowThresholdObservers.add(observer);
    }

    public void removeObserver(BalanceBelowThresholdObserver observer) {
        balanceBelowThresholdObservers.remove(observer);
    }

    public void notifyBalanceBelowThreshold(UUID userId, String color) {
        for (BalanceBelowThresholdObserver observer : balanceBelowThresholdObservers) {
            observer.onBalanceBelowThreshold(userId,color);
        }
    }

    public Set<PositionObserver> getPositionObservers() {
        return positionObservers;
    }

    public Set<BalanceObserver> getBalanceObservers() {
        return balanceObservers;
    }

    public Set<PlayersTurnObserver> getPlayersTurnObservers() {
        return playersTurnObservers;
    }

    public Set<BalanceBelowThresholdObserver> getBalanceBelowThresholdObservers() {
        return balanceBelowThresholdObservers;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}