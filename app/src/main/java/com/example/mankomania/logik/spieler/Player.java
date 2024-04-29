package com.example.mankomania.logik.spieler;

import com.example.mankomania.gameboardfields.GameboardField;
import com.example.mankomania.logik.stocks.StockInitializer;
import com.example.mankomania.logik.stocks.StockTypes;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Player {

    private GameboardField currentField;
    Logger logger = Logger.getLogger(getClass().getName());
    private int id;
    private String username;
    private Color color;
    private Wallet wallet;
    private Map<String, Integer> stocks ;
    private int position;
    private SecureRandom random = new SecureRandom();


    public Player(String username, Color color) {
        this.id = random.nextInt(10000);
        this.username = username;
        this.color = color;
        this.wallet = new Wallet();
        this.stocks = StockInitializer.initializeRandomStocks();
        this.position = 0;
    }

    public GameboardField getCurrentField() {
        return currentField;
    }

    public void setCurrentField(GameboardField currentField) {
        this.currentField = currentField;
    }

    public void movement(int fields){
        this.position += fields;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Color getColor() {
        return color;
    }

    //Auslagern in einer späteren Phase
    public void payToPlayer(Player player, int amount){
            this.wallet.removeMoney(amount);
            player.wallet.addMoney(amount);
    }
    public void addMoneyToWallet(int amount) {
            this.wallet.addMoney(amount);
    }


    public void removeMoneyFromWallet(int amount) {
            this.wallet.removeMoney(amount);
    }

    public int getWalletBalance() {
        return this.wallet.totalAmount();
    }
    public void addShare(StockTypes stockTypes, int amount){
        stocks.merge(String.valueOf(stockTypes), amount, Integer::sum);
    }
    public Map<String, Integer> getAmountOfStock(){
        return new HashMap<>(stocks);
    }
    public void resetAllShares(){
        for(StockTypes stockTypes : StockTypes.values()){
            stocks.put(String.valueOf(stockTypes), 0);
        }
    }

    public int getPosition() {
        return position;
    }
}
