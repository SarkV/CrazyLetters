package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.avtdev.crazyletters.BuildConfig;
import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.listeners.ISplashProgressBar;
import com.avtdev.crazyletters.models.response.DictionaryResponse;
import com.avtdev.crazyletters.models.response.GameModeResponse;
import com.avtdev.crazyletters.services.ConstantGS;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Logger;
import com.avtdev.crazyletters.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SplashActivity extends BaseActivity implements ISplashProgressBar {
    private static String TAG = "SplashActivity";

    int numberOfSync = Constants.Firebase.NUMSINCRO;

    TextView mProgressBarText;
    ProgressBar mProgressbar;

    int mProgress = 0;
    int mTotalProgress = 4;

    long mBuildSincro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        mProgressbar = findViewById(R.id.progressBar);
        mProgressBarText = findViewById(R.id.progressBarText);

        mProgressbar.setMax(mTotalProgress);

        mProgress = 0;
        mTotalProgress = 2;
        setProgress();

        Long lastSincroDictionary = Utils.getLongSharedPreferences(SplashActivity.this, Constants.Preferences.LAST_SYNC_DICTIONARY.name(), 0L);
        Long lastSincroGameModes = Utils.getLongSharedPreferences(SplashActivity.this, Constants.Preferences.LAST_SYNC_GAME_MODES.name(), 0L);

        mBuildSincro = Utils.getUTCDate(BuildConfig.LAST_BUILD);
        if(!BuildConfig.SYNCRO){
            if(mBuildSincro > lastSincroDictionary){
                new Handler().postDelayed(() -> getOfflineDictionary( true), 1000);
            }else{
                getDataDictionary(lastSincroDictionary);
            }
            if(mBuildSincro > lastSincroGameModes){
                new Handler().postDelayed(() -> getOfflineGames(true), 1000);
            }else{
                getDataGames(lastSincroGameModes);
            }
        }else{
            login();
            login();
        }
    }

    @Override
    public void addProgress() {
        mProgress++;
        setProgress();
    }

    public void setProgress() {
        mProgressbar.setMax(mTotalProgress);
        mProgressbar.setProgress(0);
        mProgressbar.setProgress(mProgress);
        mProgressBarText.setText(mProgress + " / " + mTotalProgress);
    }

    private void setTotalProgress(long totalProgress){
        mTotalProgress = Math.round(totalProgress / 100) + 3;
        setProgress();
    }

    private void getDataDictionary(Long lastSyncro){
        Logger.d(TAG, "getDataDictionary");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dictionary = database.getReference(Constants.Firebase.DICTIONARY);

        Long currentTime = Utils.getUTCDate();

        dictionary.orderByChild(Constants.Firebase.CREATEDAT)
                .startAt(lastSyncro)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Logger.d(TAG, "dictionary_onDataChange", dataSnapshot.getChildrenCount());

                mProgress = 0;
                setTotalProgress(dataSnapshot.getChildrenCount());

                try{

                    List<DictionaryResponse> listResponse = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        DictionaryResponse response = data.getValue(DictionaryResponse.class);
                        if(response != null){
                            response.setId(data.getKey());
                            listResponse.add(response);
                        }
                    }

                    RealmManager.getInstance(SplashActivity.this).setDictionary(listResponse, SplashActivity.this);

                    Utils.setSharedPreferences(SplashActivity.this, Constants.Preferences.LAST_SYNC_DICTIONARY.name(), currentTime);

                }catch (Exception e){
                    Logger.e(TAG, "dictionary_onDataChange", e.getMessage());
                }

                addProgress();
                login();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Logger.e(TAG, "dictionary_onCancelled", databaseError.getCode(), databaseError.getMessage());
                login();
            }
        });
    }

    private void getDataGames(Long lastSyncro){
        Logger.d(TAG, "getDataGames");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference gameMode = database.getReference(Constants.Firebase.GAMEMODE);

        Long currentTime = Utils.getUTCDate();
        gameMode.orderByChild(Constants.Firebase.CREATEDAT)
                .startAt(lastSyncro)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Logger.d(TAG, "gameMode_onDataChange", dataSnapshot.getChildrenCount());
                try{

                    List<GameModeResponse> listResponse = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        GameModeResponse response = data.getValue(GameModeResponse.class);
                        if(response == null){
                            throw new Exception("Null response in GameModeResponse: " + data.toString());
                        }
                        response.setId(data.getKey());
                        listResponse.add(response);
                    }
                    if(listResponse.size() > 0)
                        RealmManager.getInstance(SplashActivity.this).saveDefaultGames(listResponse);

                    Utils.setSharedPreferences(SplashActivity.this, Constants.Preferences.LAST_SYNC_GAME_MODES.name(), currentTime);

                    addProgress();
                }catch (Exception e){
                    Logger.e(TAG, "gameMode_onDataChange", e.getMessage());
                }

                addProgress();
                login();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Logger.e(TAG, "gameMode_onCancelled", databaseError.getCode(), databaseError.getMessage());
                login();
            }
        });
    }

    private void getOfflineDictionary(boolean updateTime){
        Logger.d(TAG, "getOfflineDictionary");
        Gson gson = new Gson();
        Long lastSincroDictionary = Utils.getLongSharedPreferences(SplashActivity.this, Constants.Preferences.LAST_SYNC_DICTIONARY.name(), 0L);

        InputStream inputStream = null;
        try{
            inputStream = getResources().openRawResource(R.raw.total_words);

            String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
            DictionaryResponse[] listDictionaryResponse = gson.fromJson(jsonString, DictionaryResponse[].class);
            List<DictionaryResponse> dictionaryList = new ArrayList<>();

            for(DictionaryResponse dictionaryResponse : listDictionaryResponse){
                if(dictionaryResponse.getCreatedAt() >= lastSincroDictionary){
                    dictionaryList.add(dictionaryResponse);
                }
            }

            if(dictionaryList.size() > 0){
                setTotalProgress(dictionaryList.size());

                RealmManager.getInstance(this).setDictionary(dictionaryList, SplashActivity.this);

            }

            if(updateTime){
                Utils.setSharedPreferences(this, Constants.Preferences.LAST_SYNC_DICTIONARY.name(), mBuildSincro);
            }
        }catch (Exception ex){
            Logger.e(TAG, "getOfflineDictionary", ex);
        }finally {
            try{
                if(inputStream != null)
                    inputStream.close();
            }catch (Exception ex){}

            addProgress();
            login();
        }
    }

    private void getOfflineGames(boolean updateTime){
        Logger.w(TAG, "getOfflineGames");
        Gson gson = new Gson();
        InputStream inputStream = null;
        try{
            inputStream = getResources().openRawResource(R.raw.games_mode);

            String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
            GameModeResponse[] gameModeResponses = gson.fromJson(jsonString, GameModeResponse[].class);

            List<GameModeResponse> gameModeList = new ArrayList<>(Arrays.asList(gameModeResponses));

            RealmManager.getInstance(this).saveDefaultGames(gameModeList);

            if(updateTime){
                Utils.setSharedPreferences(this, Constants.Preferences.LAST_SYNC_DICTIONARY.name(), mBuildSincro);
            }
        }catch (Exception ex){
            Logger.e(TAG, "getOfflineGames", ex);
        }finally {
            try{
                if(inputStream != null)
                    inputStream.close();
            }catch (Exception ex){}

            addProgress();
            login();
        }
    }

    private void login(){
        numberOfSync--;
        if(numberOfSync == 0){
            long countDictionary = RealmManager.getInstance(this).getDictionaryCount();
            long countGames = RealmManager.getInstance(this).getDefaultGamesCount();
            if(countDictionary == 0 || countGames == 0){
                if(countDictionary == 0){
                    numberOfSync++;
                    mTotalProgress++;
                    setProgress();
                    getOfflineDictionary(false);
                }
                if(countGames == 0){
                    numberOfSync++;
                    mTotalProgress++;
                    setProgress();
                    getOfflineGames(false);
                }
            }else{
                if(BuildConfig.GOOGLE_SERVICE) {
                    GoogleService.getInstance(this).signInSilently((Constants.SignInStatus status) -> {
                        if (status != null && status.equals(Constants.SignInStatus.OK)) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        } else {
                            GoogleService.getInstance(this).startSignInIntent();
                        }
                    });
                }else{
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantGS.REQUEST_CODE.SIGN_IN && resultCode == RESULT_OK) {
            GoogleService.getInstance(this).checkSignIn(data, (Constants.SignInStatus status) -> {
                if(status != null && status.equals(Constants.SignInStatus.OK)){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }else{
                    showRestartDialog();
                }
            });
        }else{
            showRestartDialog();
        }
    }

    public void showRestartDialog(){
        showDialog(R.string.error_title, R.string.error_login,
                R.string.retry, (dialog, which) -> GoogleService.getInstance(SplashActivity.this).startSignInIntent(),
                R.string.exit, (dialog, which) -> System.exit(1), null, null);
    }
}
