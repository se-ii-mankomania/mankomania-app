package com.example.mankomania.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.mankomania.R;
import com.example.mankomania.api.HorseRaceAPI;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

public class HorseRace extends AppCompatActivity {
    private ImageView horse1, horse2, horse3, horse4;
    private Button startRaceButton;
    private TextView resultTextView;
    private String token;
    private UUID lobbyid;
    private String userId;

    private Button returnButton;
    private int selectedHorse;
    private int selectedBetId ;
    private int selectedHorseId;
    private int selectedBetAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_horserace);

        initSharedPreferences();

        horse1 = findViewById(R.id.horse1);
        horse2 = findViewById(R.id.horse2);
        horse3 = findViewById(R.id.horse3);
        horse4 = findViewById(R.id.horse4);

        returnButton = findViewById(R.id.returnButton);

        startRaceButton = findViewById(R.id.startRaceButton);
        resultTextView = findViewById(R.id.textView);

        RadioGroup chooseHorse = findViewById(R.id.horseRadioGroup);
        RadioGroup chooseBetAmount = findViewById(R.id.betRadioGroup);
        TextView chooseBetAmountText = findViewById(R.id.betAmount);

        returnButton.setVisibility(View.INVISIBLE);

        returnButton.setOnClickListener(v -> {
            Intent backToBoard = new Intent(HorseRace.this, Board.class);
            startActivity(backToBoard);
        });


        startRaceButton.setOnClickListener(v -> {
            resultTextView.setVisibility(View.INVISIBLE);
            selectedHorseId = chooseHorse.getCheckedRadioButtonId();
            if (selectedHorseId == -1) {
                runOnUiThread(()-> Toast.makeText(HorseRace.this, "Bitte wähle ein Pferd aus.", Toast.LENGTH_SHORT).show());
                return;
            }

            selectedHorse = -1;
            if (selectedHorseId == R.id.horse1Radio) {
                selectedHorse = 1;
            } else if (selectedHorseId == R.id.horse2Radio) {
                selectedHorse = 2;
            } else if (selectedHorseId == R.id.horse3Radio) {
                selectedHorse = 3;
            } else if (selectedHorseId == R.id.horse4Radio) {
                selectedHorse = 4;
            }
            selectedBetId = chooseBetAmount.getCheckedRadioButtonId();
            if (selectedBetId == -1) {
                runOnUiThread(()->Toast.makeText(HorseRace.this, "Bitte wähle deinen Geldeinsatz aus.", Toast.LENGTH_SHORT).show());
                return;
            }
            selectedBetAmount = -1;

            if (selectedBetId == R.id.bet5000Radio) {
                selectedBetAmount = 5000;
            } else if (selectedBetId == R.id.bet10000Radio) {
                selectedBetAmount = 10000;
            } else if (selectedBetId == R.id.bet20000Radio) {
                selectedBetAmount = 20000;
            }

            chooseHorse.setVisibility(View.INVISIBLE);
            chooseBetAmount.setVisibility(View.INVISIBLE);
            chooseBetAmountText.setVisibility(View.INVISIBLE);
            startRaceButton.setVisibility(View.INVISIBLE);

            HorseRaceAPI.startHorseRace(token, lobbyid, userId, selectedBetAmount, selectedHorse, new HorseRaceAPI.GetHorseRaceResultsCallback() {
                @Override
                public void onGetHorseRaceResultsSuccess(int[] horsePlaces) {
                    startRace(horsePlaces);
                }

                @Override
                public void onGetHorseRaceResultsFailure(String errorMessage) {
                    runOnUiThread(()->Toast.makeText(HorseRace.this, "Fehler: " + errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
        });

    }
    private void startRace(int[] horsePlaces){
        int [] durations = new int[4];
        //plaetze der pferde auf sekunden mappen
        for (int i = 0; i < horsePlaces.length; i++) {
            durations[horsePlaces[i]-1] = (i + 1) * 1000;
        }

        // Ermittlung des Platzes des ausgewählten Pferdes
        String horseMessage = "Dein Pferd hat den";
        int platz = -1;
        for (int i = 0; i < horsePlaces.length; i++) {
            if (horsePlaces[i] == selectedHorse) {
                platz = i + 1;
                break;
            }
        }
        if (platz != -1) {
            horseMessage += " " + platz + ". Platz belegt!";
        } else {
            horseMessage = "Dein Pferd hat das Rennen nicht beendet.";
        }
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(horseMessage).append("\n");
        for (int i = 0; i < horsePlaces.length; i++) {
            resultBuilder.append(i+1).append(". Platz: Pferd ").append(horsePlaces[i]).append("\n");
        }


        animateHorse(horse1, durations[0]);
        animateHorse(horse2, durations[1]);
        animateHorse(horse3, durations[2]);
        animateHorse(horse4, durations[3]);

        //ergebnis anzeigen
        horse1.postDelayed(() -> {
            resultTextView.setText(resultBuilder.toString());
            resultTextView.setVisibility(View.VISIBLE);
            returnButton.setVisibility(View.VISIBLE);
        }, Math.max(durations[0], Math.max(durations[1], Math.max(durations[2], durations[3]))));

    }
    private void animateHorse(View horse, int duration){
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        TranslateAnimation animation = new TranslateAnimation(0, (float)screenWidth- horse.getWidth(), 0, 0);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        horse.startAnimation(animation);
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
            lobbyid= UUID.fromString(lobbyidString);
            userId = sharedPreferences.getString("userId", null);
        } catch (GeneralSecurityException | IOException ignored) {
            runOnUiThread(()->Toast.makeText(getApplicationContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show());
        }
    }
}
