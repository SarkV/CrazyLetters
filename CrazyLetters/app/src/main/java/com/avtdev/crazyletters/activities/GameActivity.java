package com.avtdev.crazyletters.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.realm.Dictionary;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.GameFactory;
import com.avtdev.crazyletters.services.GameRoom;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends BaseActivity implements View.OnClickListener, GameCanvas.IGameCanvas {

    private static final String TAG  = "GameActivity";

    Game mGame;
    GameConstants.Mode mGameMode;
    GameRoom mGameRoom;
    GameCanvas mGameCanvas;

    TextView mTime;
    TextView mCurrentWord;

    ProgressBar mPlayerPuntuationPB;
    TextView mPlayerPuntuationTv;
    ProgressBar mBestPuntuationPB;
    TextView mBestPuntuationTv;

    int mMaxWordLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        try{
            if(getIntent() != null && getIntent().getExtras() != null){
                long id = getIntent().getExtras().getLong(Constants.Extras.GAME.name());
                mGame = RealmManager.getInstance(this).getGame(id);
                mGameMode = GameConstants.Mode.valueOf(getIntent().getExtras().getString(Constants.Extras.GAME_MODE.name(), GameConstants.Mode.SINGLE_PLAYER.name()));
            }
            if(mGameMode == null){
                mGameMode = GameConstants.Mode.SINGLE_PLAYER;
            }
            if(mGame == null){
                mGame = RealmManager.getInstance(this).getLastGame();
            }
            if(mGame == null){
                finish();
            }

            mGameCanvas = findViewById(R.id.gameCanvas);
            mGameCanvas.setListener(this);

            mTime = findViewById(R.id.tvTime);
            mCurrentWord = findViewById(R.id.tvCurrentWord);

            mPlayerPuntuationPB = findViewById(R.id.pbPlayerPuntuation);
            mPlayerPuntuationTv = findViewById(R.id.tvPlayerPuntuation);
            mBestPuntuationPB = findViewById(R.id.pbBestContraryPuntuation);
            mBestPuntuationTv = findViewById(R.id.tvBestContraryPuntuation);
            findViewById(R.id.ivRemove).setOnClickListener(this);

            mGameRoom = GameRoom.getInstance(this);
            List<String> playersId = new ArrayList<>();
            playersId.add("1");
            mGameRoom.setPlayersId(playersId);
            mMaxWordLength = RealmManager.getInstance(this).getDictionaryMax(mGame.getLanguagesString());
            mGameRoom.setSearchVariables(mGame.getLanguagesString(), mGame.hasAccent());

            initializeTime();
            initializeTextListener();

            new GameFactory(mGame, mGameCanvas);

        }catch (Exception ex){
            Logger.e("GameActivity", "onCreate", ex);
            finish();
        }
    }

    private void initializeTime(){
        if(mGame.getTime() > 0){
            new CountDownTimer(mGame.getTime() * 60000, 1000){
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000);
                    int minutes = seconds / 60;
                    seconds -= minutes * 60;

                    mTime.setText(String.format("%02d:%02d", minutes, seconds));
                }

                public void onFinish() {
                    finishGame();
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
                if(s.length() <= mMaxWordLength) {
                    if (s.length() > 0) {
                        addWord(s.toString());
                    }
                }else{
                    mCurrentWord.setText(s.subSequence(0, s.length() - 1));
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
            mPlayerPuntuationPB.setProgress(total > 0 ? (puntuations[0]  * 100 ) / total : 0);

            mBestPuntuationTv.setText((String.valueOf(puntuations[1])));
            mBestPuntuationPB.setProgress(total > 0 ? (puntuations[1]  * 100 ) / total : 0);

            mCurrentWord.setText("");
        }
    }

    public void addLetter(char letter){
        mCurrentWord.setText(mCurrentWord.getText().toString() + letter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivRemove:
                mCurrentWord.setText("");
                break;
        }
    }

    private void finishGame(){
        mGameRoom.finishGame();
        finish();
    }
}
