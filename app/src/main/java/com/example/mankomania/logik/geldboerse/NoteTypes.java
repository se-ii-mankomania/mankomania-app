package com.example.mankomania.logik.geldboerse;

public enum NoteTypes {
    FIVETHOUSAND(5000),TENTHOUSAND(10000), FIFTYTHOUSAND(50000), HUNDREDTHOUSAND(100000);

    private final int value;

    NoteTypes(int values){
        this.value = values;
    }

    public int getValue(){
        return this.value;
    }
}
