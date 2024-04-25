package com.example.mankomania.logik;

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
    public void addMoney(NoteTypes note, int amount){
        int currentAmount = notes.getOrDefault(note, 0);
        notes.put(note, currentAmount + amount);
    }
    
//    public void cheatMoney(NoteTypes noteTypes, int amount){
//        int currentAmount = notes.getOrDefault(noteTypes, 0);
//        if(currentAmount >= amount){
//            notes.put(noteTypes, currentAmount - amount);
//        }else {
//        throw new IllegalArgumentException("Nicht genug Scheine");
//        }
//    }

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
                isEmpty = true;
                throw new IllegalArgumentException("Sie haben gewonnen");
            }
            //Entfernt was möglich ist und zieht von den anderen Scheine den restlichen Betrag ab
            if(currentAmount > 0){
                neededValue -= currentAmount * noteTypes.getValue();
                notes.put(noteTypes, 0);
            }
            removeOtherAvailableNotes(neededValue);
        }
    }

    private void removeOtherAvailableNotes(int neededValue){
        //Iteriert über die Scheine und sortiert sie nach ihrem Wert absteigend
        NoteTypes[] noteValues = NoteTypes.values();
        java.util.Arrays.sort(noteValues, (a, b) -> Integer.compare(b.getValue(), a.getValue()));

        for(NoteTypes note : noteValues){
            int noteValue = note.getValue();
            if(neededValue <= 0) break;

            // Holt die Anzahl der verfügbaren Scheine dieses Typs
            int noteCount = notes.getOrDefault(note, 0);
            // Überprüft, ob Scheine verfügbar sind und ob der Wert des Scheines zur Deckung des benötigten Wertes beitragen kann
            if(noteCount > 0 && noteValue <= neededValue){
                // Bestimmt die maximale Anzahl von Scheinen, die verwendet werden kann ohne den benötigten Wert zu überschreiten oder mehr Scheine zu verwenden, als vorhanden sind
                int maxAmountToUse = Math.min(noteCount, neededValue / noteValue);
                int valueToRemove = maxAmountToUse * noteValue;

                notes.put(note, noteCount - maxAmountToUse);
                neededValue -= valueToRemove;
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
