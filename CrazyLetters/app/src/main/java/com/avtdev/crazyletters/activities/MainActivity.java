package com.avtdev.crazyletters.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.avtdev.crazyletters.BuildConfig;
import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.fragments.GameDefinitionFragment;
import com.avtdev.crazyletters.fragments.GameFragment;
import com.avtdev.crazyletters.fragments.MainFragment;
import com.avtdev.crazyletters.fragments.SettingsFragment;
import com.avtdev.crazyletters.listeners.IGame;
import com.avtdev.crazyletters.listeners.IMain;
import com.avtdev.crazyletters.listeners.ISettings;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.Task;

public class MainActivity extends BaseActivity implements ISettings, IMain, IGame {

    Boolean adsEnabled;
    AdRequest mAdRequest;
    FragmentManager mFragmentManager;
    AdView mAdView;
    GoogleService mGoogleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);
        mFragmentManager = getSupportFragmentManager();
        mGoogleService = GoogleService.getInstance(this);

        changeFragment(MainFragment.newInstance(), true);

        if(adsEnabled == null || !adsEnabled){
            adsEnabled = false;
            MobileAds.initialize(this, initializationStatus -> adsEnabled = true);

            setBannerAd();
        }
    }

    public boolean areAdsEnabled(){
        long withoutAds = Utils.getLongSharedPreferences(this, Constants.Preferences.WITHOUT_ADS.name(), 0L);
        if(withoutAds == 0L){
            return true;
        }else{
            if(Utils.getUTCDate() >= withoutAds){
                Utils.removeSharedPreferences(this, Constants.Preferences.WITHOUT_ADS.name());
                return true;
            }
            return false;
        }
    }

    public void setBannerAd(){
        if(areAdsEnabled()){
            if(BuildConfig.ADS){
                mAdView.setAdUnitId(getString(R.string.banner));
            }
            if(mAdRequest == null)
                mAdRequest = new AdRequest.Builder().build();
            mAdView.loadAd(mAdRequest);
        }
    }

    public void hideAds(){
        mAdView.destroy();
    }

    public boolean isOffline(){
        return Utils.getBooleanSharedPreferences(this, Constants.Preferences.SIGN_IN_REFUSED.name(), false);
    }

    public void changeFragment(Fragment fragment, boolean override){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if(override){
            fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getName());
        }else{
            fragmentTransaction.add(R.id.fragment_container, fragment, fragment.getClass().getName());
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }

        if(areAdsEnabled()){
            setBannerAd();
        }else{
            hideAds();
        }
        fragmentTransaction.commit();
    }

    public void logout(){
        mGoogleService.signOut((@NonNull Task task) -> {
            Utils.removeSharedPreferences(this, Constants.Preferences.SIGN_IN_REFUSED.name());
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            finish();
        });
    }

    public void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager != null && getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void setEnabled(View view, View.OnClickListener listener) {
        view.setAlpha(1);
        view.setOnClickListener(listener);
    }

    @Override
    public void setDisabled(View view) {
        view.setAlpha(.5f);
        view.setOnClickListener(v -> signIn());
    }

    public void reconnect(){
        Fragment f = mFragmentManager.getFragments().get(mFragmentManager.getFragments().size() - 1);
        if (MainFragment.class.getName().equals(f.getTag())){
            ((MainFragment) f).checkButtons();
        }else if (SettingsFragment.class.getName().equals(f.getTag())){
            ((SettingsFragment) f).checkOffline();
        }else if (GameDefinitionFragment.class.getName().equals(f.getTag())){
            ((GameDefinitionFragment) f).checkOffline();
        }
    }

    @Override
    public void onBackPressed() {
        if(mFragmentManager.getFragments().get(0).getTag().equals(GameFragment.class.getName())){
            ((GameFragment) mFragmentManager.getFragments().get(0)).onBackPressed();
        }else{
            if (mFragmentManager.getBackStackEntryCount() > 0) {
                mFragmentManager.popBackStack();
            } else {
                showTwoBtnDialog(R.string.warning,
                        R.string.warning_exit,
                        R.string.exit,
                        (dialog, which) -> finish(),
                        R.string.cancel,
                        (dialog, which) -> dialog.cancel());
            }
        }
    }

    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);

        switch (res){
            case GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED:
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                finish();
                break;
            case GamesActivityResultCodes.RESULT_LEFT_ROOM:
                leftRoom();
                break;
        }
    }

    @Override
    protected void onStop() {
        leftRoom();
        super.onStop();
    }

    private void leftRoom(){

    }

    @Override
    public void selectGameMode(GameConstants.Level level){
        mGoogleService.startQuickGame(level.getValue(), new RoomUpdateCallback() {
            @Override
            public void onRoomCreated(int i, @Nullable Room room) {
            }

            @Override
            public void onJoinedRoom(int i, @Nullable Room room) {
                showWaitingRoom(room);
            }

            @Override
            public void onLeftRoom(int i, @NonNull String s) {

            }

            @Override
            public void onRoomConnected(int i, @Nullable Room room) {
                showWaitingRoom(room);
            }
        });
    }

    private void showWaitingRoom(Room room) {
       /* Games.getRealTimeMultiplayerClient(this, mGoogleService.getSignedAccount())
                .getWaitingRoomIntent(room, 1)
                .addOnSuccessListener(intent -> startActivityForResult(intent, WAITING_ROOM_CODE));*/
    }
}
