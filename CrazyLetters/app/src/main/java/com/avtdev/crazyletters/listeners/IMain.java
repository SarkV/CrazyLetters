package com.avtdev.crazyletters.listeners;

import android.content.DialogInterface;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.avtdev.crazyletters.utils.GameConstants;

public interface IMain {
    boolean isOffline();
    void setDisabled(View view);
    void setEnabled(View view, View.OnClickListener listener);
    void hideKeyboard();
    void onBackPressed();
    void selectGameMode(GameConstants.Level level);
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
