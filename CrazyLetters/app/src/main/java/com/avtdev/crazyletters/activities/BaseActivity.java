package com.avtdev.crazyletters.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.games.GamesActivityResultCodes;

public class BaseActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    static Boolean adsEnabled;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        if(adsEnabled == null || !adsEnabled){
            adsEnabled = false;
            MobileAds.initialize(this, initializationStatus -> adsEnabled = true);
        }
    }

    protected boolean areAdsEnabled(){
        long withoutAds = Utils.getLongSharedPreferences(this, Constants.Preferences.WITHOUT_ADS.name(), 0L);
        return Utils.getUTCDate() >= withoutAds;
    }

    protected void setBannerAd(){
        if(areAdsEnabled()){
            AdView mAdView = findViewById(R.id.adView);
            if(mAdView != null){
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }
    }

    protected void hideAds(){
        AdView mAdView = findViewById(R.id.adView);
        if(mAdView != null){
            mAdView.destroy();
        }
    }

    public void showOneBtnDialog(Integer title, Object message, Integer positiveMessage, DialogInterface.OnClickListener positiveListener){
        showDialog(title, message, positiveMessage, positiveListener,
                null, null, null, null);
    }

    public void showTwoBtnDialog(Integer title, Object message,
                                 Integer positiveMessage, DialogInterface.OnClickListener positiveListener,
                                 Integer negativeMessage, DialogInterface.OnClickListener negativeListener){
        showDialog(title, message, positiveMessage, positiveListener,
                negativeMessage, negativeListener, null, null);
    }

    public void showDialog (Integer title, Object message,
                            Integer positiveMessage, DialogInterface.OnClickListener positiveListener,
                            Integer negativeMessage, DialogInterface.OnClickListener negativeListener,
                            Integer neutralMessage, DialogInterface.OnClickListener neutralListener){

        AlertDialog.Builder builder =  new AlertDialog.Builder(this);

        if(title != null){
            builder = builder.setTitle(title);
        }
        if(message != null){
            if(message instanceof String){
                builder = builder.setMessage(String.valueOf(message));
            }else{
                builder = builder.setMessage((int) message);
            }
        }
        if(positiveMessage != null){
            builder = builder.setPositiveButton(positiveMessage, positiveListener);
        }
        if(negativeMessage != null){
            builder = builder.setNegativeButton(negativeMessage, negativeListener);
        }
        if(neutralMessage != null){
            builder = builder.setNeutralButton(neutralMessage, neutralListener);
        }
        builder.show();
    }

    public void logout(){
        startActivity(new Intent(BaseActivity.this, SplashActivity.class));
        finish();
    }


    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);

        if (res == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            logout();
        }
    }

    public void showProgressDialog(){
       /* if(mProgressBar == null)
            mProgressBar = findViewById(R.id.progressBar);

        if(mProgressBar != null){
            mProgressBar.setVisibility(View.VISIBLE);
        }*/
    }

    public void hideProgressDialog(){
        if(mProgressBar != null){
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


}
