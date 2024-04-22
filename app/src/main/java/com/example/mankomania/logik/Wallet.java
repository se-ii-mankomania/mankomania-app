package com.example.mankomania.logik;

import java.util.EnumMap;
import java.util.Map;

public class Wallet {
    private Map<NoteTypes, Integer> notes = new EnumMap<>(NoteTypes.class);

    public Wallet(){
        notes.put(NoteTypes.FIVETHOUSAND, 6);
        notes.put(NoteTypes.TENTHOUSAND, 7);
        notes.put(NoteTypes.FIFTYTHOUSAND, 6);
        notes.put(NoteTypes.HUNDREDTHOUSAND, 6);
    }
    public void addMoney(NoteTypes note, int amount){
        int currentAmount = notes.getOrDefault(note, 0);
        notes.put(note, currentAmount + amount);
    }
    public void cheatMoney(NoteTypes noteTypes, int amount){
        int currentAmount = notes.getOrDefault(noteTypes, 0);
        if(currentAmount >= amount){
            notes.put(noteTypes, currentAmount - amount);
        }else {
        throw new IllegalArgumentException("Nicht genug Scheine");
        }
    }

    public void removeMoney(NoteTypes noteTypes, int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("Der zu entfernende Betrag muss größer als 0 sein.");
        }
        //Speichert die aktuelle Anzahl an Scheinen des gewünschten Typs
        int currentAmount = notes.getOrDefault(noteTypes, 0);
        //Berechnet den Gesamtbetrag welcher benötigt wird
        int neededValue = noteTypes.getValue() * amount;

        //Entfernen von Scheinen wenn davon genug vorhanden sind
        if(currentAmount >= amount){
            notes.put(noteTypes, currentAmount - amount);
        } else {
            if(totalAmount() < neededValue){
                throw new IllegalArgumentException("Sie haben gewonnen");
            }
            //Entfernt was möglich ist und zieht von den anderen Scheine den restlichen Betrag ab
            if(currentAmount > 0){
                neededValue -= currentAmount * noteTypes.getValue();
                notes.put(noteTypes, 0);
            }
        }
    }
    public int totalAmount(){
        int total = 0;
        for (NoteTypes note : NoteTypes.values()) {
            total += note.getValue() * notes.get(note);
        }
        return total;
    }
}
