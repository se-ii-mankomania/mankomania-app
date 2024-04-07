package com.example.mankomania.LogikTest;

import static org.junit.Assert.assertEquals;

import com.example.mankomania.logik.StockTypes;

import org.junit.Test;

public class StockTypesTest {
    //Testet ob die richtigen Namen zurückgegeben werden
    @Test
    public void testNameOfBRUCHSTAHL_AG(){
        assertEquals("Bruchstahl AG", StockTypes.BRUCHSTAHL_AG.getName());
    }
    @Test
    public void testNameOfTROCKENÖL_AG(){
        assertEquals("Trockenöl AG", StockTypes.TROCKENÖL_AG.getName());
    }
    @Test
    public void testNameOfKURZSCHLUSS_VERSORGUNGS_AG(){
        assertEquals("Kurzschluss-Versorgungs AG", StockTypes.KURZSCHLUSS_VERSORGUNGS_AG.getName());
    }

    //Stellt sicher, dass alle Aktien vorhanden sind
    @Test
    public void testEnumValues(){
        StockTypes[] expectedStockTypes = {StockTypes.BRUCHSTAHL_AG, StockTypes.TROCKENÖL_AG, StockTypes.KURZSCHLUSS_VERSORGUNGS_AG};
        StockTypes[] actualStockTypes = StockTypes.values();
        assertEquals(expectedStockTypes, actualStockTypes);
    }
}
