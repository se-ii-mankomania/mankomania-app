package com.example.mankomania.logik.spieler;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class Wallet {
    private Map<NoteTypes, Integer> notes = new EnumMap<>(NoteTypes.class);
    private boolean isEmpty = false;


    public Wallet(){
        notes.put(NoteTypes.FIVETHOUSAND, 6);
        notes.put(NoteTypes.TENTHOUSAND, 7);
        notes.put(NoteTypes.FIFTYTHOUSAND, 6);
        notes.put(NoteTypes.HUNDREDTHOUSAND, 6);
    }
    public void addMoney(int amount){
        if (amount <= 0) {
            throw new IllegalArgumentException("Der hinzuzufügende Betrag muss größer als 0 sein.");
        }

        NoteTypes[] noteValues = NoteTypes.values();
        Arrays.sort(noteValues, (a, b) -> Integer.compare(b.getValue(), a.getValue())); // Scheine absteigend nach ihrem Wert sortieren

        for (NoteTypes note : noteValues) {
            int noteValue = note.getValue();
            if (noteValue <= amount) {
                int countToAdd = amount / noteValue; // Berechnen, wie viele Scheine dieses Typs hinzugefügt werden können
                int currentAmount = notes.getOrDefault(note, 0);
                notes.put(note, currentAmount + countToAdd);
                amount = amount % noteValue; // Verbleibenden Betrag aktualisieren
            }
        }
    }

  public  void removeMoney(int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("Der zu entfernende Betrag muss größer als 0 sein.");
        }
        int total = totalAmount();

        if (total <= amount){
            isEmpty = true;
        }
        removeAmount(amount);
  }

  private void removeAmount(int amount){
        NoteTypes[] noteValues = NoteTypes.values();
        Arrays.sort(noteValues, (a, b) -> Integer.compare(b.getValue(), a.getValue()));

        for(NoteTypes note : noteValues){
            if(amount <= 0) break;

            int noteValue = note.getValue();
            int noteCount = notes.getOrDefault(note, 0);

            if(noteCount > 0 && noteValue <= amount){
                int maxAmountToUse = Math.min(noteCount, amount / noteValue);
                int valueToRemove = maxAmountToUse * noteValue;

                notes.put(note, noteCount - maxAmountToUse);
                amount -= valueToRemove;
            }
        }
    }


    // Methode, um die Anzahl der Scheine eines bestimmten Typs zu bekommen
    public int getNoteCount(NoteTypes noteType) {
        return notes.getOrDefault(noteType, 0);
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public int totalAmount(){
        int total = 0;
        for (NoteTypes note : NoteTypes.values()) {
            total += note.getValue() * notes.get(note);
        }
        return total;
    }
}
