package com.avtdev.crazyletters.listeners;

import android.content.DialogInterface;

import androidx.fragment.app.Fragment;

public interface IMain {
    boolean isOffline();
    void hideKeyboard();
    void onBackPressed();
    void changeFragment(Fragment fragment, boolean override);
    void showOneBtnDialog(Integer title, Object message, Integer positiveMessage, DialogInterface.OnClickListener positiveListener);
    void showTwoBtnDialog(Integer title, Object message,
                          Integer positiveMessage, DialogInterface.OnClickListener positiveListener,
                          Integer negativeMessage, DialogInterface.OnClickListener negativeListener);
    void showDialog (Integer title, Object message,
                     Integer positiveMessage, DialogInterface.OnClickListener positiveListener,
                     Integer negativeMessage, DialogInterface.OnClickListener negativeListener,
                     Integer neutralMessage, DialogInterface.OnClickListener neutralListener);

}
