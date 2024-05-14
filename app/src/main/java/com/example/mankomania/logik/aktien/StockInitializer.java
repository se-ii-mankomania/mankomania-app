package com.example.mankomania.logik.aktien;


import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class StockInitializer {

    private StockInitializer() {
    }

    private static final SecureRandom random = new SecureRandom();

    public static Map<String, Integer> initializeRandomStocks() {
            Map<String, Integer> stocks = new HashMap<>();
            StockTypes[] stockTypes = StockTypes.values();

            // Zwei zufällige Aktien auswählen, Duplikate sind erlaubt
            for (int i = 0; i < 2; i++) {
                int index = random.nextInt(stockTypes.length);
                StockTypes selectedStockType = stockTypes[index];
                //Bifunktion welche Prüft ob der Schlüssel vorhanden ist (in diesem Fall unsere Aktie) und fügt 1 hinzu
                stocks.merge(selectedStockType.toString(), 1, Integer::sum);
            }
            return stocks;
        }
}

