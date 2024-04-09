package com.example.mankomania;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;

public class CreateNewLobby extends AppCompatActivity {

    EditText nameInput;
    Switch privateLobbySwitch;
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

        // bind elements
        nameInput = findViewById(R.id.nameEditText);
        passwordInput = findViewById(R.id.passwortEditText);
        privateLobbySwitch = findViewById(R.id.privateLobbySwitch);
        maxPlayerSpinner = findViewById(R.id.maxSpielerSpinner);
        createLobbyButton = findViewById(R.id.createLobbyButton);

        // deactivate password at first
        passwordInput.setEnabled(false);
        passwordInput.setFocusable(false);
        passwordInput.setFocusableInTouchMode(false);

        // options for maxPlayerSpinner
        String[] options = { "2", "3", "4"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxPlayerSpinner.setAdapter(adapter);

        // only allow a password if the lobby is private
        privateLobbySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                passwordInput.setEnabled(isChecked);
                passwordInput.setFocusable(isChecked);
                passwordInput.setFocusableInTouchMode(isChecked);
                if (!isChecked) {
                    passwordInput.setText("");
                }
            }
        });

        createLobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}