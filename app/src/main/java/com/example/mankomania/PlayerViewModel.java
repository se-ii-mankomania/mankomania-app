package com.example.mankomania;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlayerViewModel extends ViewModel {
    //TODO auf Klasse Player anpassen
    /*private MutableLiveData<Player> currentPlayer=new MutableLiveData<Player>();

    public LiveData<Player> getCurrentPlayer(){
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Player player){
        this.currentPlayer.setValue(player);
    }*/

    //TODO PseudoMehtode um Coverage zu testen

    public static boolean isEven(int i){
        return i%2==0;
    }


}
