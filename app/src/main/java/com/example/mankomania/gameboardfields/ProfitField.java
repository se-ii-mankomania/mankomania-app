package com.example.mankomania.gameboardfields;

public class ProfitField extends GameboardField{
   private int profit;

    public ProfitField(int x, int y, int id, int profit) {
        super(x, y, id);
        this.profit = profit;
    }

    public int getProfit(){
        return this.profit;
    }



}
