package com.avtdev.crazyletters.services;

import android.content.Context;

import com.avtdev.crazyletters.models.realm.Dictionary;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.models.realm.Language;
import com.avtdev.crazyletters.models.realm.SyncInfo;
import com.avtdev.crazyletters.models.response.DictionaryResponse;
import com.avtdev.crazyletters.models.response.GameModeResponse;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

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
            if(response != null && !response.isEmpty()){
                List<Dictionary> dictionaries = new ArrayList<>();
                List<Dictionary> dictionariesRemove = new ArrayList<>();
                for(DictionaryResponse dictionaryResponse : response){
                    if(Utils.isNull(dictionaryResponse.getWord())){
                        dictionariesRemove.add(new Dictionary(dictionaryResponse));
                    }else{
                        dictionaries.add(new Dictionary(dictionaryResponse));
                    }
                }

                realm.copyToRealmOrUpdate(dictionaries);

                updateLanguages(realm);
            }
        });
    }

    private void updateLanguages(Realm realm){
        long globalWords = realm.where(Dictionary.class)
                .isNull(Dictionary.PROPERTIES.LANGUAGE)
                .or()
                .isEmpty(Dictionary.PROPERTIES.LANGUAGE)
                .count();

        RealmResults<Dictionary> dictionaries = realm.where(Dictionary.class)
                .isNotNull(Dictionary.PROPERTIES.LANGUAGE)
                .distinct(Dictionary.PROPERTIES.LANGUAGE).findAll();


        List<Language> languages = new ArrayList<>();
        for(Dictionary dic : dictionaries){
            Language lan = new Language(dic.getLanguage());
            lan.setOcurrences(realm.where(Dictionary.class).equalTo(Dictionary.PROPERTIES.LANGUAGE, dic.getLanguage()).count() + globalWords);
            languages.add(lan);
        }

        languages.add(new Language("", realm.where(Dictionary.class).count()));

        realm.insertOrUpdate(languages);
    }

    public List<Language> getLanguages(){
        Realm realm = getRealm();
        return realm.copyFromRealm(realm.where(Language.class).findAll());
    }

    public List<Dictionary> getDictionary(String language){
        return getRealm().where(Dictionary.class).equalTo(Dictionary.PROPERTIES.LANGUAGE, language).findAll();
    }

    public void saveCustomGames(List<GameModeResponse> listGames){
        getRealm().executeTransaction(realm -> {

            if(listGames != null && !listGames.isEmpty()){
                for(GameModeResponse response : listGames){
                    Game game = realm.where(Game.class)
                            .equalTo(Game.PROPERTIES.NAME, response.getName())
                            .and()
                            .equalTo(Game.PROPERTIES.CUSTOM, true)
                            .findFirst();

                    if(game == null && (response.isRemoved() == null || !response.isRemoved())){
                        realm.insertOrUpdate(new Game(
                                response.getName(),
                                response.getVelocity().toArray(new Integer[0]),
                                response.getLettersType().toArray(new String[0]),
                                response.getLanguages().toArray(new String[0]),
                                response.getAccent(),
                                response.getTime(),
                                true));
                    }else if(game != null && response.isRemoved() != null && response.isRemoved()){
                        game.deleteFromRealm();
                    }else if(game != null){
                        game.setLastUsed(new Date());
                        realm.insertOrUpdate(game);
                    }
                }
            }
        });
    }

    public void saveGame(Game game){
        saveGame(game.getName(),
                game.getVelocity(),
                game.getLettersType(),
                game.getLanguages(),
                game.hasAccent(),
                game.getTime());
    }

    public void removeGame(long id){
        Game game = getRealm().where(Game.class).equalTo(Game.PROPERTIES.ID, id).findFirst();
        if(game != null){
            game.deleteFromRealm();
        }
    }

    public void saveGame(String name, Integer[] velocity, GameConstants.LettersType[] lettersType, String[] language, boolean accent, int time){
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
                game = new Game(name, velocity, lettersType, language, accent, time, false);
            }else{
                game.setLastUsed(new Date());
            }
            realm.insertOrUpdate(game);

        });
    }

    public void updateGame(Game game){
        getRealm().executeTransaction(realm -> {
            game.setLastUsed(new Date());
            realm.insertOrUpdate(game);
        });
    }

    public Game getLastGame(){
        return getRealm().where(Game.class).sort(Game.PROPERTIES.LAST_USED, Sort.DESCENDING).findFirst();
    }

    public Game getGame(Long id){
        if(id == null) return null;
        return getRealm().where(Game.class).equalTo(Game.PROPERTIES.ID, id).findFirst();
    }

    public List<Game> getGames(){
        Realm realm = getRealm();
        return realm.copyFromRealm(realm.where(Game.class)
                .isNotNull(Game.PROPERTIES.NAME)
                .sort(Game.PROPERTIES.LAST_USED, Sort.DESCENDING)
                .findAll());
    }
}
