package com.example.mankomania;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.mankomania.screens.PlayerViewModel;

import org.junit.jupiter.api.Test;

public class PseudoTestClass {
    @Test
    public void testIsEvenTrue(){
        assertTrue(PlayerViewModel.isEven(8));
    }

    @Test
    public void testIsEvenFalse(){
        assertFalse(PlayerViewModel.isEven(3));
    }
}
