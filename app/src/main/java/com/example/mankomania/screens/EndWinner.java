package com.example.mankomania.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mankomania.R;

public class EndWinner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_end_winner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView winner=findViewById(R.id.winnerTxt);

        Intent fromBoard=getIntent();
        String winnerColor=fromBoard.getStringExtra("Winner");

        winner.setText(String.format("%s%s", getString(R.string.gewonnen_hat), winnerColor));

        OnBackPressedCallback callback = new OnBackPressedCallback(true){

            @Override
            public void handleOnBackPressed() {
                Intent backToLogin=new Intent(EndWinner.this, MainActivityLogin.class);
                startActivity(backToLogin);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}