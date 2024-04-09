package com.example.mankomania.gameboardfields;

import com.example.mankomania.Player;

public class HotelField extends GameboardField{
    int rent;
    int cost;
    Player owner;
    public HotelField(int x, int y, int id, int rent, int cost, Player owner) {
        super(x, y, id);
        this.rent = rent;
        this.cost = cost;
        this.owner = owner;
    }
    public Player getOwner(){
        return this.owner;
    }
    public void setOwner(Player owner){
        this.owner = owner;
    }
}
