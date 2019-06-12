package com.avtdev.crazyletters.services;

import android.content.Context;

import com.avtdev.crazyletters.models.realm.Dictionary;
import com.avtdev.crazyletters.models.realm.SyncInfo;
import com.avtdev.crazyletters.models.response.DictionaryResponse;
import com.avtdev.crazyletters.utils.Constants;

import java.util.ArrayList;
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
}
