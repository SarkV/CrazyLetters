package com.avtdev.crazyletters.services;

import android.content.Context;

import com.avtdev.crazyletters.models.realm.Dictionary;
import com.avtdev.crazyletters.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

public class GameRoom {

    private static GameRoom sInstance;

    private List<String> mPlayersId;
    private String mRoomId;

    private List<List<String>> mWordsDone;

    Realm mRealm;
    String mLanguage;
    Boolean mHasAccent;

    private GameRoom(){
        mPlayersId = new ArrayList<>();
    }

    public static GameRoom getInstance(Context context){
        if(sInstance == null){
            sInstance = new GameRoom();
            sInstance.mRealm = RealmManager.getInstance(context).getRealm();
        }
        return sInstance;
    }

    public void setPlayersId(List<String> playersId) {
        this.mPlayersId = playersId;

        mWordsDone = new ArrayList<>();
        for (String id : mPlayersId) {
            mWordsDone.add(new ArrayList<>());
        }
    }

    private boolean checkWord(String word){
        RealmQuery query = mRealm.where(Dictionary.class);
        if(mHasAccent){
            query.equalTo(Dictionary.PROPERTIES.WORD, word);
        }else{
            query.equalTo(Dictionary.PROPERTIES.WORD, word);
        }
        if(mLanguage != null){
            query.equalTo(Dictionary.PROPERTIES.LANGUAGE, mLanguage);
        }

        return query.count() > 0;
    }

    public void setSearchVariables(String language, boolean hasAccent){
        this.mLanguage = language;
        this.mHasAccent = hasAccent;
    }

    public void finishGame(){
        mPlayersId.clear();

        mWordsDone = null;
        mLanguage = null;
        mHasAccent = null;
    }

    public boolean checkPlayerWord(String word){
        for(List<String> listWord : mWordsDone){
            if(listWord.contains(word)){
                return false;
            }
        }
        return checkWord(word);
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
