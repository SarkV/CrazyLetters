package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.services.GoogleService;
import com.google.android.gms.games.GamesActivityResultCodes;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnSinglePlayer).setOnClickListener(this);
        findViewById(R.id.btnMultiplayer).setOnClickListener(this);
        findViewById(R.id.btnInvite).setOnClickListener(this);
        findViewById(R.id.btnSeeInvitations).setOnClickListener(this);
        findViewById(R.id.btnLeaderboard).setOnClickListener(this);
        findViewById(R.id.btnSettings).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSinglePlayer:
                startActivity(new Intent(MainActivity.this, GameActivity.class));

                break;
            case R.id.btnMultiplayer:

                break;
            case R.id.btnInvite:

                break;
            case R.id.btnSeeInvitations:

                break;
            case R.id.btnLeaderboard:
                GoogleService.getInstance(this).showLeaderboard();
                break;
            case R.id.btnSettings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));

                break;
        }
    }
}
