package com.example.mankomania.api;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mankomania.logik.Color;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SessionStatusService extends Service {

    private static final String TAG = "StatusUpdateService";
    private static final long INTERVAL_MS = 5000; // 5 Sekunden
    private final Set<PositionObserver> positionObservers = new HashSet<>();
    private final Set<BalanceObserver> balanceObservers = new HashSet<>();
    private final Set<PlayersTurnObserver> playersTurnObservers = new HashSet<>();

    private Handler handler;
    private Runnable runnable;
    private String token;
    private UUID lobbyId;

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

        SharedPreferences sharedPreferencesToken = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        token = sharedPreferencesToken.getString("token", null);
        SharedPreferences sharedPreferencesLobbyId = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        lobbyId = UUID.fromString(sharedPreferencesLobbyId.getString("lobbyid", null));


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                sendStatusRequest();
                handler.postDelayed(this, INTERVAL_MS);
            }
        };
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
                public void onGetStatusByLobbySuccess(HashMap<UUID, Session> sessions) {
                    // brauchi no was?
                }

                @Override
                public void onGetStatusByLobbyFailure(String errorMessage) {
                    Toast.makeText(getApplicationContext(), "Fehler: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Lobby-ID ist null", Toast.LENGTH_SHORT).show();
        }
    }

    public void notifyUpdatesInSession(Session newSession, UUID userId){
        HashMap<UUID,Session> sessions=SessionAPI.getSessions();
        Session fromerSession=sessions.get(userId);

        if(fromerSession==null || Objects.equals(fromerSession.getCurrentPosition(), newSession.getCurrentPosition())){
            notifyPositionChanged(userId,newSession.getCurrentPosition());
        }
        if(fromerSession==null || Objects.equals(fromerSession.getBalance(), newSession.getBalance())){
            notifyBalanceChanged(userId,newSession.getBalance());
        }
        if(fromerSession==null || Objects.equals(fromerSession.getIsPlayersTurn(), newSession.getIsPlayersTurn())){
            notifyTurnChanged(convertEnumToStringColor(newSession.getColor()),newSession.getIsPlayersTurn());
        }
    }

    private String convertEnumToStringColor(Color color){
        switch(color){
            case BLUE: return "blau";
            case RED: return "rot";
            case PURPLE: return "lila";
            case GREEN: return "grün";
            default: return "";
        }
    }
    public interface PositionObserver {
        void onPositionChanged(UUID userId, int newPosition);
    }
    public interface BalanceObserver{
        void onBalanceChanged(UUID userId, int newBalance);
    }
    public interface PlayersTurnObserver{
        void onTurnChanged(String color, boolean newTurn);
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

    public void notifyPositionChanged(UUID userId,int newPosition) {
        for (SessionStatusService.PositionObserver observer : positionObservers) {
            observer.onPositionChanged(userId, newPosition);
        }
    }

    public void notifyBalanceChanged(UUID userId,int newBalance) {
        for (SessionStatusService.BalanceObserver observer : balanceObservers) {
            observer.onBalanceChanged(userId, newBalance);
        }
    }

    public void notifyTurnChanged(String color,boolean newTurn) {
        for (SessionStatusService.PlayersTurnObserver observer : playersTurnObservers) {
            observer.onTurnChanged(color, newTurn);
        }
    }
}