package com.avtdev.crazyletters.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Logger;
import com.avtdev.crazyletters.utils.Utils;

public class GameActivity extends AppCompatActivity {

    Game mGame;
    GameConstants.Mode mGameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        try{
            if(getIntent() != null && getIntent().getExtras() != null){
                Bundle extras = getIntent().getExtras();
                Long gameId = getIntent().getExtras().getLong(Constants.Extras.GAME.name());
                mGame = RealmManager.getInstance(this).getGame(gameId);
                if(mGame == null){
                    finish();
                }
                mGameMode = GameConstants.Mode.valueOf(extras.getString(Constants.Extras.GAME_MODE.name(), GameConstants.Mode.INVITATION.name()));

            }else{
                finish();
            }

        }catch (Exception ex){
            Logger.log(Logger.LOGGER_TYPE.ERROR, "GameActivity", "onCreate", ex);
            finish();
        }
    }
}
