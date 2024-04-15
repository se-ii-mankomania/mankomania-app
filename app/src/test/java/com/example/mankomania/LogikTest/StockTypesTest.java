package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.mankomania.logik.StockTypes;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

public class StockTypesTest {
    //Testet ob die richtigen Namen zurückgegeben werden
    @Test
    public void testNameOfBRUCHSTAHL_AG(){
        assertEquals("Bruchstahl AG", StockTypes.BRUCHSTAHL_AG.getName());
    }
    @Test
    public void testNameOfTROCKENOEL_AG(){
        assertEquals("Trockenöl AG", StockTypes.TROCKENOEL_AG.getName());
    }
    @Test
    public void testNameOfKURZSCHLUSS_VERSORGUNGS_AG(){
        assertEquals("Kurzschluss-Versorgungs AG", StockTypes.KURZSCHLUSS_VERSORGUNGS_AG.getName());
    }

    //Stellt sicher, dass alle Aktien vorhanden sind
    @Test
    public void testNumberOfEnumValues(){
        StockTypes[] values=StockTypes.values();
        assertEquals(3, Arrays.stream(values).count());
    }
}
