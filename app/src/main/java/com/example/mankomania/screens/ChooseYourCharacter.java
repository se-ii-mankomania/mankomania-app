package com.example.mankomania.screens;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mankomania.R;

public class ChooseYourCharacter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_your_character);

        //TODO Wann finden Updates statt?
        updateAvailableRadioButtons();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button start=findViewById(R.id.ChooseYourCharacter_StartButton);
        start.setOnClickListener((View v) -> {
            updateAvailableRadioButtons();
            RadioGroup colorSelection=findViewById(R.id.ChooseYourCharacter_ColorSelectionRadioGroup);
            int selectedColor=colorSelection.getCheckedRadioButtonId();
            if(selectedColor!=-1) {
                saveColorChoice(selectedColor);
                Intent startGameWithChosenCharacter=new Intent(ChooseYourCharacter.this, Board.class);
                startActivity(startGameWithChosenCharacter);
            }else{
                Toast.makeText(getApplicationContext(), "Bitte wähle eine Farbe aus.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * updateAvailableRadioButtons() überprüft, welche Farben noch zur Auswahl
     * zur Verfügung stehen und graut die vergebenen Optionen aus
     */

    private void updateAvailableRadioButtons(){
        RadioGroup colorSelection=findViewById(R.id.ChooseYourCharacter_ColorSelectionRadioGroup);
         for(int i=0;i<colorSelection.getChildCount();i++){
             RadioButton currentButton= (RadioButton) colorSelection.getChildAt(i);
             //TODO führe Check durch, ob die Farbe schon vergeben ist (via id oder Farbe)
             //Notes: muss in Echtzeit geschehen => mit Sockets oder ähnlichem
             boolean unavaliable=false;
             //id=ChooseYourCharacter_GreenPlayer => via Farbe nicht accesible
             //Idee: id umbenennen=> Vergleich mit Strings => naming conventions für andere Strings
             if(unavaliable){
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

    //TODO wann muss ich die Button wieder in ihren ursprünglichen Zustand versetzen
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
        String color= String.valueOf(selectedButton.getText());
        //Bsp: color=Blau
        //TODO Farbe für Spieler speichern
    }
}