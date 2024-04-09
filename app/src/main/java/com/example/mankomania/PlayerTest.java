package com.example.mankomania;

import com.example.mankomania.gameboardfields.GameboardField;

public class PlayerTest {


    GameboardField currentField;
    int profit;

    ColorTest color;
    public PlayerTest(ColorTest color, GameboardField currentField){
        this.color = color;
        this.currentField = currentField;
    }
    public void setGameboardField(GameboardField gameboardField){
        this.currentField = gameboardField;
    }
    public void setProfit(int profit){
        this.profit = profit;
    }
}
