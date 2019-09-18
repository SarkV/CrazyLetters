package com.avtdev.crazyletters.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.activities.BaseActivity;
import com.avtdev.crazyletters.listeners.IGame;
import com.avtdev.crazyletters.listeners.IMain;
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

public class GameFragment extends Fragment implements View.OnClickListener, GameCanvas.IGameCanvas, GameRoom.IGameRoom {

    private static final String TAG  = "GameFragment";

    IGame mListener;

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

    public static GameFragment newInstance(IGame listener, Game game, GameConstants.Mode gameMode) {

        Bundle args = new Bundle();

        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        fragment.mListener = listener;
        fragment.mGame = game;
        fragment.mGameMode = gameMode;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            if(mGameMode == null){
                mGameMode = GameConstants.Mode.SINGLE_PLAYER;
            }
            if(mGame == null){
                mGame = RealmManager.getInstance(getContext()).getLastGame();
            }
            if(mGame == null){
                mListener.changeFragment(MainFragment.newInstance(mListener), true);
            }

            mGameCanvas = view.findViewById(R.id.gameCanvas);
            mGameCanvas.setListener(this);

            mTime = view.findViewById(R.id.tvTime);
            mCurrentWord = view.findViewById(R.id.tvCurrentWord);

            mPlayerPuntuationPB = view.findViewById(R.id.pbPlayerPuntuation);
            mPlayerPuntuationTv = view.findViewById(R.id.tvPlayerPuntuation);
            mBestPuntuationPB = view.findViewById(R.id.pbBestContraryPuntuation);
            mBestPuntuationTv = view.findViewById(R.id.tvBestContraryPuntuation);

            mLastWordPlayer = view.findViewById(R.id.tvLastWordPlayer);
            mLastWord = view.findViewById(R.id.tvLastWord);

            view.findViewById(R.id.ivRemove).setOnClickListener(this);
            view.findViewById(R.id.ivSend).setOnClickListener(this);

            mGameRoom = GameRoom.getInstance(getContext(), this);
            List<String> playersId = new ArrayList<>();
            playersId.add("1");
            mGameRoom.setPlayersId(playersId);
            mMaxWordLength = RealmManager.getInstance(getContext()).getDictionaryMax(mGame.getLanguagesString());
            mGameRoom.setSearchVariables(mGame.getLanguagesString(), mGame.hasAccent());

            initializeTime();
            initializeText();

            new GameFactory(mGame, mGameCanvas, getContext());

        }catch (Exception ex){
            Logger.e("GameFragment", "onViewCreated", ex);
            mListener.changeFragment(MainFragment.newInstance(mListener), true);
        }
    }

    private void initializeText(){
        mCurrentWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCurrentWord.setTextColor(getContext().getColor(R.color.colorSecondaryLight));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initializeTime(){
        if(mGame.getTime() > 0){
            mSoundEnabled = Utils.getBooleanSharedPreferences(getContext(), Constants.Preferences.ENABLE_SOUND.name(), true);

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
                        mCurrentWord.setTextColor(getContext().getColor(R.color.blue));
                        break;
                    case NOT_EXIST:
                        mCurrentWord.setTextColor(getContext().getColor(R.color.red));
                        break;
                }
        }
    }

    private void finishGame(){
        mGameRoom.finishGame();
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
            mListener.showTwoBtnDialog(R.string.warning,
                    message,
                    R.string.exit,
                    (dialog, which) -> exit(),
                    R.string.cancel,
                    (dialog, which) -> dialog.cancel());
        }
    }
}
