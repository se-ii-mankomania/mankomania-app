package com.example.mankomania;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

/**
 * Diese Klasse fügt den "Logout"- und den "Finances & Stocks"-Button eine Funktionlität zu.
 */
public class ToolbarFunctionalities {

    public ToolbarFunctionalities(Context context){
        Activity activity=(Activity)context;
        View toolbar=activity.findViewById(R.id.bottom_navigation);

        Button financesAndStocks=toolbar.findViewById(R.id.Board_ToFinancesAndStocks);
        financesAndStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchToFinancesAndStocks=new Intent(activity, FinancesAndStocks.class);
                context.startActivity(switchToFinancesAndStocks);
            }
        });

        Button logout=activity.findViewById(R.id.Board_LogoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Daten des Spiels speichern etc.
                Intent fromBoardToLogin=new Intent(activity, MainActivityLogin.class);
                context.startActivity(fromBoardToLogin);
            }
        });
    }
}
