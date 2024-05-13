package com.example.mankomania;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.mankomania.gameboardfields.GameboardField;
import com.example.mankomania.gameboardfields.HotelField;
import com.example.mankomania.gameboardfields.ProfitField;
import com.example.mankomania.logik.Color;
import com.example.mankomania.logik.Player;
import com.example.mankomania.screens.Cellposition;
import com.example.mankomania.screens.FieldsHandler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


 class BoardTest {
    FieldsHandler fieldsHandler = new FieldsHandler();

    @BeforeEach
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
     void gameFieldMoveTest(){
        Player playerblue = new Player("BLUE", Color.BLUE);
        playerblue.setCurrentField(fieldsHandler.getField(0));

        assertEquals(fieldsHandler.getField(0).getId(), playerblue.getCurrentField().getId());

        fieldsHandler.movePlayer(playerblue, 5);
        assertEquals(fieldsHandler.getField(5).getId(), playerblue.getCurrentField().getId());
        assertNotEquals(fieldsHandler.getField(0).getId(), playerblue.getCurrentField().getId());
    }
    @Test
     void gameFieldMoveTestFromStartPosition(){
        Player playerblue = new Player("BLUE", Color.BLUE);
        playerblue.setCurrentField(fieldsHandler.getField(48));
        Player playergreen = new Player("GREEN", Color.GREEN);
        playergreen.setCurrentField(fieldsHandler.getField(49));
        Player playerred = new Player("RED", Color.RED);
        playerred.setCurrentField(fieldsHandler.getField(50));
        Player playerpurple = new Player("PURPLE",Color.PURPLE);
        playerpurple.setCurrentField(fieldsHandler.getField(51));

        fieldsHandler.movePlayer(playerblue, 7);
        fieldsHandler.movePlayer(playergreen, 7);
        fieldsHandler.movePlayer(playerred, 7);
        fieldsHandler.movePlayer(playerpurple, 7);

        assertEquals(fieldsHandler.getField(6).getId(), playerblue.getCurrentField().getId());
        assertEquals(fieldsHandler.getField(17).getId(), playergreen.getCurrentField().getId());
        assertEquals(fieldsHandler.getField(28).getId(), playerred.getCurrentField().getId());
        assertEquals(fieldsHandler.getField(39).getId(), playerpurple.getCurrentField().getId());
    }
    @Test
     void gameFieldMoveToNewRound(){
        Player playerblue = new Player("BLUE", Color.BLUE);
        playerblue.setCurrentField(fieldsHandler.getField(40));

        fieldsHandler.movePlayer(playerblue, 5);
        assertEquals(fieldsHandler.getField(1).getId(), playerblue.getCurrentField().getId());
    }

    @Test
     void gameFieldMoveTestStartAtActionfield(){
        Player playerblue = new Player("BLUE", Color.BLUE);
        playerblue.setCurrentField(fieldsHandler.getField(44));
        Player playergreen = new Player("GREEN", Color.GREEN);
        playergreen.setCurrentField(fieldsHandler.getField(45));
        Player playerred = new Player("RED", Color.RED);
        playerred.setCurrentField(fieldsHandler.getField(46));
        Player playerpurple = new Player("PURPLE",Color.PURPLE);
        playerpurple.setCurrentField(fieldsHandler.getField(47));

        fieldsHandler.movePlayer(playerblue, 5);
        fieldsHandler.movePlayer(playergreen, 5);
        fieldsHandler.movePlayer(playerred, 5);
        fieldsHandler.movePlayer(playerpurple, 5);

        assertEquals(fieldsHandler.getField(1).getId(), playerblue.getCurrentField().getId());
        assertEquals(fieldsHandler.getField(18).getId(), playergreen.getCurrentField().getId());
        assertEquals(fieldsHandler.getField(23).getId(), playerred.getCurrentField().getId());
        assertEquals(fieldsHandler.getField(40).getId(), playerpurple.getCurrentField().getId());
    }
    @Test
     void caseStartLeftBottomMoveBy12(){
        Player playerpurple = new Player("PURPLE",Color.PURPLE);
        playerpurple.setCurrentField(fieldsHandler.getField(51));

        fieldsHandler.movePlayer(playerpurple, 12);

        assertEquals(fieldsHandler.getField(0).getId(), playerpurple.getCurrentField().getId());
    }
    @Test
     void caseStartAtCasinoMoveByMoreThan8(){
        Player playerpurple = new Player("PURPLE",Color.PURPLE);
        playerpurple.setCurrentField(fieldsHandler.getField(47));

        fieldsHandler.movePlayer(playerpurple, 9);

        assertEquals(fieldsHandler.getField(0).getId(), playerpurple.getCurrentField().getId());
    }

    @Test
     void caseStartAtAuctionHouseMoveByLessThan4(){
        Player playerpurple = new Player("PURPLE",Color.PURPLE);
        playerpurple.setCurrentField(fieldsHandler.getField(44));

        fieldsHandler.movePlayer(playerpurple, 3);

        assertEquals(fieldsHandler.getField(43).getId(), playerpurple.getCurrentField().getId());
    }
    @Test
    void testMoveWithinGameBoardFieldsInvalidID(){
       Player playerpurple = new Player("PURPLE",Color.PURPLE);
       playerpurple.setCurrentField(new GameboardField(1,2, -1));

       IndexOutOfBoundsException indexOutOfBoundsException = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {fieldsHandler.movePlayer(playerpurple, 3);});
       assertEquals("Invalid ID", indexOutOfBoundsException.getMessage());
       playerpurple.setCurrentField(new GameboardField(1,2, 53));
      IndexOutOfBoundsException indexOutOfBoundsException1 =  Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {fieldsHandler.movePlayer(playerpurple, 3);});
      assertEquals("Invalid ID", indexOutOfBoundsException1.getMessage());
    }
    @Test void testWrongSizeIniFields(){
       FieldsHandler fieldsHandler1 = new FieldsHandler();
       Assertions.assertThrows(IllegalArgumentException.class, () -> {fieldsHandler1.initFields(new Cellposition[1][14]);});
       Assertions.assertThrows(IllegalArgumentException.class, () -> {fieldsHandler1.initFields(new Cellposition[14][1]);});
       IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {fieldsHandler1.initFields(new Cellposition[1][1]);});
       Assertions.assertThrows(IllegalArgumentException.class, () -> {fieldsHandler1.initFields(new Cellposition[15][15]);});
       assertEquals("wrong size", illegalArgumentException.getMessage());
    }

    @Test
    void testHotelfield(){
       HotelField hotel = new HotelField(1,2, 3,2000, 20000, null);
       Player player = new Player("Blue", Color.BLUE);
       assertNull(hotel.getOwner());
       hotel.setOwner(player);
       assertEquals( 1, hotel.getX());
       assertEquals(2, hotel.getY());
       assertEquals(3, hotel.getId() );
       assertEquals(2000, hotel.getRent());
       assertEquals(20000, hotel.getCost());
       assertEquals(player, hotel.getOwner());
    }
    @Test
    void testProfitField(){
       ProfitField profitField = new ProfitField(1,2,3, 3000);
       assertEquals(3000, profitField.getProfit());
       assertEquals( 1, profitField.getX());
       assertEquals( 2, profitField.getY());
       assertEquals(3,profitField.getId());
    }
    @Test void testFieldsHandlerInvalidID(){
       IllegalArgumentException exceptionTooBig = assertThrows(IllegalArgumentException.class, () -> {fieldsHandler.getField(53);});
       IllegalArgumentException exceptionTooSmall = assertThrows(IllegalArgumentException.class, () -> {fieldsHandler.getField(-1);});
       assertEquals("Field index is out of bounds", exceptionTooSmall.getMessage() );
       assertEquals("Field index is out of bounds", exceptionTooBig.getMessage());


    }




}
