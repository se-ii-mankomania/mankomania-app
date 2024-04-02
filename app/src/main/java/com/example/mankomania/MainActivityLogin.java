package com.example.mankomania;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivityLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button login=findViewById(R.id.Login_LoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameInput=findViewById(R.id.Login_Nutzername);
                EditText passwordInput=findViewById(R.id.Login_Passwort);
                //TODO Verknüpfung Datenbank und Nutzername & Passwort prüfen
                boolean isValid=true;
                if(isValid) {
                    Intent loginIntent = new Intent(MainActivityLogin.this, GameScore.class);
                    startActivity(loginIntent);
                }else{
                    usernameInput.setError("Nutzername ist ungültig");
                    passwordInput.setError("Passwort ist ungültig.");
                }
            }
        });

    }
}