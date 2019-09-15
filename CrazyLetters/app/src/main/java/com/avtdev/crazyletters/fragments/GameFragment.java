package com.avtdev.crazyletters.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.activities.BaseActivity;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.GameFactory;
import com.avtdev.crazyletters.services.GameRoom;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Logger;
import com.avtdev.crazyletters.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class GameFragment extends BaseActivity implements View.OnClickListener, GameCanvas.IGameCanvas, GameRoom.IGameRoom {

    private static final String TAG  = "GameFragment";

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

    TextView mLastWordPlayer, mLastWord;

    int mMaxWordLength;
    boolean mSoundEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_game);

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

            mLastWordPlayer = findViewById(R.id.tvLastWordPlayer);
            mLastWord = findViewById(R.id.tvLastWord);

            findViewById(R.id.ivRemove).setOnClickListener(this);
            findViewById(R.id.ivSend).setOnClickListener(this);

            mGameRoom = GameRoom.getInstance(this);
            List<String> playersId = new ArrayList<>();
            playersId.add("1");
            mGameRoom.setPlayersId(playersId);
            mMaxWordLength = RealmManager.getInstance(this).getDictionaryMax(mGame.getLanguagesString());
            mGameRoom.setSearchVariables(mGame.getLanguagesString(), mGame.hasAccent());

            initializeTime();
            initializeText();

            new GameFactory(mGame, mGameCanvas, this);

        }catch (Exception ex){
            Logger.e("GameFragment", "onCreate", ex);
            finish();
        }
    }

    private void initializeText(){
        mCurrentWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCurrentWord.setTextColor(getColor(R.color.colorSecondaryLight));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initializeTime(){
        if(mGame.getTime() > 0){
            mSoundEnabled = Utils.getBooleanSharedPreferences(this, Constants.Preferences.ENABLE_SOUND.name(), true);

            new CountDownTimer(mGame.getTime() * 60000, 1000){
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000);
                    int minutes = seconds / 60;
                    seconds -= minutes * 60;

                    if(mSoundEnabled && minutes == 0 && seconds == GameConstants.SECONDS_TO_SOUND){
                        //TODO --> Start sound
                    }

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

    public boolean addLetter(char letter){
        if(mCurrentWord.getText().length() < mMaxWordLength){
            mCurrentWord.setText(mCurrentWord.getText().toString() + letter);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivRemove:
                mCurrentWord.setText("");
                break;
            case R.id.ivSend:
                GameConstants.WordError wordError = mGameRoom.checkPlayerWord(mCurrentWord.getText().toString());
                switch (wordError){
                    case CREATED:
                        mCurrentWord.setText("");
                        break;
                    case ALREADY_DONE:
                        mCurrentWord.setTextColor(getColor(R.color.blue));
                        break;
                    case NOT_EXIST:
                        mCurrentWord.setTextColor(getColor(R.color.red));
                        break;
                }
        }
    }

    private void finishGame(){
        mGameRoom.finishGame();
        finish();
    }

    @Override
    public void modifyPuntuations(String word, boolean isPLayer, int[] puntuations) {
        int total = puntuations[0] + puntuations[1];
        mPlayerPuntuationTv.setText(String.valueOf(puntuations[0]));
        mPlayerPuntuationPB.setProgress((puntuations[0]  * 100 ) / total);

        mBestPuntuationTv.setText((String.valueOf(puntuations[1])));
        mBestPuntuationPB.setProgress((puntuations[1]  * 100 ) / total);

        if(isPLayer){
            mLastWordPlayer.setText(word);
        }else{
            mLastWord.setText(word);
        }
    }

    public void exit(){

    }

    @Override
    public void onBackPressed() {
        int message = 0;
        switch (mGameMode){
            case DEMO:
                message = R.string.warning_exit_demo;
                break;
            case SINGLE_PLAYER:
                message = R.string.warning_exit_single;
                break;
            case MULTI_PLAYER:
                message = R.string.warning_exit_multiplayer;
                break;
            case INVITATION:
                message = R.string.warning_exit_invitation;
                break;
        }
        if(message > 0){
            showTwoBtnDialog(R.string.warning,
                    message,
                    R.string.exit,
                    (dialog, which) -> exit(),
                    R.string.cancel,
                    (dialog, which) -> dialog.cancel());
        }
    }
}
