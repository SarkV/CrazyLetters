package com.avtdev.crazyletters.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

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
}