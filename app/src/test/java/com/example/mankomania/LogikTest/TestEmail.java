package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.mankomania.screens.MainActivityLogin;

import org.junit.jupiter.api.Test;


class TestEmail {
    @Test
    void testValidEmail(){
        assertFalse(MainActivityLogin.isNoValidEmail("l@gmail.com"));
        assertFalse(MainActivityLogin.isNoValidEmail("ajsdhf@dushlf.jkasdh"));
        assertFalse(MainActivityLogin.isNoValidEmail("l@edu.aau.at"));
    }
    @Test
    void testInvalidEmail(){
        assertTrue(MainActivityLogin.isNoValidEmail("lgmail.com"));
        assertTrue(MainActivityLogin.isNoValidEmail(""));
        assertTrue(MainActivityLogin.isNoValidEmail("test"));
    }
}
