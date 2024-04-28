package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.mankomania.logik.NoteTypes;
import com.example.mankomania.logik.Wallet;

import org.junit.jupiter.api.Test;



public class WalletTest {
    private Wallet wallet = new Wallet();

    //Stellt sicher, dass das hinzufügen funktioniert und der Gesamtbetrag aktualisiert wird
    @Test
    void testAddMoneyIncreasesTotalAmount() {
        int initialTotal = wallet.totalAmount();
        int addedMoney = 5000;
        wallet.addMoney(addedMoney);
        assertEquals(initialTotal + addedMoney, wallet.totalAmount());
    }

    @Test
    void testAddMoneyWithZeroAmount(){
        int addMoney = 0;
        Exception e = assertThrows(IllegalArgumentException.class, () -> wallet.addMoney(0));
        assertEquals("Der hinzuzufügende Betrag muss größer als 0 sein.", e.getMessage());
    }
    //Methode addMoney fügt immer die größtmöglichen Scheine als erstes hinzu
    @Test
    void testAddMoneyAddsCorrectNotetype(){
            wallet.addMoney(80000);
            assertEquals(6, wallet.getNoteCount(NoteTypes.HUNDREDTHOUSAND));
            assertEquals(7, wallet.getNoteCount(NoteTypes.FIFTYTHOUSAND));
            assertEquals(10, wallet.getNoteCount(NoteTypes.TENTHOUSAND));
            assertEquals(6, wallet.getNoteCount(NoteTypes.FIVETHOUSAND));
    }

    @Test
    void testRemoveMoneyDecreasesTotalAmount(){
        int initialTotal = wallet.totalAmount();
        int amountToRemove = 5000;
        wallet.removeMoney(amountToRemove);
        assertEquals(initialTotal - amountToRemove, wallet.totalAmount());
    }

    @Test
    void testRemoveMoneyWithNegativeAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                wallet.removeMoney(-5000)
        );
        assertEquals("Der zu entfernende Betrag muss größer als 0 sein.", exception.getMessage());
    }

    @Test
    void testRemoveMoneyWithZeroAmount(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> wallet.removeMoney(0));
        assertEquals("Der zu entfernende Betrag muss größer als 0 sein.", exception.getMessage());
    }

    @Test
    void testRemoveMoneyRemovesNotetypes(){
        wallet.removeMoney(5000);
        assertEquals(5, wallet.getNoteCount(NoteTypes.FIVETHOUSAND));
    }

    @Test
    void testIsEmpty(){
        wallet.removeMoney(1000000);
        assertTrue(wallet.isEmpty());
    }

    @Test
    void testTotalAmountCalculatesCorrectly() {
        int expectedTotal = (6 * 5000) + (7 * 10000) + (6 * 50000) + (6 * 100000);
        assertEquals(expectedTotal, wallet.totalAmount());
    }

//    @Test
//    void testCheatMoneyDecreasesTotalAmount() throws Exception {
//        int initialTotal = wallet.totalAmount();
//        wallet.cheatMoney(NoteTypes.HUNDREDTHOUSAND, 1);
//        int cheatedValue = NoteTypes.HUNDREDTHOUSAND.getValue();
//        assertEquals(initialTotal - cheatedValue, wallet.totalAmount());
//    }
}
