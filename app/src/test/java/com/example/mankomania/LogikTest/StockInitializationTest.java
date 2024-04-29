package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.mankomania.logik.StockInitializer;

import org.junit.jupiter.api.Test;

import java.util.Map;


public class StockInitializationTest {

    //Testet ob die Map nicht Null ist und ob insgesamt 2 Aktien vorhanden sind
    @Test
    void testThatTheMapIsNotNull() {
        Map<String, Integer> stocks = StockInitializer.initializeRandomStocks();
        assertNotNull(stocks);
        int uniqueStocksCount = (int) stocks.values().stream().count();
        assertTrue(uniqueStocksCount == 1 || uniqueStocksCount == 2);

    }
}
