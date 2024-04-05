package com.example.mankomania.logik;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Player {
    private int id;
    private String username;
    private Color color;
    private Wallet wallet;
    private Map<String, Integer> stocks ;
    private int position;
    private SecureRandom random = new SecureRandom;

    public Player(String username, Color color) {
        this.id = random.nextInt(10000);
        this.username = username;
        this.color = color;
        this.wallet = new Wallet();
        this.stocks = new HashMap<>();
        this.position = 0;
        initializationOfStocks();
    }

    public void initializationOfStocks(){
        stocks.put(String.valueOf(StockTypes.BRUCHSTAHL_AG), 0);
        stocks.put(String.valueOf(StockTypes.TROCKENÖL_AG), 0);
        stocks.put(String.valueOf(StockTypes.KURZSCHLUSS_VERSORGUNGS_AG), 0);
    }
    public void movement(int fields){
        this.position += fields;
    }
    //Auslagern in einer späteren Phase
    public void payToPlayer(Player player, NoteTypes note, int amount){
        try {
            this.wallet.removeMoney(note, amount);
            player.wallet.addMoney(note, amount);
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void addMoneyToWallet(NoteTypes note, int amount) {
        this.wallet.addMoney(note, amount);
    }

    public void cheatMoney(NoteTypes note, int amount){
        try {
            this.wallet.cheatMoney(note, amount);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void removeMoneyFromWallet(NoteTypes note, int amount) {
        try{
            this.wallet.removeMoney(note, amount);
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public int getWalletBalance() {
        return this.wallet.totalAmount();
    }
    public void addShare(StockTypes stockTypes, int amount){
        stocks.merge(String.valueOf(stockTypes), amount, Integer::sum);
    }
    public HashMap<String, Integer> getAmountOfStock(){
        return new HashMap<String, Integer>(stocks);
    }
    public void resetAllShares(){
        for(StockTypes stockTypes : StockTypes.values()){
            stocks.put(String.valueOf(stockTypes), 0);
        }
    }
}
