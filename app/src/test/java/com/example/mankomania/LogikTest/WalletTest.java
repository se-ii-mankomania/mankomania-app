package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.mankomania.logik.NoteTypes;
import com.example.mankomania.logik.Wallet;

import org.junit.jupiter.api.Test;


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
    public void testRemoveMoneyDecreasesTotalAmount() throws Exception {
        int initialTotal = wallet.totalAmount();
        wallet.removeMoney(NoteTypes.TENTHOUSAND, 2);
        int removedValue = 2 * NoteTypes.TENTHOUSAND.getValue();
        assertEquals(initialTotal - removedValue, wallet.totalAmount());
    }

    @Test
    public void testRemoveMoneyThrowsExceptionIfNotEnoughNotes() {
        Exception exception = assertThrows(Exception.class, () -> wallet.removeMoney(NoteTypes.FIFTYTHOUSAND, 10));
        assertEquals("Nicht genug Scheine", exception.getMessage());
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
