package com.example.mankomania.screens;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.mankomania.R;
import com.example.mankomania.logik.aktien.StockTypes;
import com.example.mankomania.logik.geldboerse.NoteTypes;

public class FinancesAndStocks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finances_and_stocks);

        PlayerViewModel playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

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

        playerViewModel.getPlayer().observe(this, player -> {
            // UI mit Daten aus dem Player-Objekt aktualisieren
            bills5k.setText(String.valueOf(player.getWallet().getNoteCount(NoteTypes.FIVETHOUSAND)));
            bills10k.setText(String.valueOf(player.getWallet().getNoteCount(NoteTypes.TENTHOUSAND)));
            bills50k.setText(String.valueOf(player.getWallet().getNoteCount(NoteTypes.FIFTYTHOUSAND)));
            bills100k.setText(String.valueOf(player.getWallet().getNoteCount(NoteTypes.HUNDREDTHOUSAND)));
            balance.setText(String.valueOf(player.getWalletBalance()));

            bruchstahlAG.setText(String.valueOf(player.getStockCount(StockTypes.BRUCHSTAHL_AG)));
            trockenoelAG.setText(String.valueOf(player.getStockCount(StockTypes.TROCKENOEL_AG)));
            kurzschlussAG.setText(String.valueOf(player.getStockCount(StockTypes.KURZSCHLUSS_VERSORGUNGS_AG)));
            numberStocks.setText(String.valueOf(player.getAmountOfStock().values().stream().mapToInt(Integer::intValue).sum()));
        });

            Button toBoard = findViewById(R.id.FinancesStocks_BackToBoard);
            toBoard.setOnClickListener((View v) -> {
                Intent switchToBoard = new Intent(FinancesAndStocks.this, Board.class);
                startActivity(switchToBoard);
            });

            Button logout = findViewById(R.id.FinancesStocks_LogoutButton);
            logout.setOnClickListener((View v) -> {
                Intent fromFinancesAndStocksToLogin = new Intent(FinancesAndStocks.this, MainActivityLogin.class);
                startActivity(fromFinancesAndStocksToLogin);
            });
    }
}
