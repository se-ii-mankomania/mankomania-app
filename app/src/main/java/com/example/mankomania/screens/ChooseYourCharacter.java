package com.example.mankomania.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mankomania.R;
import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.api.SessionStatusService;
import com.example.mankomania.logik.Color;

import java.util.List;
import java.util.UUID;

public class ChooseYourCharacter extends AppCompatActivity implements SessionAPI.GetUnavailableColorsByLobbyCallback, SessionAPI.SetColorCallback {

    private Handler handler;
    private static final int INTERVAL_MS = 1000;
    private String token;
    private UUID lobbyid;
    private List<Color> unavailiableColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_your_character);

        SharedPreferences sharedPreferencesToken = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        token = sharedPreferencesToken.getString("token", null);
        SharedPreferences sharedPreferencesLobbyId = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        lobbyid = UUID.fromString(sharedPreferencesLobbyId.getString("lobbyid", null));

        handler = new Handler(Looper.getMainLooper());
        startRepeatingTask();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button start=findViewById(R.id.ChooseYourCharacter_StartButton);
        start.setOnClickListener((View v) -> {
            RadioGroup colorSelection=findViewById(R.id.ChooseYourCharacter_ColorSelectionRadioGroup);
            int selectedColor=colorSelection.getCheckedRadioButtonId();
            if(selectedColor!=-1) {
                saveColorChoice(selectedColor);
            }else{
                Toast.makeText(getApplicationContext(), "Bitte wähle eine Farbe aus.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            restoreRadioButtons();
            Log.wtf("WTF",lobbyid.toString());
            SessionAPI.getUnavailableColorsByLobby(token,lobbyid,ChooseYourCharacter.this);
            handler.postDelayed(this, INTERVAL_MS);
        }
    };
    void startRepeatingTask() {
        handler.postDelayed(runnable, INTERVAL_MS);
    }
    void stopRepeatingTask() {
        handler.removeCallbacks(runnable);
    }

    /**
     * updateAvailableRadioButtons() überprüft, welche Farben noch zur Auswahl
     * zur Verfügung stehen und graut die vergebenen Optionen aus
     */

    private void updateAvailableRadioButtons(){
        RadioGroup colorSelection=findViewById(R.id.ChooseYourCharacter_ColorSelectionRadioGroup);
        //TODO add sharedPrefrences
        for(int i=0;i<colorSelection.getChildCount();i++){
            RadioButton currentButton= (RadioButton) colorSelection.getChildAt(i);
            String colorString= String.valueOf(currentButton.getText());
            Color color=convertTextToEnum(colorString);
            if(unavailiableColors.contains(color)){
                 currentButton.setEnabled(false);
                 currentButton.setTextColor(ContextCompat.getColor(this,R.color.disabled_grey));
                 currentButton.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.disabled_grey)));
             }
         }
    }

    /**
     * restoreRadioButtons() bewirkt, dass die gesamte RadioGroup wieder in ihren
     * anfänglichen Zustand zurückgesetzt wird. Jeder Button ist auswählbar.
     */
    private void restoreRadioButtons(){
        RadioButton purple=findViewById(R.id.ChooseYourCharacter_PurplePlayer);
        purple.setEnabled(true);
        purple.setTextColor(ContextCompat.getColor(this,R.color.purplePlayer));
        purple.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.purplePlayer)));

        RadioButton green=findViewById(R.id.ChooseYourCharacter_GreenPlayer);
        green.setEnabled(true);
        green.setTextColor(ContextCompat.getColor(this,R.color.greenPlayer));
        green.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.greenPlayer)));

        RadioButton red=findViewById(R.id.ChooseYourCharacter_RedPlayer);
        red.setEnabled(true);
        red.setTextColor(ContextCompat.getColor(this,R.color.redPlayer));
        red.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.redPlayer)));

        RadioButton blue=findViewById(R.id.ChooseYourCharacter_BluePlayer);
        blue.setEnabled(true);
        blue.setTextColor(ContextCompat.getColor(this,R.color.bluePlayer));
        blue.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.bluePlayer)));
    }

    /**
     * saveColorChoice() legt die ausgewählte Farbe für den Spieler ab
     * und setzt den Wert für alle später zu gebrauchenden Variablen
     * @param buttonId gibt die ID des RadioButtons zurück
     */

    private void saveColorChoice(int buttonId){
        RadioButton selectedButton=findViewById(buttonId);
        String color= convertTextToEnglishReferenceName(String.valueOf(selectedButton.getText()));
        SessionAPI.setColor(token,lobbyid, color,ChooseYourCharacter.this);
    }
    private Color convertTextToEnum(String color){
        switch (color){
            case "Blau": return Color.BLUE;
            case "Rot":return Color.RED;
            case "Grün":return Color.GREEN;
            case "Lila":return Color.PURPLE;
            default: return null;
        }
    }
    private String convertTextToEnglishReferenceName(String color){
        switch (color){
            case "Blau": return "blue";
            case "Rot":return "red";
            case "Grün":return "green";
            case "Lila":return "lila";
            default: return "";
        }
    }
    @Override
    public void onGetUnavailableColorsByLobbySuccess(List<Color> colors) {
        this.unavailiableColors=colors;
        updateAvailableRadioButtons();
    }

    @Override
    public void onGetUnavailableColorsByLobbyFailure(String errorMessage) {
        runOnUiThread(() -> Toast.makeText(ChooseYourCharacter.this, "Fehler: " + errorMessage, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onSetColorSuccess(String successMessage) {
        //Session beitreten
        stopRepeatingTask();
        Intent startGameWithChosenCharacter=new Intent(ChooseYourCharacter.this, Board.class);
        startActivity(startGameWithChosenCharacter);
    }

    @Override
    public void onSetColorFailure(String errorMessage) {
        runOnUiThread(() -> Toast.makeText(ChooseYourCharacter.this, "Fehler: " + errorMessage, Toast.LENGTH_SHORT).show());
    }
}