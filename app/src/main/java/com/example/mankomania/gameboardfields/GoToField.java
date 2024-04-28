package com.example.mankomania.gameboardfields;

public class GoToField extends GameboardField{
    GameboardField targetField;
    public GoToField(int x, int y, int id, GameboardField targetField) {
        super(x, y, id);
        this.targetField = targetField;
    }
}
