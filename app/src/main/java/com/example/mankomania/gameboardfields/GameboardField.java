package com.example.mankomania.gameboardfields;

import java.io.Serializable;

public class GameboardField implements Serializable {
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


}
