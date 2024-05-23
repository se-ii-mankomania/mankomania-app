package com.example.mankomania.screens;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.mankomania.R;
import com.example.mankomania.api.LobbyAPI;
import com.example.mankomania.api.Status;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class CreateNewLobby extends AppCompatActivity implements LobbyAPI.AddLobbyCallback{

    EditText nameInput;
    SwitchCompat privateLobbySwitch;
    EditText passwordInput;
    Spinner maxPlayerSpinner;
    Button createLobbyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_lobby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindElements();

        deactivatePassword();

        setOptionsForMayPlayerSpinner();

        onlyAllowPasswordIfLobbyIsPrivate();

        addButtonFunctionalityForCreateLobby();
    }

    private void addButtonFunctionalityForCreateLobby() {
        createLobbyButton.setOnClickListener(v -> {
            String lobbyName = nameInput.getText().toString();
            String lobbyPassword = passwordInput.getText().toString();
            boolean isLobbyPrivate = privateLobbySwitch.isChecked();
            int maxPlayers = Integer.parseInt((String) maxPlayerSpinner.getSelectedItem());

            // make sure to "send" password = null if password == "" !!!
            if (lobbyPassword.isEmpty()) {
                lobbyPassword = null;
            }

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

                String token = sharedPreferences.getString("token", null);
                LobbyAPI.addLobby(token, lobbyName, lobbyPassword, isLobbyPrivate, maxPlayers, Status.OPEN, CreateNewLobby.this);


            } catch (GeneralSecurityException | IOException ignored) {
                Toast.makeText(getApplicationContext(), "SharedPreferences konnten nicht geladen werden.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onlyAllowPasswordIfLobbyIsPrivate() {
        // only allow a password if the lobby is private
        privateLobbySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            passwordInput.setEnabled(isChecked);
            passwordInput.setFocusable(isChecked);
            passwordInput.setFocusableInTouchMode(isChecked);
            if (!isChecked) {
                passwordInput.setText("");
            }
        });
    }

    private void setOptionsForMayPlayerSpinner() {
        // options for maxPlayerSpinner
        String[] options = { "2", "3", "4"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxPlayerSpinner.setAdapter(adapter);
    }

    private void deactivatePassword() {
        // deactivate password at first
        passwordInput.setEnabled(false);
        passwordInput.setFocusable(false);
        passwordInput.setFocusableInTouchMode(false);
    }

    private void bindElements() {
        nameInput = findViewById(R.id.nameEditText);
        passwordInput = findViewById(R.id.passwortEditText);
        privateLobbySwitch = findViewById(R.id.privateLobbySwitch);
        maxPlayerSpinner = findViewById(R.id.maxSpielerSpinner);
        createLobbyButton = findViewById(R.id.createLobbyButton);
    }

    @Override
    public void onSuccess(String message) {
        runOnUiThread(() -> Toast.makeText(CreateNewLobby.this, "Lobby erfolgreich erstellt: " + message, Toast.LENGTH_SHORT).show());

        // go back to login page
        Intent goToGameScore = new Intent(CreateNewLobby.this, GameScore.class);
        startActivity(goToGameScore);
    }

    @Override
    public void onFailure(String errorMessage) {
        runOnUiThread(() -> Toast.makeText(CreateNewLobby.this, "Lobbyerstellung fehlgeschlagen: " + errorMessage, Toast.LENGTH_SHORT).show());
    }
}