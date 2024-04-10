package com.example.mankomania;
import com.example.mankomania.logik.Color;
import com.example.mankomania.logik.Player;
import com.example.mankomania.screens.Cellposition;
import com.example.mankomania.screens.FieldsHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {
    FieldsHandler fieldsHandler = new FieldsHandler();

    @Before
    public void setUp(){

        Cellposition[][] cellPositions = new Cellposition[14][14];
        for(int i= 0; i<14; i++){
            for(int j = 0; j<14; j++){
                cellPositions[j][i] = new Cellposition(j, i);
            }
        }
        fieldsHandler.initFields(cellPositions);

    }
    @Test
    public void gameFieldMoveTest(){
        Player playerblue = new Player("BLUE", Color.BLUE);
        playerblue.setCurrentField(fieldsHandler.fields[0]);

        Assert.assertEquals(fieldsHandler.fields[0].id, playerblue.getCurrentField().id);

        fieldsHandler.movePlayer(playerblue, 5);
        Assert.assertEquals(fieldsHandler.fields[5].id, playerblue.getCurrentField().id);
        Assert.assertNotEquals(fieldsHandler.fields[0].id, playerblue.getCurrentField().id);
    }
    @Test
    public void gameFieldMoveTestStartPosition(){
        Player playerblue = new Player("BLUE", Color.BLUE);
        playerblue.setCurrentField(fieldsHandler.fields[48]);
        Player playergreen = new Player("GREEN", Color.GREEN);
        playergreen.setCurrentField(fieldsHandler.fields[49]);
        Player playerred = new Player("RED", Color.RED);
        playerred.setCurrentField(fieldsHandler.fields[50]);
        Player playerpurple = new Player("PURPLE",Color.PURPLE);
        playerpurple.setCurrentField(fieldsHandler.fields[51]);

        fieldsHandler.movePlayer(playerblue, 7);
        fieldsHandler.movePlayer(playergreen, 7);
        fieldsHandler.movePlayer(playerred, 7);
        fieldsHandler.movePlayer(playerpurple, 7);

        Assert.assertEquals(fieldsHandler.fields[6].id, playerblue.getCurrentField().id);
        Assert.assertEquals(fieldsHandler.fields[17].id, playergreen.getCurrentField().id);
        Assert.assertEquals(fieldsHandler.fields[28].id, playerred.getCurrentField().id);
        Assert.assertEquals(fieldsHandler.fields[39].id, playerpurple.getCurrentField().id);
    }
    @Test
    public void gameFieldMoveTestBackToStart(){
        Player playerblue = new Player("BLUE", Color.BLUE);
        playerblue.setCurrentField(fieldsHandler.fields[40]);

        fieldsHandler.movePlayer(playerblue, 5);
        Assert.assertEquals(fieldsHandler.fields[1].id, playerblue.getCurrentField().id);
    }

    @Test
    public void gameFieldMoveTestFromActionfield(){
        Player playerblue = new Player("BLUE", Color.BLUE);
        playerblue.setCurrentField(fieldsHandler.fields[44]);
        Player playergreen = new Player("GREEN", Color.GREEN);
        playergreen.setCurrentField(fieldsHandler.fields[45]);
        Player playerred = new Player("RED", Color.RED);
        playerred.setCurrentField(fieldsHandler.fields[46]);
        Player playerpurple = new Player("PURPLE",Color.PURPLE);
        playerpurple.setCurrentField(fieldsHandler.fields[47]);

        fieldsHandler.movePlayer(playerblue, 5);
        fieldsHandler.movePlayer(playergreen, 5);
        fieldsHandler.movePlayer(playerred, 5);
        fieldsHandler.movePlayer(playerpurple, 5);

        Assert.assertEquals(fieldsHandler.fields[1].id, playerblue.getCurrentField().id);
        Assert.assertEquals(fieldsHandler.fields[18].id, playergreen.getCurrentField().id);
        Assert.assertEquals(fieldsHandler.fields[23].id, playerred.getCurrentField().id);
        Assert.assertEquals(fieldsHandler.fields[40].id, playerpurple.getCurrentField().id);
    }




}
