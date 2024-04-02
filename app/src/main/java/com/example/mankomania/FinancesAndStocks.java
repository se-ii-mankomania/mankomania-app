package com.example.mankomania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FinancesAndStocks extends AppCompatActivity {
    PlayerViewModel playerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finances_and_stocks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView bills5k=findViewById(R.id.Finances_5kbillsAnswer);
        TextView bills10k=findViewById(R.id.Finances_10kbillsAnswer);
        TextView bills50k=findViewById(R.id.Finances_50kbillsAnswer);
        TextView bills100k=findViewById(R.id.Finances_100kbillsAnswer);
        TextView balance=findViewById(R.id.Finances_totalTxt);

        TextView bruchstahlAG=findViewById(R.id.Stocks_BruchstahlAGAnswer);
        TextView trockenoelAG=findViewById(R.id.Stocks_TrockenoelAGAnswer);
        TextView kurzschlussAG=findViewById(R.id.Stocks_KurzschulssAGAnswer);
        TextView numberStocks=findViewById(R.id.Stocks_totalAnswer);

        //TODO mithilfe von ViewModel(?) ein Observable implemntieren,dass die Daten enthält
        /*playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        playerViewModel.getCurrentPlayer().observe(this, new Observer<Player>(){
            @Override
            public void onChanged(Player player) {
                bills5k.setText(player.get5kbills());
                bills10k.setText(player.get10kbills());
                bills50k.setText(player.get50kbills());
                bills100k.setText(player.get100kbills());
                balance.setText(player.getbalance());

                bruchstahlAG.setText(player.getBruchstahlAG());
                trockenoelAG.setText(player.getTrockenoelAG());
                kurzschlussAG.setText(player.getKurzschlussAG());
                numberStocks.setText(player.getStocksTotal());

            }
        });*/


        Button toBoard=findViewById(R.id.FinancesStocks_BackToBoard);
        toBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchToBoard=new Intent(FinancesAndStocks.this, Board.class);
                startActivity(switchToBoard);
            }
        });

        Button logout=findViewById(R.id.FinancesStocks_LogoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Daten des Spiels speichern etc.
                Intent fromFinancesAndStocksToLogin=new Intent(FinancesAndStocks.this, MainActivityLogin.class);
                startActivity(fromFinancesAndStocksToLogin);
            }
        });




    }
}