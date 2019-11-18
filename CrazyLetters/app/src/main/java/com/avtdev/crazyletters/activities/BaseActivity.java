package com.avtdev.crazyletters.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.avtdev.crazyletters.BuildConfig;
import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.services.ConstantGS;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.tasks.Task;

public class BaseActivity extends AppCompatActivity {

    GoogleService mGoogleService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantGS.REQUEST_CODE.SIGN_IN && resultCode == RESULT_OK) {
            GoogleService.getInstance(this).checkSignIn(data, (Constants.SignInStatus status) -> {
                if(status != null && status.equals(Constants.SignInStatus.OK)){
                    Utils.setSharedPreferences(this, Constants.Preferences.SIGN_IN_REFUSED.name(), false);
                    if(this instanceof SplashActivity){
                        ((SplashActivity) this).changeActivity(false);
                    }else{
                        ((MainActivity) this).reconnect();
                    }
                }else{
                    showRestartDialog(this instanceof SplashActivity);
                }
            });
        }else{
            showRestartDialog(this instanceof SplashActivity);
        }
    }

    public void signIn(){
        GoogleService.getInstance(this).startSignInIntent();
    }

    public void showRestartDialog(boolean inSplash){
        if(inSplash){
            showDialog(R.string.error_title, R.string.error_login,
                    R.string.retry, (dialog, which) -> GoogleService.getInstance(BaseActivity.this).startSignInIntent(),
                    R.string.without_connection, (dialog, which) -> ((SplashActivity) this).changeActivity(true),
                    R.string.exit, (dialog, which) -> System.exit(1));
        }else{
            showTwoBtnDialog(R.string.error_title, R.string.error_login,
                    R.string.retry, (dialog, which) -> GoogleService.getInstance(BaseActivity.this).startSignInIntent(),
                    R.string.cancel, (dialog, which) -> dialog.dismiss());
        }
    }

}
