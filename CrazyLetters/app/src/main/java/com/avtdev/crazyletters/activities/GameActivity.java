package com.avtdev.crazyletters.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Logger;

public class GameActivity extends AppCompatActivity {

    Game mGame;
    GameConstants.Mode mGameMode;

    TextView mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        try{
         //   if(getIntent() != null && getIntent().getExtras() != null){
              /*  Bundle extras = getIntent().getExtras();
                Long gameId = extras.getLong(Constants.Extras.GAME.name());
                mGame = RealmManager.getInstance(this).getGame(gameId);
                if(mGame == null){*/
                    mGame = new Game(
                            new int[]{1,2},
                            new GameConstants.LettersType[]{GameConstants.LettersType.DIAGONAL_MOVE},
                            new String[]{"en"},
                            125000);
                    //finish();
              //  }
                //mGameMode = GameConstants.Mode.valueOf(extras.getString(Constants.Extras.GAME_MODE.name(), GameConstants.Mode.INVITATION.name()));


            mTime = findViewById(R.id.tvTime);
            initializeTime();

          /*  }else{
                finish();
            }*/

        }catch (Exception ex){
            Logger.log(Logger.LOGGER_TYPE.ERROR, "GameActivity", "onCreate", ex);
            finish();
        }
    }

    private void initializeTime(){
        if(mGame.getTime() > 0){
            new CountDownTimer(mGame.getTime(), 1000){
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000);
                    int minutes = seconds / 60;
                    seconds -= minutes * 60;

                    mTime.setText(String.format("%02d:%02d", minutes, seconds));
                }

                public void onFinish() {
                    finish();
                }
            }.start();
        }else{
            mTime.setText('\u221E');
        }
    }
}
