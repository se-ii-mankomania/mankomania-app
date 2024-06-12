package com.example.mankomania.screens;

import android.os.Bundle;
import android.view.View;

import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
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


        startRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTextView.setVisibility(View.INVISIBLE);
                startRace();
            }
        });
    }
    private void startRace(){
        Random random = new Random();
        int [] durations = new int[4];

        for (int i = 0; i < 4; i++) {
            durations[i] = random.nextInt(15000) + 2000;
        }

        Map<Integer, String> horseDurationMap = new HashMap<>();
        horseDurationMap.put(durations[0], "Pferd 1");
        horseDurationMap.put(durations[1], "Pferd 2");
        horseDurationMap.put(durations[2], "Pferd 3");
        horseDurationMap.put(durations[3], "Pferd 4");

        //sortieren nach zeit der pferde
        Map<Integer, String> sortedHorseDurationMap = new TreeMap<>(horseDurationMap);

        StringBuilder resultBuilder = new StringBuilder();
        int place = 1;
        for (Map.Entry<Integer, String> entry : sortedHorseDurationMap.entrySet()) {
            resultBuilder.append(place).append(". Platz: ").append(entry.getValue()).append("\n");
            place++;
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
