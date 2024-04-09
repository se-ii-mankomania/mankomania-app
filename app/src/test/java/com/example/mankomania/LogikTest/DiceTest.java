package com.example.mankomania.LogikTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

import com.example.mankomania.logik.Dice;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DiceTest {
    private Dice dice = new Dice();

    @Test
    public void testDiceRange() {
        int[] result = dice.throwDice();
        for(int i = 0; i < 5; i++){
            assertTrue(result[0] >= 1 && result[0] <= 6 );
            assertTrue(result[1] >=1 && result[1] <= 6);
        }
    }
    @Test
    public void testRollSix(){
        assertArrayEquals(new int[]{6, 6}, dice.rollSix());
    }
}