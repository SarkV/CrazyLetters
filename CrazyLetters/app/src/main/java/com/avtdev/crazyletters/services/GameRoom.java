package com.avtdev.crazyletters.services;

import java.util.ArrayList;
import java.util.List;

public class GameRoom {

    private static GameRoom sInstance;

    private List<String> mPlayersId;
    private String mRoomId;
    private boolean mSinglePlayer;

    private GameRoom(){
        mPlayersId = new ArrayList<>();
    }

    public static GameRoom getInstance(){
        if(sInstance == null){
            sInstance = new GameRoom();
        }
        return sInstance;
    }

    public void initializeGame(boolean singlePlayer){
        mSinglePlayer = singlePlayer;
    }

    public boolean isSinglePlayer() {
        return mSinglePlayer;
    }

    public void startGame(){

    }

    public void finishGame(){
        mPlayersId.clear();
    }
}
