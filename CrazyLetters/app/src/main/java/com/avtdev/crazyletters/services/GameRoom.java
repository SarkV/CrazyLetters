package com.avtdev.crazyletters.services;

import java.util.ArrayList;
import java.util.List;

public class GameRoom {

    private static GameRoom mInstance;

    private List<String> mPlayersId;
    private String mRoomId;

    private GameRoom(){
        mPlayersId = new ArrayList<>();
    }

    public static GameRoom getInstance(){
        if(mInstance == null){
            mInstance = new GameRoom();
        }
        return mInstance;
    }

    public void startGame(){

    }

    public void finishGame(){
        mPlayersId.clear();
    }
}
