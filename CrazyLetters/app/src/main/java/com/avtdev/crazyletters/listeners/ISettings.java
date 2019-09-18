package com.avtdev.crazyletters.listeners;

public interface ISettings extends IMain {
    void logout();
    void signIn();
    void setBannerAd();
    void hideAds();
    boolean areAdsEnabled();
}
