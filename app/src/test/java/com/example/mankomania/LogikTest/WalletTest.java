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
    void testAddMoneyIncreasesTotalAmount() {
        int initialTotal = wallet.totalAmount();
        wallet.addMoney(NoteTypes.FIVETHOUSAND, 2);
        int addedValue = 2 * NoteTypes.FIVETHOUSAND.getValue();
        assertEquals(initialTotal + addedValue, wallet.totalAmount());
    }

    @Test
    void testRemoveMoneyDecreasesTotalAmount(){
        int initialTotal = wallet.totalAmount();
        wallet.removeMoney(NoteTypes.TENTHOUSAND, 2);
        int removedValue = 2 * NoteTypes.TENTHOUSAND.getValue();
        assertEquals(initialTotal - removedValue, wallet.totalAmount());
    }

    @Test
    void testRemoveMoneyWithNegativeAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                wallet.removeMoney(NoteTypes.FIVETHOUSAND, -1)
        );
        assertEquals("Der zu entfernende Betrag muss größer als 0 sein.", exception.getMessage());
    }

    @Test
    void testRemoveMoneyWithZeroAmount(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> wallet.removeMoney(NoteTypes.FIVETHOUSAND, 0));
        assertEquals("Der zu entfernende Betrag muss größer als 0 sein.", exception.getMessage());
    }

    @Test
    void testRemoveMoneyWithSufficientNotes(){
        wallet.removeMoney(NoteTypes.HUNDREDTHOUSAND, 2);
        assertEquals(4, wallet.getNoteCount(NoteTypes.HUNDREDTHOUSAND));
    }
    //Prüft ob die removeMoney Methode funktioniert auch wenn man nicht genug Scheine von einem Typ hat jedoch genug Geld in der Börse
    //um diesen Betrag zu begleichen. Es wird der nächst kleinere Scheintyp hierführ herangezogen
    @Test
    void testRemoveMoneyWithInsufficientNotesButSufficientTotal() {
        wallet.removeMoney(NoteTypes.HUNDREDTHOUSAND, 7);
        assertEquals(300000, wallet.totalAmount());
        assertEquals(0, wallet.getNoteCount(NoteTypes.HUNDREDTHOUSAND));
        assertEquals(4, wallet.getNoteCount(NoteTypes.FIFTYTHOUSAND));
    }

    @Test
    void testRemoveMoneyWithInsufficientTotal(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->
                wallet.removeMoney(NoteTypes.HUNDREDTHOUSAND, 11));
        assertEquals("Sie haben gewonnen!", exception.getMessage());
    }


    @Test
    void testTotalAmountCalculatesCorrectly() {
        int expectedTotal = (6 * 5000) + (7 * 10000) + (6 * 50000) + (6 * 100000);
        assertEquals(expectedTotal, wallet.totalAmount());
    }

    @Test
    void testCheatMoneyDecreasesTotalAmount() throws Exception {
        int initialTotal = wallet.totalAmount();
        wallet.cheatMoney(NoteTypes.HUNDREDTHOUSAND, 1);
        int cheatedValue = NoteTypes.HUNDREDTHOUSAND.getValue();
        assertEquals(initialTotal - cheatedValue, wallet.totalAmount());
    }
}
