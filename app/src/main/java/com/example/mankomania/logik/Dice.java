package com.example.mankomania.logik;

public class Dice {
    public int[] throwDice(){
        return new int[]{(int) (Math.random()*6)+1, (int) (Math.random()*6)+1};
    }

    public int[] rollSix(){
        return new int[]{6,6};
    }

}
