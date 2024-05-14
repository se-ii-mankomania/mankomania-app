package com.example.mankomania.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mankomania.R;
import com.example.mankomania.api.AuthAPI;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Pattern;

public class MainActivityLogin extends AppCompatActivity implements AuthAPI.AuthCallback{

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

        Button register=findViewById(R.id.Login_RegisterButton);
        register.setOnClickListener((View v) -> {
            Intent registerIntent=new Intent(MainActivityLogin.this, Register.class);
            startActivity(registerIntent);
        });

        Button login=findViewById(R.id.Login_LoginButton);
        login.setOnClickListener(v -> {
            EditText emailInput=findViewById(R.id.Login_Email);
            EditText passwordInput=findViewById(R.id.Login_Passwort);

            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if(isNoValidEmail(email)) {
                emailInput.setError("E-Mail-Adresse ist ungültig.");
            } else {
                AuthAPI.login(email, password, MainActivityLogin.this);
            }
        });

    }

    @Override
    public void onSuccess(String token) {
        // store token
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

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", token);
            editor.apply();
        } catch (GeneralSecurityException | IOException ignored) {
            Toast.makeText(getApplicationContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }

        // go to next page
        Intent loginIntent = new Intent(MainActivityLogin.this, GameScore.class);
        startActivity(loginIntent);
    }

    @Override
    public void onFailure(String errorMessage) {
        // handle login failure
        runOnUiThread(() -> Toast.makeText(MainActivityLogin.this, "Login fehlgeschlagen: " + errorMessage, Toast.LENGTH_SHORT).show());
    }

    /**
     * Diese Methode überprüft mit einem simplen Regex, ob es sich bei dem
     * Eingabestring um eine E-Mail-Adresse handelt.
     * @param email zu überprüfende E-Mail-Adresse aus Eingabefeld
     * @return ob E-Mail-Adresse nicht valide ist oder schon
     */

    public static boolean isNoValidEmail(String email){
        String emailRegex ="^(.+)@([\\w.-]+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return !pattern.matcher(email).matches();
    }
}