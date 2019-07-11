package com.avtdev.crazyletters.services;

import com.avtdev.crazyletters.models.realm.Dictionary;

import java.util.ArrayList;
import java.util.List;

public class GameRoom {

    private static GameRoom sInstance;

    private List<String> mPlayersId;
    private String mRoomId;

    private List<List<String>> mWordsDone;
    private List<String> mWholeWords;

    private GameRoom(){
        mPlayersId = new ArrayList<>();
    }

    public static GameRoom getInstance(){
        if(sInstance == null){
            sInstance = new GameRoom();
        }
        return sInstance;
    }

    public void startGame(List<Dictionary> listDictionary){

        mPlayersId = new ArrayList<>();
        mPlayersId.add("1");
        mPlayersId.add("2");

        mWordsDone = new ArrayList<>();
        for (String id : mPlayersId) {
            mWordsDone.add(new ArrayList<>());
        }
        mWholeWords = new ArrayList<>();

        for(Dictionary dict : listDictionary){
            if(mWholeWords.contains(dict.getWord())){
                mWholeWords.add(dict.getWord());
            }
        }
    }

    public void finishGame(){
        mPlayersId.clear();

        mWordsDone = null;
        mWholeWords = null;
    }

    public boolean checkPlayerWord(String word){
        for(List<String> listWord : mWordsDone){
            if(listWord.contains(word)){
                return false;
            }
        }
        if(mWholeWords.contains(word)) {
            mWordsDone.get(0).add(word);
            return true;
        }
        return false;
    }

    public int[] checkPuntuations(){
        int[] puntuations = new int[]{0,0};

        for(int i = 0; i < mWordsDone.size(); i++){
            int puntAux = 0;
            for(String word : mWordsDone.get(i)){
                puntAux += word.length();
            }
            if(i == 0){
                puntuations[0] = puntAux;
            }else{
                if(puntuations[1] < puntAux){
                    puntuations[1] = puntAux;
                }
            }
        }
        return puntuations;
    }
}
