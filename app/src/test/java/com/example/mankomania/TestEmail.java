package com.example.mankomania;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Stream;

public class TestEmail {
    @Test
    public void testValidEmail(){
        Assert.assertFalse(MainActivityLogin.isNoValidEmail("l@gmail.com"));
        Assert.assertFalse(MainActivityLogin.isNoValidEmail("ajsdhf@dushlf.jkasdh"));
        Assert.assertFalse(MainActivityLogin.isNoValidEmail("l@edu.aau.at"));
    }
    @Test
    public void testInvalidEmail(){
        Assert.assertTrue(MainActivityLogin.isNoValidEmail("lgmail.com"));
        Assert.assertTrue(MainActivityLogin.isNoValidEmail(""));
        Assert.assertTrue(MainActivityLogin.isNoValidEmail("test"));
    }
}
