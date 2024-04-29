package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.mankomania.logik.spieler.Color;
import com.example.mankomania.logik.spieler.Player;
import com.example.mankomania.logik.stocks.StockTypes;

import org.junit.jupiter.api.Test;



public class PlayerTest {
        private Player player1 = new Player("T1", Color.RED);
        private Player player2 = new Player("T2", Color.BLUE);



    @Test
    void playerInitializationTest() {
        assertEquals("T1", player1.getUsername());
        assertEquals(Color.RED, player1.getColor());
        assertEquals(0, player1.getPosition());
        assertNotNull(player1.getAmountOfStock());
    }
        @Test
        void testMovement() {
            player1.movement(5);
            assertEquals(5, player1.getPosition());
            player1.movement(-3);
            assertEquals(2, player1.getPosition());
        }
    @Test
    void addMoneyToWalletTest() {
       int initialTotal = player1.getWalletBalance();
       int amountToAdd = 10000;
       player1.addMoneyToWallet(amountToAdd);
       assertEquals(initialTotal + amountToAdd, player1.getWalletBalance());
    }

    @Test
    void removeMoneyFromWallet(){
        int initialTotal = player1.getWalletBalance();
        int amountToRemove = 10000;
        player1.removeMoneyFromWallet(amountToRemove);
        assertEquals(initialTotal - amountToRemove, player1.getWalletBalance());
    }

    @Test
    void testPayToPlayer() {
        int payAmount = 5000;
        int initialTotalPlayer1 = player1.getWalletBalance();
        int initialTotalPlayer2 = player2.getWalletBalance();
        player1.payToPlayer(player2, payAmount);
        assertEquals(initialTotalPlayer1 - payAmount, player1.getWalletBalance());
        assertEquals(initialTotalPlayer2 + payAmount, player2.getWalletBalance());

    }

        @Test
        void testAddShare() {
            //Aktueller Stand
            int currentStock = player1.getAmountOfStock().getOrDefault(String.valueOf(StockTypes.BRUCHSTAHL_AG), 0);
            // Hinzufügen neuer Aktien
            player1.addShare(StockTypes.BRUCHSTAHL_AG, 5);
            int newTotalStock = player1.getAmountOfStock().getOrDefault(String.valueOf(StockTypes.BRUCHSTAHL_AG), 0);
            assertEquals(currentStock + 5, newTotalStock);
    }

        @Test
        void testResetAllShares(){
        player1.addShare(StockTypes.BRUCHSTAHL_AG, 1);
        player1.addShare(StockTypes.TROCKENOEL_AG, 1);
        player1.addShare(StockTypes.KURZSCHLUSS_VERSORGUNGS_AG, 1);

        player1.resetAllShares();

        assertTrue(player1.getAmountOfStock().values().stream().allMatch(value -> value == 0));

        }

        @Test
        void getPosition() {
            player1.movement(3);
            assertEquals(3, player1.getPosition());
        }
}
