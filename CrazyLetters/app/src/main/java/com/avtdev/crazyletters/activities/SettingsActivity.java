package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

public class SettingsActivity extends BaseActivity implements View.OnClickListener, RewardedVideoAdListener {

    Switch swAllowInvitations;
    Switch swShowNotifications;
    Switch swEnableSound;
    TextView removeAdsTime;
    ImageView removeAdsButton;

    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());

        swShowNotifications = findViewById(R.id.swShowNotifications);
        swAllowInvitations = findViewById(R.id.swAllowInvitations);
        swEnableSound = findViewById(R.id.swEnableSound);
        removeAdsTime = findViewById(R.id.tvRemoveAds);
        removeAdsButton = findViewById(R.id.ivRemoveAds);


        swShowNotifications.setChecked(Utils.getBooleanSharedPreferences(this, Constants.Preferences.SHOW_NOTIFICATIONS.name(), true));
        swAllowInvitations.setChecked(Utils.getBooleanSharedPreferences(this, Constants.Preferences.ALLOW_INVITATIONS.name(), true));
        swEnableSound.setChecked(Utils.getBooleanSharedPreferences(this, Constants.Preferences.ENABLE_SOUND.name(), true));

        checkAds();

        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnSignOut).setOnClickListener(this);

        removeAdsButton.setOnClickListener(this);
    }

    private void checkAds(){
        if(areAdsEnabled()){
            removeAdsTime.setVisibility(View.GONE);
            removeAdsButton.setVisibility(View.VISIBLE);
            setBannerAd();
        }else{
            removeAdsTime.setVisibility(View.VISIBLE);
            removeAdsButton.setVisibility(View.GONE);
            initializeTime();
            hideAds();
        }
    }

    private void initializeTime(){
        long withoutAds = Utils.getLongSharedPreferences(this, Constants.Preferences.WITHOUT_ADS.name(), 0L);
        long timeRemain = Utils.getDifferenceDates(Utils.getUTCDate(), withoutAds);
        if(timeRemain > 0){
            new CountDownTimer(timeRemain, 1000){
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000);
                    int minutes = seconds / 60;
                    int hours = minutes / 60;
                    minutes -= hours * 60;
                    seconds -= ((minutes * 60) + (hours * 3600));
                    if(hours > 0){
                        removeAdsTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                    }else if(minutes > 0){
                        removeAdsTime.setText(String.format("%02d:%02d", minutes, seconds));
                    }else{
                        removeAdsTime.setText(String.format("%02d", seconds));
                    }

                }

                public void onFinish() {
                    checkAds();
                }
            }.start();
        }else{
            checkAds();
        }
    }


    public void setRemoveAds() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, Constants.NOT_SHOW_ADS_TIME);
        Utils.setSharedPreferences(this, Constants.Preferences.WITHOUT_ADS.name(), Utils.getUTCDate(cal.getTimeInMillis()));
        checkAds();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                Utils.setSharedPreferences(this,
                        Constants.Preferences.SHOW_NOTIFICATIONS.name(),
                        swShowNotifications.isChecked());

                Utils.setSharedPreferences(this,
                        Constants.Preferences.ALLOW_INVITATIONS.name(),
                        swAllowInvitations.isChecked());

                Utils.setSharedPreferences(this,
                        Constants.Preferences.ENABLE_SOUND.name(),
                        swEnableSound.isChecked());
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnSignOut:
                GoogleService.getInstance(this).signOut((@NonNull Task task) -> {
                    logout();
                });
                break;
            case R.id.ivRemoveAds:
                mRewardedVideoAd.setRewardedVideoAdListener(this);
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }
                break;
        }
    }

    @Override
    public void onRewarded(RewardItem reward) {
        setRemoveAds();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {}

    @Override
    public void onRewardedVideoAdClosed() {}

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {}

    @Override
    public void onRewardedVideoAdLoaded() {}

    @Override
    public void onRewardedVideoAdOpened() {}

    @Override
    public void onRewardedVideoStarted() {}

    @Override
    public void onRewardedVideoCompleted() { }
}
