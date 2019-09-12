package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.google.android.gms.games.GamesActivityResultCodes;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBannerAd();

        findViewById(R.id.btnSinglePlayer).setOnClickListener(this);
        findViewById(R.id.btnMultiplayer).setOnClickListener(this);
        findViewById(R.id.btnSeeInvitations).setOnClickListener(this);
        findViewById(R.id.btnLeaderboard).setOnClickListener(this);
        findViewById(R.id.btnSettings).setOnClickListener(this);
        findViewById(R.id.btnExit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSinglePlayer:
                Intent i = new Intent(MainActivity.this, GameDefinitionActivity.class);
                i.putExtra(Constants.Extras.GAME_MODE.name(), GameConstants.Mode.SINGLE_PLAYER.name());
                startActivity(i);
                break;
            case R.id.btnMultiplayer:
                startActivity(new Intent(MainActivity.this, CompetitiveSelectionActivity.class));
                finish();
                break;
            case R.id.btnSeeInvitations:
                break;
            case R.id.btnLeaderboard:
                GoogleService.getInstance(this).showLeaderboard();
                break;
            case R.id.btnSettings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.btnExit:
                showTwoBtnDialog(R.string.warning,
                        R.string.warning_exit,
                        R.string.exit,
                        (dialog, which) -> finish(),
                        R.string.cancel,
                        (dialog, which) -> dialog.cancel());
                break;
        }
    }
}
