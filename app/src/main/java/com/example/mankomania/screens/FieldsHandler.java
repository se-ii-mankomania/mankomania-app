package com.example.mankomania.screens;

import com.example.mankomania.gameboardfields.ActivityField;
import com.example.mankomania.gameboardfields.GameboardField;
import com.example.mankomania.gameboardfields.GoToField;
import com.example.mankomania.gameboardfields.HotelField;
import com.example.mankomania.gameboardfields.ProfitField;
import com.example.mankomania.logik.spieler.Player;

public class FieldsHandler {
    private GameboardField[] fields = new GameboardField[52];
    public GameboardField getField(int index) {
        if (index < 0 || index >= fields.length) {
            throw new IllegalArgumentException("Field index is out of bounds");
        }
        return fields[index];
    }


    public void movePlayer(Player player, int diceNr) {
        int currentId = player.getCurrentField().getId();

        //links oben Startposition
        if (currentId == 49) {
            moveFromStartTopLeft(player, diceNr);
        }
        //rechts oben Startposition
        else if (currentId == 50) {
            moveFromTopRight(player, diceNr);
        }
        //rechts unten Startposition
        else if (currentId == 51) {
            moveFromBottomRight(player, diceNr);
        }
        //links unten Startposition
        else if (currentId == 52) {
            moveFromBottomLeft(player, diceNr);
        }
        //Pferderennen
        else if (currentId == 46) {
            moveFromHorseRace(player, diceNr);
        }
        //Börse
        else if (currentId == 47) {
            moveFromStockMarket(player, diceNr);
        }
        //Casino
        else if (currentId == 48) {
            moveFromCasino(player, diceNr);
        }
        //Auktionshaus
        else if (currentId == 45) {
            moveFromAuctionHouse(player, diceNr);
        }
        //Bewegung innerhalb des Quadrats
        else if (currentId >= 1 && currentId <= 43) {
            moveWithinBoard(player, diceNr);
        }
        else{
            throw new IndexOutOfBoundsException("Invalid ID");
        }
    }

    private void moveFromStartTopLeft(Player player, int diceNr) {
        player.setCurrentField(fields[diceNr - 1]);
    }

    private void moveFromTopRight(Player player, int diceNr) {
        player.setCurrentField(fields[10 + diceNr]);
    }

    private void moveFromBottomRight(Player player, int diceNr) {
        player.setCurrentField(fields[21 + diceNr]);
    }

    private void moveFromBottomLeft(Player player, int diceNr) {
        if (diceNr > 11) {
            player.setCurrentField(fields[diceNr - 12]);
        } else {
            player.setCurrentField(fields[32 + diceNr]);
        }
    }

    private void moveFromHorseRace(Player player, int diceNr) {
        player.setCurrentField(fields[13 + diceNr]);
    }

    private void moveFromStockMarket(Player player, int diceNr) {
        player.setCurrentField(fields[18 + diceNr]);
    }

    private void moveFromCasino(Player player, int diceNr) {
        if (diceNr > 8) {
            player.setCurrentField(fields[diceNr - 9]);
        } else {
            player.setCurrentField(fields[35 + diceNr]);
        }
    }

    private void moveFromAuctionHouse(Player player, int diceNr) {
        if (diceNr > 3) {
            player.setCurrentField(fields[diceNr - 4]);
        } else {
            player.setCurrentField(fields[40 + diceNr]);
        }
    }

    private void moveWithinBoard(Player player, int diceNr) {
        int newPosition = player.getCurrentField().getId() + diceNr;
        if (newPosition > 43) {
            int newID = newPosition - 45;
            player.setCurrentField(fields[newID]);
        } else {
            player.setCurrentField(fields[newPosition - 1]);
        }
    }

