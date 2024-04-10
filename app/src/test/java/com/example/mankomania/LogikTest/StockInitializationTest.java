package com.example.mankomania.LogikTest;


import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.mankomania.logik.StockInitializer;

import org.junit.Test;

import java.util.Map;


public class StockInitializationTest {

    private StockInitializer stockinit = new StockInitializer();

    //Testet ob die Methode genau 2 Aktien hinzufügt bzw. keine leere Map
    @Test
    public void testInitializeStocksBasic() {
        Map<String, Integer> stocks = StockInitializer.initializeRandomStocks();
        assertNotNull(stocks);
        int uniqueStocksCount = (int) stocks.values().stream().count();
        assertTrue(uniqueStocksCount == 1 || uniqueStocksCount == 2);
    }
}
