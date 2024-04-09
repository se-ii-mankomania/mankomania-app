package com.example.mankomania.gameboardfields;

import com.example.mankomania.PlayerTest;

public class HotelField extends GameboardField{
    int rent;
    int cost;
    PlayerTest owner;
    public HotelField(int x, int y, int id, int rent, int cost, PlayerTest owner) {
        super(x, y, id);
        this.rent = rent;
        this.cost = cost;
        this.owner = owner;
    }
    public PlayerTest getOwner(){
        return this.owner;
    }
    public void setOwner(PlayerTest owner){
        this.owner = owner;
    }
}
