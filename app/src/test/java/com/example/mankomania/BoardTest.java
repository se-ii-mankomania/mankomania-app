package com.example.mankomania;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {
    FieldsHandler fieldsHandler = new FieldsHandler();

    @Before
    public void setUp(){

        Cellposition [][] cellPositions = new Cellposition[14][14];
        for(int i= 0; i<14; i++){
            for(int j = 0; j<14; j++){
                cellPositions[j][i] = new Cellposition(j, i);
            }
        }
        fieldsHandler.initFields(cellPositions);

    }
    @Test
    public void gameFieldMoveTest(){
        PlayerTest playerTest = new PlayerTest(ColorTest.BLUE, fieldsHandler.fields[0]);
        Assert.assertEquals(fieldsHandler.fields[0].id, playerTest.currentField.id);

        fieldsHandler.movePlayer(playerTest, 5);
        Assert.assertEquals(fieldsHandler.fields[5].id, playerTest.currentField.id);
        Assert.assertNotEquals(fieldsHandler.fields[0].id, playerTest.currentField.id);
    }
    @Test
    public void gameFieldMoveTestStartPosition(){
        PlayerTest playerblue = new PlayerTest(ColorTest.BLUE, fieldsHandler.fields[48]);
        PlayerTest playergreen = new PlayerTest(ColorTest.GREEN, fieldsHandler.fields[49]);
        PlayerTest playerred = new PlayerTest(ColorTest.RED, fieldsHandler.fields[50]);
        PlayerTest playerpurple = new PlayerTest(ColorTest.PURPLE, fieldsHandler.fields[51]);

        fieldsHandler.movePlayer(playerblue, 7);
        fieldsHandler.movePlayer(playergreen, 7);
        fieldsHandler.movePlayer(playerred, 7);
        fieldsHandler.movePlayer(playerpurple, 7);

        Assert.assertEquals(fieldsHandler.fields[6].id, playerblue.currentField.id);
        Assert.assertEquals(fieldsHandler.fields[17].id, playergreen.currentField.id);
        Assert.assertEquals(fieldsHandler.fields[28].id, playerred.currentField.id);
        Assert.assertEquals(fieldsHandler.fields[39].id, playerpurple.currentField.id);
    }
    @Test
    public void gameFieldMoveTestBackToStart(){
        PlayerTest playerTest = new PlayerTest(ColorTest.BLUE, fieldsHandler.fields[40]);

        fieldsHandler.movePlayer(playerTest, 5);
        Assert.assertEquals(fieldsHandler.fields[1].id, playerTest.currentField.id);
    }

    @Test
    public void gameFieldMoveTestFromActionfield(){
        PlayerTest playerblue = new PlayerTest(ColorTest.BLUE, fieldsHandler.fields[44]);
        PlayerTest playergreen = new PlayerTest(ColorTest.GREEN, fieldsHandler.fields[45]);
        PlayerTest playerred = new PlayerTest(ColorTest.RED, fieldsHandler.fields[46]);
        PlayerTest playerpurple = new PlayerTest(ColorTest.PURPLE, fieldsHandler.fields[47]);

        fieldsHandler.movePlayer(playerblue, 5);
        fieldsHandler.movePlayer(playergreen, 5);
        fieldsHandler.movePlayer(playerred, 5);
        fieldsHandler.movePlayer(playerpurple, 5);

        Assert.assertEquals(fieldsHandler.fields[1].id, playerblue.currentField.id);
        Assert.assertEquals(fieldsHandler.fields[18].id, playergreen.currentField.id);
        Assert.assertEquals(fieldsHandler.fields[23].id, playerred.currentField.id);
        Assert.assertEquals(fieldsHandler.fields[40].id, playerpurple.currentField.id);
    }




}
