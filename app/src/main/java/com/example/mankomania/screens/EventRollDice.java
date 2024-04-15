package com.example.mankomania.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mankomania.R;
import com.example.mankomania.logik.Dice;

import java.util.Arrays;

public class EventRollDice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_roll_dice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ToolbarFunctionalities toolbarFunctionalities=new ToolbarFunctionalities(this);
        Dice dice=new Dice();
        int[] randomNumber=dice.throwDice();
        //TODO entsprechende Anzahl am Spielfeld weiterrücken
        String resultOfRollingDice= String.valueOf(randomNumber[0]+randomNumber[1]);

        Button rollDice=findViewById(R.id.RollDice_RollButton);
        rollDice.setOnClickListener((View v) -> {
            TextView result=findViewById(R.id.RollDice_resultAnswer);
            result.setText(resultOfRollingDice);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent backToBoard = new Intent(EventRollDice.this, Board.class);
                startActivity(backToBoard);
            }, 700);

            Toast.makeText(getApplicationContext(), "Deine Spielfigur zieht " + Arrays.toString(randomNumber) + " Felder weiter.", Toast.LENGTH_SHORT).show();
        });
    }
}