package com.example.mankomania;

import com.example.mankomania.gameboardfields.GameboardField;

public class Player {


    GameboardField currentField;
    int profit;

    Color color;
    public Player(Color color, GameboardField currentField){
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
