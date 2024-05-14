package com.example.mankomania.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mankomania.R;
import com.example.mankomania.api.AuthAPI;

public class Register extends AppCompatActivity implements AuthAPI.AuthCallback {

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
        register.setOnClickListener((View v) -> {
            EditText emailInput=findViewById(R.id.Register_Email);
            EditText passwordInput=findViewById(R.id.Register_Passwort);

            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if(MainActivityLogin.isNoValidEmail(email)) {
                emailInput.setError("E-Mail-Adresse ist ungültig.");
            } else if (password.length() <= 7) {
                passwordInput.setError("Passwort muss >7 Zeichen lang sein.");
            } else {
                AuthAPI.register(email, password, Register.this);
            }
        });

    }

    @Override
    public void onSuccess(String message) {
        runOnUiThread(() -> Toast.makeText(Register.this, "Registrierung erfolgreich: " + message, Toast.LENGTH_SHORT).show());

        // go back to login page
        Intent loginIntent = new Intent(Register.this, MainActivityLogin.class);
        startActivity(loginIntent);
    }

    @Override
    public void onFailure(String errorMessage) {
        runOnUiThread(() -> Toast.makeText(Register.this, "Registrierung fehlgeschlagen: " + errorMessage, Toast.LENGTH_SHORT).show());
    }
}