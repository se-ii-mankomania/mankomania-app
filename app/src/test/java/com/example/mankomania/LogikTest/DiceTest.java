package com.example.mankomania.LogikTest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.mankomania.logik.spieler.Dice;

import org.junit.jupiter.api.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
class DiceTest {
    private Dice dice = new Dice();

    @Test
    void testDiceRange() {
        for(int i = 0; i < 5; i++){
            int[] result = dice.throwDice();
            assertTrue(result[0] >= 1 && result[0] <= 6 );
            assertTrue(result[1] >=1 && result[1] <= 6);
        }
    }
    @Test
    void testRollSix(){
        assertArrayEquals(new int[]{6, 6}, dice.rollSix());
    }
}