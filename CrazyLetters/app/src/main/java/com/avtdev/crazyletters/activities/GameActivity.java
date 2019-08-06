package com.avtdev.crazyletters.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.GameRoom;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Logger;

import java.util.ArrayList;

public class GameActivity extends BaseActivity implements View.OnClickListener {

    Game mGame;
    GameConstants.Mode mGameMode;
    GameRoom mGameRoom;

    TextView mTime;
    TextView mCurrentWord;

    ProgressBar mPlayerPuntuationPB;
    TextView mPlayerPuntuationTv;
    ProgressBar mBestPuntuationPB;
    TextView mBestPuntuationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        try{
            mGame = RealmManager.getInstance(this).getLastGame();
            if(mGame == null){

                mGameRoom = GameRoom.getInstance();
                mGameRoom.startGame(new ArrayList<>());
                //finish();
            }
            //mGameMode = GameConstants.Mode.valueOf(extras.getString(Constants.Extras.GAME_MODE.name(), GameConstants.Mode.INVITATION.name()));

            mTime = findViewById(R.id.tvTime);
            mCurrentWord = findViewById(R.id.tvCurrentWord);

            mPlayerPuntuationPB = findViewById(R.id.pbPlayerPuntuation);
            mPlayerPuntuationTv = findViewById(R.id.tvPlayerPuntuation);
            mBestPuntuationPB = findViewById(R.id.pbBestContraryPuntuation);
            mBestPuntuationTv = findViewById(R.id.tvBestContraryPuntuation);
            findViewById(R.id.ivRemove).setOnClickListener(this);

            initializeTime();
            initializeTextListener();

        }catch (Exception ex){
            Logger.e("GameActivity", "onCreate", ex);
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
            mTime.setText(R.string.infinite);
        }
    }

    private void initializeTextListener(){
        mCurrentWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0){
                    addWord(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addWord(String word){
        String text = mCurrentWord.getText().toString();
        if(mGameRoom.checkPlayerWord(text)){
            int[] puntuations = mGameRoom.checkPuntuations();

            int total = puntuations[0] + puntuations[1];
            mPlayerPuntuationTv.setText(String.valueOf(puntuations[0]));
            mPlayerPuntuationPB.setProgress((puntuations[0]  * 100 ) / total);

            mBestPuntuationTv.setText((String.valueOf(puntuations[1])));
            mBestPuntuationPB.setProgress((puntuations[1]  * 100 ) / total);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivRemove:
                mCurrentWord.setText("");
                break;
        }
    }
}
