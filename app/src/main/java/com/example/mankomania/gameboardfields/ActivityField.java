package com.example.mankomania.gameboardfields;

public class ActivityField extends GameboardField{
    String name;
    public ActivityField(int x, int y, int id, String name) {
        super(x, y, id);
        this.name = name;
    }
}
