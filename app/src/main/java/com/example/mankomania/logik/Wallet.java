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

    public void removeMoney(NoteTypes note, int amount){
        int currentAmmount = notes.getOrDefault(note, 0);
        if(currentAmmount >= amount){
            notes.put(note, currentAmmount - amount);
        }else {
            throw new IllegalArgumentException("Nicht genug Scheine");
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
