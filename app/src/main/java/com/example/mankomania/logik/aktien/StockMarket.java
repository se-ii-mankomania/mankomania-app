package com.example.mankomania.logik.aktien;


import java.security.SecureRandom;
import java.util.EnumMap;
import java.util.Map;

public class StockMarket {
    SecureRandom random = new SecureRandom();
    private Map<StockTypes, Double> sharePrice = new EnumMap<>(StockTypes.class);

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
            sharePrice.put(stockTypes, newSharePrice);
        }
    }
}
