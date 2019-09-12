package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;

public class CompetitiveSelectionActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitive_selection);

        setBannerAd();

        findViewById(R.id.btnEasy).setOnClickListener(this);
        findViewById(R.id.btnMedium).setOnClickListener(this);
        findViewById(R.id.btnDifficult).setOnClickListener(this);
        findViewById(R.id.btnImpossible).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnEasy:
                selectGameMode(GameConstants.Level.EASY.name());
                break;
            case R.id.btnMedium:
                selectGameMode(GameConstants.Level.MEDIUM.name());
                break;
            case R.id.btnDifficult:
                selectGameMode(GameConstants.Level.DIFFICULT.name());
                break;
            case R.id.btnImpossible:
                selectGameMode(GameConstants.Level.IMPOSSIBLE.name());
                break;
        }
    }

    private void selectGameMode(String level){
        Intent i = new Intent(this, GameActivity.class);
        Game game = RealmManager.getInstance(this).getDefaultGame(level);
        long id = 0;
        if(game != null){
            id = game.getId();
        }
        i.putExtra(Constants.Extras.GAME_MODE.name(), GameConstants.Mode.MULTI_PLAYER.name());
        i.putExtra(Constants.Extras.GAME.name(), id);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
