package com.example.mankomania;

import static com.example.mankomania.PlayerViewModel.isEven;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestPseudoMethod {
    @Test
    public void testIsEven(){
        assertTrue(isEven(8));
        assertTrue(isEven(0));
        assertFalse(isEven(11));
        assertFalse(isEven(15));
    }
}
