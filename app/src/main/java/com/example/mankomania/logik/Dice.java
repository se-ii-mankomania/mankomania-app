package com.example.mankomania.logik;

import java.security.SecureRandom;

public class Dice {
    SecureRandom random = new SecureRandom();
    public int[] throwDice(){
        return new int[]{ random.nextInt(6)+1, random.nextInt(6)+1};
    }

    public int[] rollSix(){
        return new int[]{6,6};
    }

}
