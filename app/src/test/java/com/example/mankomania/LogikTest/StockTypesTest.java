package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.mankomania.logik.StockTypes;

import org.junit.jupiter.api.Test;

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
    public void testEnumValues(){
        StockTypes[] expectedStockTypes = {StockTypes.BRUCHSTAHL_AG, StockTypes.TROCKENOEL_AG, StockTypes.KURZSCHLUSS_VERSORGUNGS_AG};
        StockTypes[] actualStockTypes = StockTypes.values();
        assertEquals(expectedStockTypes, actualStockTypes);
    }
}
