package com.avtdev.crazyletters.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.avtdev.crazyletters.BuildConfig;
import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.fragments.MainFragment;
import com.avtdev.crazyletters.listeners.IMain;
import com.avtdev.crazyletters.listeners.ISettings;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.tasks.Task;

public class MainActivity extends BaseActivity implements ISettings, IMain {

    Boolean adsEnabled;
    AdRequest mAdRequest;
    FragmentManager mFragmentManager;
    AdView mAdView = findViewById(R.id.adView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);
        mFragmentManager = getSupportFragmentManager();
        changeFragment(MainFragment.newInstance(this), true);

        if(adsEnabled == null || !adsEnabled){
            adsEnabled = false;
            MobileAds.initialize(this, initializationStatus -> adsEnabled = true);

            setBannerAd();
        }
    }

    private boolean areAdsEnabled(){
        long withoutAds = Utils.getLongSharedPreferences(this, Constants.Preferences.WITHOUT_ADS.name(), 0L);
        return Utils.getUTCDate() >= withoutAds;
    }

    private void setBannerAd(){
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
        return false;
    }

    public void changeFragment(Fragment fragment, boolean override){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if(override){
            fragmentTransaction.replace(R.id.fragment_container, fragment);
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
        GoogleService.getInstance(this).signOut((@NonNull Task task) -> {
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            finish();
        });
    }

    public void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
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

    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);

        if (res == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            finish();
        }
    }
}
