package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.mankomania.logik.StockMarket;
import com.example.mankomania.logik.StockTypes;

import org.junit.jupiter.api.Test;


class StockMarketTest {
    private StockMarket stockMarket = new StockMarket();

    //Stellt sicher, dass beim Erstellen eines StockMarket-Objekts alle Aktientypen mit einem Startkurs initialisiert werden, der im definierten Bereich liegt.
    @Test
    void testSharePriceRange(){
        for(StockTypes stocks : StockTypes.values()){
            double price = stockMarket.getSharePrice(stocks);
            assertTrue(price >= 100.0 && price <= 200.0);
        }
    }

    //Prüft ob nach der Aktualisierung der Preis größer 0 ist
    @Test
    void testRefreshSharePriceStaysWithinExpectedRange() {
        stockMarket.refreshSharePrice();
        for (StockTypes stockType : StockTypes.values()) {
            double price = stockMarket.getSharePrice(stockType);
            assertTrue(price > 0);
        }
    }

    //Stellt sicher, dass der Preis sich ändert
    @Test
    void testRefreshSharePriceChangesValue() {
        double originalPrice = stockMarket.getSharePrice(StockTypes.BRUCHSTAHL_AG);
        stockMarket.refreshSharePrice();
        double refreshedPrice = stockMarket.getSharePrice(StockTypes.BRUCHSTAHL_AG);

        assertNotEquals(originalPrice, refreshedPrice);
    }
}
