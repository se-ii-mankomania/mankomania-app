package com.example.mankomania.screens;

import android.os.Bundle;
import android.view.View;

import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mankomania.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class HorseRace extends AppCompatActivity {
    private ImageView horse1, horse2, horse3, horse4;
    private Button startRace;
    private TextView resultTextView;

    private Button chooseHorse1, chooseHorse2, chooseHorse3, chooseHorse4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_horserace);

        horse1 = findViewById(R.id.horse1);
        horse2 = findViewById(R.id.horse2);
        horse3 = findViewById(R.id.horse3);
        horse4 = findViewById(R.id.horse4);


        startRace = findViewById(R.id.startRaceButton);
        resultTextView = findViewById(R.id.textView);

        RadioGroup chooseHorse = findViewById(R.id.horseRadioGroup);
        RadioGroup chooseBetAmount = findViewById(R.id.betRadioGroup);
        TextView chooseBetAmountText = findViewById(R.id.betAmount);

        startRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTextView.setVisibility(View.INVISIBLE);
                chooseHorse.setVisibility(View.INVISIBLE);
                chooseBetAmount.setVisibility(View.INVISIBLE);
                chooseBetAmountText.setVisibility(View.INVISIBLE);
                startRace(new int[]{2, 3, 1, 4});

            }
        });
    }
    private void startRace(int[] horsePlaces){
        int [] durations = new int[4];
        //plaetze der pferde auf sekunden mappen
        for (int i = 0; i < horsePlaces.length; i++) {
            durations[horsePlaces[i]-1] = (i + 1) * 1000;
        }

        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < horsePlaces.length; i++) {
            resultBuilder.append(i+1).append(". Platz: ").append(horsePlaces[i]).append("\n");
        }

        animateHorse(horse1, durations[0]);
        animateHorse(horse2, durations[1]);
        animateHorse(horse3, durations[2]);
        animateHorse(horse4, durations[3]);

        //ergebnis anzeigen
        horse1.postDelayed(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText(resultBuilder.toString());
                resultTextView.setVisibility(View.VISIBLE);
            }
        }, Math.max(durations[0], Math.max(durations[1], Math.max(durations[2], durations[3]))));
    }
    private void animateHorse(View horse, int duration){
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        TranslateAnimation animation = new TranslateAnimation(0, screenWidth- horse.getWidth(), 0, 0);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        horse.startAnimation(animation);
    }
}
