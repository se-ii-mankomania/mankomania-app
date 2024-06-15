package com.example.mankomania.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mankomania.R;
import com.example.mankomania.api.SessionStatusService;

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

        Button playerScores = toolbar.findViewById(R.id.Board_PlayerScores);
        playerScores.setOnClickListener((View v) -> {
            if (activity instanceof FragmentActivity) {
                FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

                if (fragment instanceof PlayerScoresFragment) {
                    fragmentManager.popBackStack();
                } else {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, new PlayerScoresFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
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
