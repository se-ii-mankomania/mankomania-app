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

import java.util.regex.Pattern;

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

        Button register=findViewById(R.id.Login_RegisterButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(MainActivityLogin.this,Register.class);
                startActivity(registerIntent);
            }
        });

        Button login=findViewById(R.id.Login_LoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailInput=findViewById(R.id.Login_Email);
                EditText passwordInput=findViewById(R.id.Login_Passwort);
                //TODO Verknüpfung Datenbank und E-Mail & Passwort prüfen
                boolean isValid=true;
                if(isNoValidEmail(emailInput.getText().toString())) {
                    emailInput.setError("E-Mail-Adresse ist ungültig.");
                }else if(!isValid)
                    passwordInput.setError("E-mail oder Passwort sind ungültig.");
                else{
                    Intent loginIntent = new Intent(MainActivityLogin.this, GameScore.class);
                    startActivity(loginIntent);
                }
            }
        });

    }

    /**
     * Diese Methode überprüft mit einem simplen Regex, ob es sich bei dem
     * Eingabestring um eine E-Mail-Adresse handelt.
     * @param email zu überprüfende E-Mail-Adresse aus Eingabefeld
     * @return ob E-Mail-Adresse nicht valide ist oder schon
     */

    static boolean isNoValidEmail(String email){
        String emailRegex ="^(.+)@(\\S+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return !pattern.matcher(email).matches();
    }
}