    public void initFields(Cellposition[][] cellPositions) {
        if(cellPositions.length != 14 || cellPositions[0].length != 14){
            throw new IllegalArgumentException("wrong size");
        }

        fields[44] = new ActivityField( cellPositions[4][2].getX(), cellPositions[4][2].getY(), 45, "Auktionshaus");
        fields[45] = new ActivityField( cellPositions[4][11].getX(), cellPositions[4][11].getY(), 46, "Pferderennen");
        fields[46] = new ActivityField( cellPositions[9][11].getX(), cellPositions[9][11].getY(), 47, "Börse" );
        fields[47] = new ActivityField( cellPositions[9][2].getX(), cellPositions[9][2].getY(), 48, "Casino");
        fields[24] = new ActivityField( cellPositions[12][10].getX(), cellPositions[12][10].getY(), 25, "Lotterie");
        fields[6] = new ActivityField( cellPositions[1][7].getX(), cellPositions[1][7].getY(), 7, "Böse 1");

        fields[0] = new GoToField( cellPositions[1][1].getX(), cellPositions[1][1].getY(), 1, fields[44]);
        fields[1] = new GoToField( cellPositions[1][2].getX(), cellPositions[1][2].getY(), 2, fields[45]);
        fields[2] = new ProfitField( cellPositions[1][3].getX(), cellPositions[1][3].getY(), 3, 10000);
        fields[3] = new ProfitField( cellPositions[1][4].getX(), cellPositions[1][4].getY(), 4, 5000);
        fields[4] = new GoToField( cellPositions[1][5].getX(), cellPositions[1][5].getY(), 5, fields[46]);
        fields[5] = new ProfitField( cellPositions[1][6].getX(), cellPositions[1][6].getY(), 6, 7000);
        fields[7] = new ProfitField( cellPositions[1][8].getX(), cellPositions[1][8].getY(), 8, 20000);
        fields[8] = new GoToField( cellPositions[1][9].getX(), cellPositions[1][9].getY(), 9, fields[47]);
        fields[9] = new HotelField( cellPositions[1][10].getX(), cellPositions[1][10].getY(), 10, 10000, 70000, null);
        fields[10] = new ProfitField( cellPositions[1][11].getX(), cellPositions[1][11].getY(), 11, 10000);
        fields[11] = new GoToField( cellPositions[1][12].getX(), cellPositions[1][12].getY(), 12, fields[6]);

        fields[12] = new ProfitField( cellPositions[2][12].getX(), cellPositions[2][12].getY(), 13, 8000);
        fields[13] = new GoToField( cellPositions[3][12].getX(), cellPositions[3][12].getY(), 14, fields[45]);
        fields[14] = new ProfitField( cellPositions[4][12].getX(), cellPositions[4][12].getY(), 15, 7000);
        fields[15] = new ProfitField( cellPositions[5][12].getX(), cellPositions[5][12].getY(), 16, 15000);
        fields[16] = new GoToField( cellPositions[6][12].getX(), cellPositions[6][12].getY(), 17, fields[46]);
        fields[17] = new HotelField( cellPositions[7][12].getX(), cellPositions[7][12].getY(), 18, 13000, 100000, null);
        fields[18] = new GoToField( cellPositions[8][12].getX(), cellPositions[8][12].getY(), 19, fields[47]);
        fields[19] = new ProfitField( cellPositions[9][12].getX(), cellPositions[9][12].getY(), 20, 20000);
        fields[20] = new ProfitField( cellPositions[10][12].getX(), cellPositions[10][12].getY(), 21, 15000);
        fields[21] = new ProfitField( cellPositions[11][12].getX(), cellPositions[11][12].getY(), 22, 10000); //starter3
        fields[22] = new GoToField( cellPositions[12][12].getX(), cellPositions[12][12].getY(), 23, fields[44]);

        fields[23] = new ProfitField( cellPositions[12][11].getX(), cellPositions[12][11].getY(), 24, 11000);
        fields[25] = new ProfitField( cellPositions[12][9].getX(), cellPositions[12][9].getY(), 26, 13000);
        fields[26] = new ProfitField( cellPositions[12][8].getX(), cellPositions[12][8].getY(), 27, 20000);
        fields[27] = new HotelField( cellPositions[12][7].getX(), cellPositions[12][7].getY(), 28, 20000, 120000, null);
        fields[28] = new ProfitField(cellPositions[12][6].getX(), cellPositions[12][6].getY(), 29, 10000);
        fields[29] = new ProfitField( cellPositions[12][5].getX(), cellPositions[12][5].getY(), 30, 15000);
        fields[30] = new ProfitField( cellPositions[12][4].getX(), cellPositions[12][4].getY(), 31, 20000);
        fields[31] = new GoToField( cellPositions[12][3].getX(), cellPositions[12][3].getY(), 32, fields[45]);
        fields[32] = new ProfitField( cellPositions[12][2].getX(), cellPositions[12][2].getY(), 33, 10000); //starter4
        fields[33] = new GoToField( cellPositions[12][1].getX(), cellPositions[12][1].getY(), 34, fields[46]);

        fields[34] = new ProfitField( cellPositions[11][1].getX(), cellPositions[11][1].getY(), 35, 5000);
        fields[35] = new ProfitField( cellPositions[10][1].getX(), cellPositions[10][1].getY(), 36, 30000);
        fields[36] = new GoToField( cellPositions[9][1].getX(), cellPositions[9][1].getY(), 37, fields[24]);
        fields[37] = new ProfitField( cellPositions[8][1].getX(), cellPositions[8][1].getY(), 38, 5000);
        fields[38] = new ProfitField( cellPositions[7][1].getX(), cellPositions[7][1].getY(), 39, 12000);
        fields[39] = new ProfitField( cellPositions[6][1].getX(), cellPositions[6][1].getY(), 40, 10000);
        fields[40] = new HotelField( cellPositions[5][1].getX(), cellPositions[5][1].getY(), 41, 5000, 40000, null);
        fields[41] = new ProfitField( cellPositions[4][1].getX(), cellPositions[4][1].getY(), 42, 6000);
        fields[42] = new GoToField( cellPositions[3][1].getX(), cellPositions[3][1].getY(), 43, fields[44]);
        fields[43] = new ProfitField( cellPositions[2][1].getX(), cellPositions[2][1].getY(), 44, 18000);


        fields[48] = new GameboardField( cellPositions[0][0].getX(), cellPositions[0][0].getY(), 49);
        fields[49] = new GameboardField( cellPositions[0][13].getX(), cellPositions[0][13].getY(), 50);
        fields[50] = new GameboardField( cellPositions[13][13].getX(), cellPositions[13][13].getY(), 51);
        fields[51] = new GameboardField( cellPositions[13][0].getX(), cellPositions[13][0].getY(), 52);


    }
}
