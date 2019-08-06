package com.avtdev.crazyletters.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.response.DictionaryResponse;
import com.avtdev.crazyletters.services.ConstantGS;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Logger;
import com.avtdev.crazyletters.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SplashActivity extends BaseActivity {

    private static String TAG = "SplashActivity";

    int numberOfSync = Constants.Firebase.NUMSINCRO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
/* TODO Notifications
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {

        });*/
        getData();
    }

    private void getData(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dictionary = database.getReference(Constants.Firebase.DICTIONARY);
        DatabaseReference gameMode = database.getReference(Constants.Firebase.GAMEMODE);

        Long lastSincroDictionary = Utils.getLongSharedPreferences(SplashActivity.this, Constants.Preferences.LAST_SYNC_DICTIONARY.name(), null);
        Long lastSincroGameModes = Utils.getLongSharedPreferences(SplashActivity.this, Constants.Preferences.LAST_SYNC_GAME_MODES.name(), null);

        Long currentTime = Utils.getUTCDate();

        Query queryDictionary = dictionary;
        if(lastSincroDictionary != null){
            queryDictionary = dictionary.orderByChild(Constants.Firebase.CREATEDAT).startAt(lastSincroDictionary);
        }
        queryDictionary.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Logger.d(TAG, "dictionary_onDataChange", dataSnapshot.getChildrenCount());

                try{

                    List<DictionaryResponse> listResponse = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        DictionaryResponse response = data.getValue(DictionaryResponse.class);
                        if(response != null){
                            response.setId(data.getKey());
                            listResponse.add(response);
                        }
                    }

                    RealmManager.getInstance(SplashActivity.this).setDictionary(listResponse);

                    Utils.setSharedPreferences(SplashActivity.this, Constants.Preferences.LAST_SYNC_DICTIONARY.name(), currentTime);

                }catch (Exception e){
                    Logger.e(TAG, "dictionary_onDataChange", e.getMessage());
                }

                numberOfSync--;
                login();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Logger.e(TAG, "dictionary_onCancelled", databaseError.getCode(), databaseError.getMessage());
                numberOfSync--;
                login();
            }
        });

        Query queryGameMode = gameMode;
        if(lastSincroGameModes != null){
            queryGameMode = gameMode.orderByChild(Constants.Firebase.CREATEDAT).startAt(lastSincroDictionary);
        }
        queryGameMode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Logger.d(TAG, "gameMode_onDataChange", dataSnapshot.getChildrenCount());

                try{
/*
                    List<DictionaryResponse> listResponse = new ArrayList<>();
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while(iterator.hasNext()){
                        DataSnapshot data = iterator.next();
                        DictionaryResponse response = data.getValue(DictionaryResponse.class);
                        response.setId(data.getKey());
                        listResponse.add(response);
                    }
    */
                    Utils.setSharedPreferences(SplashActivity.this, Constants.Preferences.LAST_SYNC_GAME_MODES.name(), currentTime);

                }catch (Exception e){
                    Logger.e(TAG, "gameMode_onDataChange", e.getMessage());
                }

                numberOfSync--;
                login();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Logger.e(TAG, "gameMode_onCancelled", databaseError.getCode(), databaseError.getMessage());
                numberOfSync--;
                login();
            }
        });
    }

    private void login(){
        if(numberOfSync == 0){
            GoogleService.getInstance(this).signInSilently((Constants.SignInStatus status) -> {
                if(status != null && status.equals(Constants.SignInStatus.OK)){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }else{
                    GoogleService.getInstance(this).startSignInIntent();
                };
            });
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
