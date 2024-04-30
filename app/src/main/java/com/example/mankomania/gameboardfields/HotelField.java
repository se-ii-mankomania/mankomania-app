package com.example.mankomania.gameboardfields;

import com.example.mankomania.logik.Player;

public class HotelField extends GameboardField{
   private int rent;
   private int cost;
   private Player owner;
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
    public int getCost(){
        return this.cost;
    }
    public int getRent(){
        return this.rent;
    }
}
