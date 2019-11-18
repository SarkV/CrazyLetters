package com.avtdev.crazyletters.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.avtdev.crazyletters.BuildConfig;
import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.activities.BaseActivity;
import com.avtdev.crazyletters.listeners.IMain;
import com.avtdev.crazyletters.listeners.ISettings;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

public class SettingsFragment extends Fragment implements View.OnClickListener, RewardedVideoAdListener {

    private ISettings mListener;

    private Switch swAllowInvitations;
    private Switch swShowNotifications;
    private Switch swEnableSound;
    private TextView removeAdsTime;
    private ImageView removeAdsButton;

    private TextView tvSignIn;

    private RewardedVideoAd mRewardedVideoAd;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ISettings){
            mListener = (ISettings) context;
        }else{
            throw new RuntimeException("Context not instance of ISettings");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mListener == null){
            mListener = (ISettings) getActivity();
        }

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mRewardedVideoAd.loadAd(BuildConfig.ADS ? getString(R.string.reward) : "ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());

        swShowNotifications = view.findViewById(R.id.swShowNotifications);
        swAllowInvitations = view.findViewById(R.id.swAllowInvitations);
        swEnableSound = view.findViewById(R.id.swEnableSound);
        removeAdsTime = view.findViewById(R.id.tvRemoveAds);
        removeAdsButton = view.findViewById(R.id.ivRemoveAds);
        tvSignIn = view.findViewById(R.id.btnSignOut);


        swShowNotifications.setChecked(Utils.getBooleanSharedPreferences(getContext(), Constants.Preferences.SHOW_NOTIFICATIONS.name(), true));
        swAllowInvitations.setChecked(Utils.getBooleanSharedPreferences(getContext(), Constants.Preferences.ALLOW_INVITATIONS.name(), true));
        swEnableSound.setChecked(Utils.getBooleanSharedPreferences(getContext(), Constants.Preferences.ENABLE_SOUND.name(), true));

        checkAds();

        view.findViewById(R.id.btnSave).setOnClickListener(this);
        view.findViewById(R.id.btnCancel).setOnClickListener(this);

        checkOffline();

        removeAdsButton.setOnClickListener(this);
    }

    public void checkOffline(){
        if(mListener.isOffline()){
            tvSignIn.setText(R.string.sign_in);
            tvSignIn.setOnClickListener(v -> mListener.signIn());
        }else{
            tvSignIn.setText(R.string.sign_out);
            tvSignIn.setOnClickListener(v -> mListener.logout());
        }
    }

    private void checkAds(){
        if(mListener.areAdsEnabled()){
            removeAdsTime.setVisibility(View.GONE);
            removeAdsButton.setVisibility(View.VISIBLE);
            mListener.setBannerAd();
        }else{
            removeAdsTime.setVisibility(View.VISIBLE);
            removeAdsButton.setVisibility(View.GONE);
            initializeTime();
            mListener.hideAds();
        }
    }

    private void initializeTime(){
        long withoutAds = Utils.getLongSharedPreferences(getContext(), Constants.Preferences.WITHOUT_ADS.name(), 0L);
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


    private void setRemoveAds(int time) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, time);
        Utils.setSharedPreferences(getContext(), Constants.Preferences.WITHOUT_ADS.name(), Utils.getUTCDate(cal.getTimeInMillis()));
        checkAds();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                Utils.setSharedPreferences(getContext(),
                        Constants.Preferences.SHOW_NOTIFICATIONS.name(),
                        swShowNotifications.isChecked());

                Utils.setSharedPreferences(getContext(),
                        Constants.Preferences.ALLOW_INVITATIONS.name(),
                        swAllowInvitations.isChecked());

                Utils.setSharedPreferences(getContext(),
                        Constants.Preferences.ENABLE_SOUND.name(),
                        swEnableSound.isChecked());
            case R.id.btnCancel:
                mListener.onBackPressed();
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
        setRemoveAds(reward.getAmount());
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
