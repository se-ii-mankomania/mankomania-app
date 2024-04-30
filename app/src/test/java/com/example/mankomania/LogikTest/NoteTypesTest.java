package com.example.mankomania.LogikTest;

import com.example.mankomania.logik.spieler.NoteTypes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


class NoteTypesTest {

    //Prüft ob FIVETHOUSAND den richtigen Wert hat
    @Test
    void testValueOfFIVETHOUSAND(){
        assertEquals(5000, NoteTypes.FIVETHOUSAND.getValue());
    }

    //Prüft ob TENTHOUSAND den richtigen Wert hat
    @Test
    void testValueOfTENTHOUSAND(){
        assertEquals(10000, NoteTypes.TENTHOUSAND.getValue());
    }
    //Prüft ob FIFTYTHOUSAND den richtigen Wert hat
    @Test
    void testValueOfFIFTYTHOUSAND(){
        assertEquals(50000, NoteTypes.FIFTYTHOUSAND.getValue());
    }
    //Prüft ob HUNDREDTHOUSAND den richtigen Wert hat
    @Test
    void testValueOfHUNDREDTHOUSAND(){
        assertEquals(100000, NoteTypes.HUNDREDTHOUSAND.getValue());
    }
}
