package com.example.mankomania.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.example.mankomania.R;
import com.example.mankomania.api.SessionStatusService;
import com.example.mankomania.logik.Color;

import java.util.UUID;

/**
 * Diese Klasse fügt den "Logout"- und den "Finances & Stocks"-Button eine Funktionlität zu.
 */
public class ToolbarFunctionalities {

    private ToolbarFunctionalities() {}

    public static void setUpToolbar(Context context){
        Activity activity=(Activity)context;
        View toolbar=activity.findViewById(R.id.bottom_navigation);

        Button financesAndStocks=toolbar.findViewById(R.id.Board_ToFinancesAndStocks);
        financesAndStocks.setOnClickListener((View v) -> {
            Intent switchToFinancesAndStocks=new Intent(activity, FinancesAndStocks.class);
            context.startActivity(switchToFinancesAndStocks);
        });

        //Current Player displayen
        TextView currentPlayer=toolbar.findViewById(R.id.CurrentPlayer);
        SessionStatusService sessionStatusService=new SessionStatusService();
        sessionStatusService.registerObserver(new SessionStatusService.PlayersTurnObserver() {
            @Override
            public void onTurnChanged(String color, boolean newTurn) {
                activity.runOnUiThread(() -> {
                    currentPlayer.setText(color);
                });
            }
        });

        Button logout=activity.findViewById(R.id.Board_LogoutButton);
        logout.setOnClickListener((View v) -> {
            stopSessionStatusService(context);
            //TODO Daten des Spiels speichern etc.
            Intent fromBoardToLogin=new Intent(activity, MainActivityLogin.class);
            context.startActivity(fromBoardToLogin);
        });
    }

    private static void stopSessionStatusService(Context context){
        Intent sessionStatusServiceIntent=new Intent(context, SessionStatusService.class);
        context.stopService(sessionStatusServiceIntent);
    }
}
