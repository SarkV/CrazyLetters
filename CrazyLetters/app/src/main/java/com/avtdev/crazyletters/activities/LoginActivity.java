package com.avtdev.crazyletters.activities;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.services.ConstantGS;
import com.avtdev.crazyletters.services.GoogleService;

public class LoginActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleService.getInstance(LoginActivity.this).startSignInIntent();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantGS.REQUEST_CODE.SIGN_IN) {
            Object message = GoogleService.getInstance(this).checkSignIn(data);
            if(message == null){

            }else{
                showOneBtnDialog(null, message, android.R.string.ok, null);
            }
        }

    }
}
