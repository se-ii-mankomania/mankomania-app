package com.example.mankomania;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Random;

public class Event_RollDice extends AppCompatActivity {

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
        Random random=new Random();
        int randomNumber=random.nextInt(6) + 1;
        //TODO entsprechende Anzahl am Spielfeld weiterrücken
        String resultOfRollingDice= String.valueOf(randomNumber);

        Button rollDice=findViewById(R.id.RollDice_RollButton);
        rollDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView result=findViewById(R.id.RollDice_resultAnswer);
                result.setText(resultOfRollingDice);

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent backToBoard=new Intent(Event_RollDice.this, Board.class);
                        startActivity(backToBoard);
                    }
                },700);
                Toast.makeText(getApplicationContext(), "Deine Spielfigur zieht "+randomNumber+" Felder weiter.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}