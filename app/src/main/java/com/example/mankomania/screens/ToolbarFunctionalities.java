package com.example.mankomania.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.mankomania.R;
import com.example.mankomania.api.SessionStatusService;

/**
 * Diese Klasse fügt den "Logout"- und den "Finances & Stocks"-Button eine Funktionlität zu.
 */
public class ToolbarFunctionalities {

    public interface OnPlayerScoresClickListener {
        void onPlayerScoresClicked();
    }
    private static OnPlayerScoresClickListener playerScoresClickListener;

    public static void setOnPlayerScoresClickListener(OnPlayerScoresClickListener listener) {
        playerScoresClickListener = listener;
    }

    private ToolbarFunctionalities() {}

    public static void setUpToolbar(Context context){
        Activity activity=(Activity)context;
        View toolbar=activity.findViewById(R.id.bottom_navigation);

        Button financesAndStocks=toolbar.findViewById(R.id.Board_ToFinancesAndStocks);
        financesAndStocks.setOnClickListener((View v) -> {
            Intent switchToFinancesAndStocks=new Intent(activity, FinancesAndStocks.class);
            context.startActivity(switchToFinancesAndStocks);
        });

        Button playerScores = toolbar.findViewById(R.id.Board_PlayerScores);
        playerScores.setOnClickListener(v -> {
            if (playerScoresClickListener != null) {
                playerScoresClickListener.onPlayerScoresClicked();
            }
        });

        Button logout=activity.findViewById(R.id.Board_LogoutButton);
        logout.setOnClickListener((View v) -> {
            stopSessionStatusService(context);
            Intent fromBoardToLogin=new Intent(activity, MainActivityLogin.class);
            context.startActivity(fromBoardToLogin);
        });
    }

    private static void stopSessionStatusService(Context context){
        Intent sessionStatusServiceIntent=new Intent(context, SessionStatusService.class);
        context.stopService(sessionStatusServiceIntent);
    }
}
