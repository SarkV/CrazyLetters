package com.avtdev.crazyletters.services;

import android.content.Context;

import com.avtdev.crazyletters.models.realm.Dictionary;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.models.realm.SyncInfo;
import com.avtdev.crazyletters.models.response.DictionaryResponse;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmManager {

    private static RealmManager mInstance;

    Realm mRealm;

    private RealmManager(Context context) {
        Realm.init(context);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
    }

    public static RealmManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new RealmManager(context);
        }
        return mInstance;
    }

    private Realm getRealm(){
        if(mRealm == null || mRealm.isClosed()){
            // Get a Realm instance for this thread
            mRealm = Realm.getDefaultInstance();
        }
        return mRealm;
    }

    public void setSyncInfo(String table, Date updateDate){
        getRealm().executeTransaction((Realm realm) -> {
            realm.copyToRealmOrUpdate(new SyncInfo(table, updateDate));
        });
    }

    public SyncInfo getSyncInfo(String table){
        return getRealm().where(SyncInfo.class).equalTo(SyncInfo.PROPERTIES.TABLE, table).findFirst();
    }

    public void setDictionary(List<DictionaryResponse> response){
        getRealm().executeTransaction((Realm realm) -> {
            List<Dictionary> dictionaries = new ArrayList<>();
            List<Dictionary> dictionariesRemove = new ArrayList<>();
            for(DictionaryResponse dictionaryResponse : response){
                if(Constants.ActionType.DELETED.name().equals(dictionaryResponse.getAction())){
                    dictionariesRemove.add(new Dictionary(dictionaryResponse));
                }else{
                    dictionaries.add(new Dictionary(dictionaryResponse));
                }
            }

            realm.copyToRealmOrUpdate(dictionaries);

        });
    }

    public List<Dictionary> getDictionary(String language){
        return getRealm().where(Dictionary.class).equalTo(Dictionary.PROPERTIES.LANGUAGE, language).findAll();
    }

    public void saveGame(int[] velocity, GameConstants.LettersType[] lettersType, String[] language, int time){
        getRealm().executeTransaction(realm -> {
            Game game = realm.where(Game.class)
                    .equalTo(Game.PROPERTIES.VELOCITY, Utils.listToString(Arrays.asList(velocity)))
                    .and()
                    .equalTo(Game.PROPERTIES.LETTERS_TYPE, Utils.listToString(Arrays.asList(lettersType)))
                    .and()
                    .equalTo(Game.PROPERTIES.LANGUAGES, Utils.listToString(Arrays.asList(language)))
                    .and()
                    .equalTo(Game.PROPERTIES.LETTERS_TYPE, time)
                    .findFirst();

            if(game == null){
                realm.insertOrUpdate(new Game(velocity, lettersType, language, time));
            }else{
                game.setLastUsed(new Date());
                realm.insertOrUpdate(game);
            }

        });
    }

    public void updateGame(Game game){
        getRealm().executeTransaction(realm -> {
            game.setLastUsed(new Date());
            realm.insertOrUpdate(game);
        });
    }

    public Game getGame(Long id){
        if(id == null) return null;
        return getRealm().where(Game.class).equalTo(Game.PROPERTIES.ID, id).findFirst();
    }

    public List<Game> getGames(){
        return getRealm().where(Game.class).findAll();
    }
}
