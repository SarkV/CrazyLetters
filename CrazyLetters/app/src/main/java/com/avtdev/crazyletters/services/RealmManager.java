package com.avtdev.crazyletters.services;

import android.content.Context;

import com.avtdev.crazyletters.BuildConfig;
import com.avtdev.crazyletters.models.realm.Dictionary;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.models.realm.Language;
import com.avtdev.crazyletters.models.realm.LetterFrequency;
import com.avtdev.crazyletters.models.realm.SyncInfo;
import com.avtdev.crazyletters.models.response.DictionaryResponse;
import com.avtdev.crazyletters.models.response.GameModeResponse;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Logger;
import com.avtdev.crazyletters.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmManager {

    private static final String TAG = "RealmManager";

    private static RealmManager mInstance;

    Realm mRealm;

    private RealmManager(Context context) {
        Realm.init(context);


        RealmConfiguration config;
        if( BuildConfig.PRO){
           config = new RealmConfiguration.Builder()
                   .build();
        } else{
            config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .initialData(new LetterFrequencyTransaction())
                    .build();
        }

        Realm.setDefaultConfiguration(config);
    }

    public static RealmManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new RealmManager(context);
        }
        return mInstance;
    }

    public Realm getRealm(){
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

    public int getDictionaryMax(String language){
        if(language == null){
            return getRealm()
                    .where(Dictionary.class)
                    .sort(Dictionary.PROPERTIES.WORD_LENGTH, Sort.DESCENDING)
                    .findFirst()
                    .getWordLength();
        }else{
            return getRealm()
                    .where(Dictionary.class)
                    .equalTo(Dictionary.PROPERTIES.LANGUAGE, language)
                    .sort(Dictionary.PROPERTIES.WORD_LENGTH, Sort.DESCENDING)
                    .findFirst()
                    .getWordLength();
        }
    }

    public void saveDefaultGames(List<GameModeResponse> listGames){
        getRealm().executeTransaction(realm -> {

            if(listGames != null && !listGames.isEmpty()){
                for(GameModeResponse response : listGames){
                    Game game = realm.where(Game.class)
                            .equalTo(Game.PROPERTIES.NAME, response.getName())
                            .and()
                            .equalTo(Game.PROPERTIES.DEFAULT_GAME, true)
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
                        game.setVelocity(response.getVelocity().toArray(new Integer[0]));
                        game.setLettersType(response.getLettersType().toArray(new String[0]));
                        game.setLanguages(response.getLanguages().toArray(new String[0]));
                        game.setAccent(response.getAccent());
                        game.setTime(response.getTime());
                        game.setLastUsed(new Date());
                        realm.insertOrUpdate(game);
                    }
                }
            }
        });
    }

    public void removeGame(long id){
        getRealm().executeTransaction(realm -> {
            Game game = realm.where(Game.class).equalTo(Game.PROPERTIES.ID, id).findFirst();
            if(game != null){
                game.deleteFromRealm();
            }
        });
    }

    public Game updateGame(Game game, boolean modifyLastUsed){

        Realm realm = getRealm();
        try{
            realm.beginTransaction();

            if(modifyLastUsed){
                game.setLastUsed(new Date());
            }
            realm.insertOrUpdate(game);
            realm.commitTransaction();

            return game;
        }catch (Exception ex){
            Logger.e(TAG, "updateGame", ex);
            realm.cancelTransaction();
        }
        return null;
    }

    public Game saveGame(Game game, boolean modifyLastUsed){
        return saveGame(game.getName(),
                game.getVelocity(),
                game.getLettersType(),
                game.getLanguages(),
                game.hasAccent(),
                game.getTime(),
                modifyLastUsed);
    }

    public Game saveGame(String name, Integer[] velocity, GameConstants.LettersType[] lettersType, String[] language, boolean accent, int time, boolean modifyLastUsed){

        Game game = null;
        Realm realm = getRealm();
        try{

            realm.beginTransaction();
            if(Utils.isNull(name)){
                game = realm.where(Game.class)
                        .equalTo(Game.PROPERTIES.VELOCITY, Utils.listToString(Arrays.asList(velocity)))

                        .equalTo(Game.PROPERTIES.LETTERS_TYPE, Utils.listToString(Arrays.asList(lettersType)))
                        .equalTo(Game.PROPERTIES.LANGUAGES, Utils.listToString(Arrays.asList(language)))
                        .equalTo(Game.PROPERTIES.TIME, time)
                        .findFirst();
            }

            if(game == null){
                game = new Game(name, velocity, lettersType, language, accent, time, false);
            }
            if(modifyLastUsed){
                game.setLastUsed(new Date());
            }
            realm.insertOrUpdate(game);
        realm.commitTransaction();

        return game;
        }catch (Exception ex){
            Logger.e(TAG, "saveGame", ex);
            realm.cancelTransaction();
        }
        return null;
    }

    public Game getLastGame(){
        return getRealm().where(Game.class).sort(Game.PROPERTIES.LAST_USED, Sort.DESCENDING).findFirst();
    }

    public Game getGame(Long id){
        if(id == null) return null;
        Realm realm = getRealm();
        Game game = realm.where(Game.class).equalTo(Game.PROPERTIES.ID, id).findFirst();
        return game != null ? realm.copyFromRealm(game) : null;
    }

    public List<Game> getGames(){
        Realm realm = getRealm();
        return realm.copyFromRealm(realm.where(Game.class)
                .isNotNull(Game.PROPERTIES.NAME)
                .isNotEmpty(Game.PROPERTIES.NAME)
                .sort(Game.PROPERTIES.LAST_USED, Sort.DESCENDING)
                .findAll());
    }

    public List<LetterFrequency> getLettersFrequency(String language){
        Realm realm = getRealm();
        RealmQuery query = realm.where(LetterFrequency.class);
        boolean first = true;
        if(language == null){
            List<Language> languageList = getLanguages();
            for(Language lan: languageList){
                if(!Utils.isNull(lan.getLanguage())){
                    if(!first)
                        query = query.or();
                    query = query.equalTo(LetterFrequency.PROPERTIES.LANGUAGE, lan.getLanguage());
                    first = false;

                }
            }
        }else{
            String[] languageList = language.split(Constants.ARRAY_SEPARATOR);
            for(String lan: languageList){
                if(!first)
                    query = query.or();
                query = query.equalTo(LetterFrequency.PROPERTIES.LANGUAGE, lan);
                first = false;
            }
        }
        return realm.copyFromRealm(query.findAll());
    }
}
