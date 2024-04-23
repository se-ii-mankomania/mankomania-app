package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.mankomania.logik.NoteTypes;
import com.example.mankomania.logik.Wallet;

import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;


public class WalletTest {
    private Wallet wallet = new Wallet();

    //Stellt sicher, dass das hinzufügen funktioniert und der Gesamtbetrag aktualisiert wird
    @Test
    public void testAddMoneyIncreasesTotalAmount() {
        int initialTotal = wallet.totalAmount();
        wallet.addMoney(NoteTypes.FIVETHOUSAND, 2);
        int addedValue = 2 * NoteTypes.FIVETHOUSAND.getValue();
        assertEquals(initialTotal + addedValue, wallet.totalAmount());
    }

    @Test
    public void testRemoveMoneyDecreasesTotalAmount(){
        int initialTotal = wallet.totalAmount();
        wallet.removeMoney(NoteTypes.TENTHOUSAND, 2);
        int removedValue = 2 * NoteTypes.TENTHOUSAND.getValue();
        assertEquals(initialTotal - removedValue, wallet.totalAmount());
    }

    @Test
    public void testRemoveMoneyWithNegativeAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                wallet.removeMoney(NoteTypes.FIVETHOUSAND, -1)
        );
        assertEquals("Der zu entfernende Betrag muss größer als 0 sein.", exception.getMessage());
    }

    @Test
    public void testRemoveMoneyWithZeroAmount(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> wallet.removeMoney(NoteTypes.FIVETHOUSAND, 0));
        assertEquals("Der zu entfernende Betrag muss größer als 0 sein.", exception.getMessage());
    }
    

    @Test
    public void testTotalAmountCalculatesCorrectly() {
        int expectedTotal = (6 * 5000) + (7 * 10000) + (6 * 50000) + (6 * 100000);
        assertEquals(expectedTotal, wallet.totalAmount());
    }

    @Test
    public void testCheatMoneyDecreasesTotalAmount() throws Exception {
        int initialTotal = wallet.totalAmount();
        wallet.cheatMoney(NoteTypes.HUNDREDTHOUSAND, 1);
        int cheatedValue = NoteTypes.HUNDREDTHOUSAND.getValue();
        assertEquals(initialTotal - cheatedValue, wallet.totalAmount());
    }
}
