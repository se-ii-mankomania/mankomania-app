package com.example.mankomania.LogikTest;

import static org.junit.Assert.assertEquals;

import com.example.mankomania.logik.NoteTypes;

import org.junit.Test;

import java.util.Arrays;

public class NoteTypesTest {

    //Prüft ob FIVETHOUSAND den richtigen Wert hat
    @Test
    public void testValueOfFIVETHOUSAND(){
        assertEquals(5000, NoteTypes.FIVETHOUSAND.getValue());
    }

    //Prüft ob TENTHOUSAND den richtigen Wert hat
    @Test
    public void testValueOfTENTHOUSAND(){
        assertEquals(10000, NoteTypes.TENTHOUSAND.getValue());
    }
    //Prüft ob FIFTYTHOUSAND den richtigen Wert hat
    @Test
    public void testValueOfFIFTYTHOUSAND(){
        assertEquals(50000, NoteTypes.FIFTYTHOUSAND.getValue());
    }
    //Prüft ob HUNDREDTHOUSAND den richtigen Wert hat
    @Test
    public void testValueOfHUNDREDTHOUSAND(){
        assertEquals(100000, NoteTypes.HUNDREDTHOUSAND.getValue());
    }

    //Stellt sicher, dass alle erwarteten Enums vorhanden sind
    @Test
    public void testEnumValues(){
        NoteTypes[] expectedValues = {NoteTypes.FIVETHOUSAND, NoteTypes.TENTHOUSAND, NoteTypes.FIFTYTHOUSAND, NoteTypes.HUNDREDTHOUSAND};
        NoteTypes[] actualValues = NoteTypes.values();
        assertEquals(expectedValues, actualValues);
    }
}
