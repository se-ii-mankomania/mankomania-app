package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.mankomania.logik.aktien.StockTypes;

import org.junit.jupiter.api.Test;


class StockTypesTest {
    //Testet ob die richtigen Namen zurückgegeben werden
    @Test
    void testNameOfBRUCHSTAHL_AG(){
        assertEquals("Bruchstahl AG", StockTypes.BRUCHSTAHL_AG.getName());
    }
    @Test
    void testNameOfTROCKENOEL_AG(){
        assertEquals("Trockenöl AG", StockTypes.TROCKENOEL_AG.getName());
    }
    @Test
    void testNameOfKURZSCHLUSS_VERSORGUNGS_AG(){
        assertEquals("Kurzschluss-Versorgungs AG", StockTypes.KURZSCHLUSS_VERSORGUNGS_AG.getName());
    }
}
