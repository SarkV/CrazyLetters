package com.avtdev.crazyletters.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.listeners.ISplash;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        GoogleService.getInstance(this).signInSilently((Constants.SignInStatus status) -> {

            Intent intent;
            if(status != null && status.equals(Constants.SignInStatus.OK)){
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }else{
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        });


    }
}
