package com.example.mankomania.logik;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StockMarket {
    private Map<StockTypes, Double> sharePrice = new EnumMap<>(StockTypes.class);
    private Random random = new Random();

    public StockMarket(){
        for(StockTypes stocks : StockTypes.values()){
            sharePrice.put(stocks, 100.0 + random.nextDouble() * 100);
        }
    }
    public double getSharePrice(StockTypes stockTypes){
       return sharePrice.get(stockTypes);
    }

    public void refreshSharePrice(){
        for (StockTypes stockTypes : StockTypes.values()){
            double newSharePrice = sharePrice.get(stockTypes) + (random.nextDouble()* 20 - 10);
        }
    }
}
