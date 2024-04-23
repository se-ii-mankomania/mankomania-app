package com.example.mankomania.gameboardfields;

public class GameboardField {
    private int x;
    private int y;

    private int id;
    public GameboardField(int x, int y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
    }
    // Getter
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    // Setter
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
