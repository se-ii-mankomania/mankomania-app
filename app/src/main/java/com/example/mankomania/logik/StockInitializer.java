package com.example.mankomania.logik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StockInitializer {
    private static final Random random = new Random();

    public static Map<String, Integer> initializeRandomStocks() {
            Map<String, Integer> stocks = new HashMap<>();
            StockTypes[] stockTypes = StockTypes.values();

            // Zwei zufällige Aktien auswählen, Duplikate sind erlaubt
            for (int i = 0; i < 2; i++) {
                int index = random.nextInt(stockTypes.length);
                StockTypes selectedStockType = stockTypes[index];
                //Bifunktion welche Prüft ob der Schlüssel vorhanden ist (in diesem Fall unsere Aktie) und fügt 1 hinzu
                stocks.merge(String.valueOf(selectedStockType), 1, Integer::sum);
            }

            return stocks;
        }
}

