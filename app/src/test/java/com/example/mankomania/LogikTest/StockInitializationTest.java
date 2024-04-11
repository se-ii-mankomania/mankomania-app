package com.example.mankomania.LogikTest;


import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

import com.example.mankomania.logik.StockInitializer;

import org.junit.Test;

import java.util.Map;


public class StockInitializationTest {

    //Testet ob die Map nicht Null ist und ob insgesamt 2 Aktien vorhanden sind
    @Test
    public void testThatTheMapIsNotNull() {
        Map<String, Integer> stocks = StockInitializer.initializeRandomStocks();
        assertNotNull(stocks);
        int uniqueStocksCount = (int) stocks.values().stream().count();
        assertTrue(uniqueStocksCount == 1 || uniqueStocksCount == 2);

    }
}
