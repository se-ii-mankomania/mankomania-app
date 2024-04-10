package com.example.mankomania.gameboardfields;

public class GoToField extends GameboardField{
    GameboardField goToField;
    public GoToField(int x, int y, int id, GameboardField goToField) {
        super(x, y, id);
        this.goToField = goToField;
    }
}
