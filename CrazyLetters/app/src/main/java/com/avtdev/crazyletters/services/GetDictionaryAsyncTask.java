package com.avtdev.crazyletters.services;

import android.os.AsyncTask;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.activities.SplashActivity;
import com.avtdev.crazyletters.listeners.ISplashProgressBar;
import com.avtdev.crazyletters.models.response.DictionaryResponse;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Logger;
import com.avtdev.crazyletters.utils.Utils;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetDictionaryAsyncTask extends AsyncTask<List<DictionaryResponse>, Integer, Boolean>  {

    private static final String TAG = "GetDictionaryAsyncTask";

    ISplashProgressBar mListener;
    Long mUpdateTime;



    public GetDictionaryAsyncTask(ISplashProgressBar listener, Long updateTime){
        mListener = (ISplashProgressBar) listener;
        mUpdateTime = updateTime;
    }

    @Override
    protected Boolean doInBackground(List<DictionaryResponse>... params) {
        Logger.d(TAG, "getOfflineDictionary");
        Gson gson = new Gson();
        Long lastSincroDictionary = Utils.getLongSharedPreferences((SplashActivity) mListener, Constants.Preferences.LAST_SYNC_DICTIONARY.name(), 0L);
        List<DictionaryResponse> dictionaryList = new ArrayList<>();

        try {
            if(params == null || params.length == 0){
                InputStream inputStream = ((SplashActivity) mListener).getResources().openRawResource(R.raw.total_words);

                String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
                DictionaryResponse[] listDictionaryResponse = gson.fromJson(jsonString, DictionaryResponse[].class);

                for (DictionaryResponse dictionaryResponse : listDictionaryResponse) {
                    if (dictionaryResponse.getCreatedAt() >= lastSincroDictionary) {
                        dictionaryList.add(dictionaryResponse);
                    }
                }
            }else{
                dictionaryList.addAll(params[0]);
            }

            if (dictionaryList.size() > 0) {
                mListener.setTotalProgress(dictionaryList.size());

                RealmManager.getInstance((SplashActivity) mListener).setDictionary(dictionaryList,  mListener);
            }
            return true;
        } catch (Exception ex) {
            Logger.e(TAG, "getOfflineDictionary", ex);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean o) {
        super.onPostExecute(o);
        mListener.login();

        if(mUpdateTime != null){
            Utils.setSharedPreferences(((SplashActivity) mListener), Constants.Preferences.LAST_SYNC_DICTIONARY.name(), mUpdateTime);
        }
    }
}
