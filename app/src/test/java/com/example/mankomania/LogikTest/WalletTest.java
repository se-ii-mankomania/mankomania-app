package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.mankomania.logik.geldboerse.NoteTypes;
import com.example.mankomania.logik.geldboerse.Wallet;

import org.junit.jupiter.api.Test;



class WalletTest {
    private Wallet wallet = new Wallet();

    @Test
    void testStartingCount(){
        assertEquals(0, wallet.getNoteCount(NoteTypes.FIVETHOUSAND));
        assertEquals(0, wallet.getNoteCount(NoteTypes.TENTHOUSAND));
        assertEquals(0, wallet.getNoteCount(NoteTypes.FIFTYTHOUSAND));
        assertEquals(0, wallet.getNoteCount(NoteTypes.HUNDREDTHOUSAND));
    }

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
        Exception e = assertThrows(IllegalArgumentException.class, () -> wallet.addMoney(addMoney));
        assertEquals("Der hinzuzufügende Betrag muss größer als 0 sein.", e.getMessage());
    }
    //Methode addMoney fügt immer die größtmöglichen Scheine als erstes hinzu
    @Test
    void testAddMoneyAddsCorrectNotetype(){
            wallet.addMoney(80000);
            assertEquals(0, wallet.getNoteCount(NoteTypes.HUNDREDTHOUSAND));
            assertEquals(1, wallet.getNoteCount(NoteTypes.FIFTYTHOUSAND));
            assertEquals(3, wallet.getNoteCount(NoteTypes.TENTHOUSAND));
            assertEquals(0, wallet.getNoteCount(NoteTypes.FIVETHOUSAND));
    }

    @Test
    void testRemoveMoneyDecreasesTotalAmount(){
        int amountToBeAdded=30000;
        wallet.addMoney(amountToBeAdded);
        int amountToRemove = 20000;
        wallet.removeMoney(amountToRemove);
        assertEquals(amountToBeAdded - amountToRemove, wallet.totalAmount());
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
    void testRemoveMoreMoneyThanYouHave(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> wallet.removeMoney(5000));
        assertEquals("Der zu entfernende Betrag übersteigt das verfügabre Guthaben", exception.getMessage());

    }
    @Test
    void testWithoutChange(){
        wallet.addMoney(50000);
        assertEquals(1, wallet.getNoteCount(NoteTypes.FIFTYTHOUSAND));
        wallet.removeMoney(50000);
        assertEquals(0, wallet.getNoteCount(NoteTypes.FIFTYTHOUSAND));
        assertTrue(wallet.isEmpty());
    }

    @Test
    void testRemoveMoneyWithChange(){
        wallet.addMoney(50000);
        assertEquals(1, wallet.getNoteCount(NoteTypes.FIFTYTHOUSAND));
        wallet.removeMoney(30000);
        assertEquals(2, wallet.getNoteCount(NoteTypes.TENTHOUSAND));
    }

    @Test
    void testRemoveMoneyRemovesNotetypes(){
        wallet.addMoney(30000);
        wallet.removeMoney(20000);
        assertEquals(1, wallet.getNoteCount(NoteTypes.TENTHOUSAND));
    }

    @Test
    void testWithChange(){
        wallet.addMoney(50000);
        assertEquals(1, wallet.getNoteCount(NoteTypes.FIFTYTHOUSAND));
        wallet.removeMoney(30000);
        assertEquals(2, wallet.getNoteCount(NoteTypes.TENTHOUSAND));
    }

    @Test
    public void testRemoveMoneyUsingSmallerNotes() {
        wallet.addMoney(20000);
        wallet.addMoney(45000);
        assertEquals(6, wallet.getNoteCount(NoteTypes.TENTHOUSAND));
        assertEquals(1, wallet.getNoteCount(NoteTypes.FIVETHOUSAND));
        wallet.removeMoney(15000);
        assertEquals(5, wallet.getNoteCount(NoteTypes.TENTHOUSAND));
        assertEquals(0, wallet.getNoteCount(NoteTypes.FIVETHOUSAND));
    }

    @Test
    void testIsEmpty(){
        wallet.addMoney(100000);
        assertEquals(1, wallet.getNoteCount(NoteTypes.HUNDREDTHOUSAND));
        wallet.removeMoney(100000);
        assertEquals(0, wallet.getNoteCount(NoteTypes.HUNDREDTHOUSAND));
        assertTrue(wallet.isEmpty());
    }

    @Test
    void testTotalAmountCalculatesCorrectly() {
        wallet.addMoney(75000);
        int expectedTotal = 5000 + (2 * 10000) + 50000;
        assertEquals(expectedTotal, wallet.totalAmount());
    }

}
