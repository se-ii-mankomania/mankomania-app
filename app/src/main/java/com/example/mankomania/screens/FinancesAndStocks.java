package com.example.mankomania.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.mankomania.R;
import com.example.mankomania.api.Session;
import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.logik.aktien.StockTypes;
import com.example.mankomania.logik.geldboerse.NoteTypes;
import com.example.mankomania.logik.geldboerse.Wallet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FinancesAndStocks extends AppCompatActivity implements SessionAPI.GetStatusByLobbyCallback {

    private String token;
    private UUID lobbyid;
    private UUID userId;

    private TextView bills5k;
    private TextView bills10k;
    private TextView bills50k;
    private TextView bills100k;
    private TextView balance;

    private TextView bruchstahlAG;
    private TextView trockenoelAG;
    private TextView kurzschlussAG;
    private TextView numberStocks;

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
            String useridString = sharedPreferences.getString("userId", null);
            userId=UUID.fromString(useridString);
        } catch (GeneralSecurityException | IOException ignored) {
            Toast.makeText(getApplicationContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }

        SessionAPI.getStatusByLobby(token, lobbyid,this);

        bills5k=findViewById(R.id.Finances_5kbillsAnswer);
        bills10k=findViewById(R.id.Finances_10kbillsAnswer);
        bills50k=findViewById(R.id.Finances_50kbillsAnswer);
        bills100k=findViewById(R.id.Finances_100kbillsAnswer);
        balance=findViewById(R.id.Finances_totalAnswer);

        bruchstahlAG=findViewById(R.id.Stocks_BruchstahlAGAnswer);
        trockenoelAG=findViewById(R.id.Stocks_TrockenoelAGAnswer);
        kurzschlussAG=findViewById(R.id.Stocks_KurzschulssAGAnswer);
        numberStocks=findViewById(R.id.Stocks_totalAnswer);

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

    @Override
    public void onGetStatusByLobbySuccess(HashMap<UUID, Session> sessions) {
        for (Map.Entry<UUID, Session> entry : sessions.entrySet()) {
            if(entry.getKey().equals(userId)){
                setTextsForTextVies(entry.getValue());
            }
        }
    }

    private void setTextsForTextVies(Session session) {
        int currentBalance=session.getBalance();
        balance.setText(String.valueOf(currentBalance));

        Wallet wallet=new Wallet();
        wallet.addMoney(currentBalance);
        bills5k.setText(String.valueOf(wallet.getNoteCount(NoteTypes.FIVETHOUSAND)));
        bills10k.setText(String.valueOf(wallet.getNoteCount(NoteTypes.TENTHOUSAND)));
        bills50k.setText(String.valueOf(wallet.getNoteCount(NoteTypes.FIFTYTHOUSAND)));
        bills100k.setText(String.valueOf(wallet.getNoteCount(NoteTypes.HUNDREDTHOUSAND)));

        bruchstahlAG.setText(String.valueOf(session.getAmountBShares()));
        trockenoelAG.setText(String.valueOf(session.getAmountTShares()));
        kurzschlussAG.setText(String.valueOf(session.getAmountKVShares()));
        int numberOfStocks=session.getAmountBShares()+session.getAmountKVShares()+session.getAmountTShares();
        numberStocks.setText(String.valueOf(numberOfStocks));
    }

    @Override
    public void onGetStatusByLobbyFailure(String errorMessage) {
        runOnUiThread(() -> Toast.makeText(FinancesAndStocks.this, "Fehler: " + errorMessage, Toast.LENGTH_SHORT).show());
    }
}
