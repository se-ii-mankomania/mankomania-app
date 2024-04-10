package com.example.mankomania.LogikTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.mankomania.logik.Color;
import com.example.mankomania.logik.NoteTypes;
import com.example.mankomania.logik.Player;
import com.example.mankomania.logik.StockTypes;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;



public class PlayerTest {
        private Player player1 = new Player("T1", Color.RED);
        private Player player2 = new Player("T2", Color.BLUE);

    @Test
    public void playerInitializationTest() {
        assertEquals("T1", player1.getUsername());
        assertEquals(Color.RED, player1.getColor());
        assertEquals(0, player1.getPosition());
        assertNotNull(player1.getAmountOfStock());
    }
        @Test
        public void testMovement() {
            player1.movement(5);
            assertEquals(5, player1.getPosition());
        }
    @Test
    public void addMoneyToWalletTest() {
        int initialBalance = player1.getWalletBalance();
        player1.addMoneyToWallet(NoteTypes.FIVETHOUSAND, 2);
        int expectedBalance = 2 * NoteTypes.FIVETHOUSAND.getValue() + initialBalance ;
        assertEquals(expectedBalance, player1.getWalletBalance());
    }
    @Test
    public void removeMoneyFromWallet(){
        int initialBalance = player1.getWalletBalance();
        player1.removeMoneyFromWallet(NoteTypes.FIVETHOUSAND, 4);
        int expectedBalance = initialBalance - 4 * NoteTypes.FIVETHOUSAND.getValue();
        assertEquals(expectedBalance, player1.getWalletBalance());
    }
    @Test
    public void addCheatMoneyToWalletTest() {
        int initialBalance = player1.getWalletBalance();
        player1.cheatMoney(NoteTypes.FIVETHOUSAND, 2);
        int expectedBalance = initialBalance - 2 * NoteTypes.FIVETHOUSAND.getValue();
        assertEquals(expectedBalance, player1.getWalletBalance());
    }
    @Test
    public void testPayToPlayer() {

        int initialPayerBalance = player1.getWalletBalance();
        int initialRecipientBalance = player2.getWalletBalance();

        int transferValue = 2 * NoteTypes.FIVETHOUSAND.getValue();

        player1.payToPlayer(player2, NoteTypes.FIVETHOUSAND, 2);

        int expectedPayer1Balance = initialPayerBalance - transferValue;
        int expectedPlayer2Balance = initialRecipientBalance + transferValue;

        assertEquals(expectedPayer1Balance, player1.getWalletBalance());
        assertEquals(expectedPlayer2Balance, player2.getWalletBalance());
    }
    
        @Test
        public void testAddShare() {
            //Aktueller Stand
            int currentStock = player1.getAmountOfStock().getOrDefault(String.valueOf(StockTypes.BRUCHSTAHL_AG), 0);
            // Hinzufügen neuer Aktien
            player1.addShare(StockTypes.BRUCHSTAHL_AG, 5);
            int newTotalStock = player1.getAmountOfStock().getOrDefault(String.valueOf(StockTypes.BRUCHSTAHL_AG), 0);
            assertEquals(currentStock + 5, newTotalStock);
    }

        @Test
        public void testResetAllShares(){
        player1.addShare(StockTypes.BRUCHSTAHL_AG, 1);
        player1.addShare(StockTypes.TROCKENOEL_AG, 1);
        player1.addShare(StockTypes.KURZSCHLUSS_VERSORGUNGS_AG, 1);

        player1.resetAllShares();

        assertTrue(player1.getAmountOfStock().values().stream().allMatch(value -> value == 0));

        }

        @Test
        public void getPosition() {
            player1.movement(3);
            assertEquals(3, player1.getPosition());
        }
}
