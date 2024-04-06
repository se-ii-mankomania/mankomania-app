package com.example.mankomania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        Button register=findViewById(R.id.Register_RegisterButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailInput=findViewById(R.id.Register_Email);
                EditText passwordInput=findViewById(R.id.Register_Passwort);
                //TODO Nutzer in Datenbank anlegen
                boolean availablePassword=true;
                boolean availableEmail=true;
                if(MainActivityLogin.isNoValidEmail(emailInput.getText().toString()) && availableEmail) {
                    emailInput.setError("E-Mail-Adresse ist ungültig.");
                }else if(!availablePassword){
                    passwordInput.setError("Passwort ist bereits vergeben.");
                }else{
                    Intent registerIntent = new Intent(Register.this, MainActivityLogin.class);
                    startActivity(registerIntent);
                }
            }
        });

    }
}