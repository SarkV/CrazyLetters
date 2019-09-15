package com.avtdev.crazyletters.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.activities.BaseActivity;
import com.avtdev.crazyletters.activities.GameListActivity;
import com.avtdev.crazyletters.activities.LanguageSelectionActivity;
import com.avtdev.crazyletters.listeners.IGameDefinition;
import com.avtdev.crazyletters.listeners.IMain;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.GoogleService;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Logger;
import com.avtdev.crazyletters.utils.Utils;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.Arrays;

public class GameDefinitionFragment extends Fragment implements View.OnClickListener {

    private static String TAG = "GameDefinitionFragment";
    public final static int SELECT_LANGUAGE_CODE = 100;
    public final static int LOAD_GAME_CODE = 101;

    private IMain mListener;

    GameConstants.Mode mGameMode;

    private Game mGame;

    private EditText mGameName;

    private SeekBar mMinVelSB;
    private TextView mMinVelTV;
    private SeekBar mMaxVelSB;
    private TextView mMaxVelTV;

    private CheckBox mHorizMove;
    private CheckBox mVertMove;
    private CheckBox mDiagMove;
    private CheckBox mShowHide;

    private TextView mLanguagesTV;
    private String[] mLanguagesList;

    private CheckBox mAccent;

    private NumberPicker mTimePicker;

    public static GameDefinitionFragment newInstance(IMain listener, GameConstants.Mode gameMode) {

        Bundle args = new Bundle();

        GameDefinitionFragment fragment = new GameDefinitionFragment();
        fragment.setArguments(args);
        fragment.mListener = listener;
        fragment.mGameMode = gameMode;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_definition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameName = view.findViewById(R.id.etName);

        mMinVelSB = view.findViewById(R.id.sbMinVel);
        mMinVelTV = view.findViewById(R.id.tvMinVel);
        mMaxVelSB = view.findViewById(R.id.sbMaxVel);
        mMaxVelTV = view.findViewById(R.id.tvMaxVel);

        mHorizMove = view.findViewById(R.id.cbHorizMove);
        mVertMove = view.findViewById(R.id.cbVertMove);
        mDiagMove = view.findViewById(R.id.cbDiagMove);
        mShowHide = view.findViewById(R.id.cbShowHide);

        mLanguagesTV = view.findViewById(R.id.tvLanguages);

        mAccent = view.findViewById(R.id.cbUseAccent);

        mTimePicker = view.findViewById(R.id.timePicker);

        view.findViewById(R.id.btnLoadGame).setOnClickListener(this);
        view.findViewById(R.id.btnSaveGame).setOnClickListener(this);
        view.findViewById(R.id.btnPlay).setOnClickListener(this);

        if(!mListener.isOffline()){
            view.findViewById(R.id.btnInvite).setOnClickListener(this);
        }else{
            view.findViewById(R.id.btnInvite).setEnabled(false);
        }

        if(RealmManager.getInstance(getContext()).getLanguages().size() > 2){
            view.findViewById(R.id.btnSelectLanguages).setOnClickListener(this);
            mLanguagesTV.setOnClickListener(this);
        }else{
            view.findViewById(R.id.btnSelectLanguages).setVisibility(View.INVISIBLE);
        }

        setLanguages(new String[0]);

        initializeVelocity();

        initializeTimePicker();
    }

    private void initializeTimePicker(){
        String[] timeValues = new String[61];
        for(int i = 0; i < timeValues.length; i++){
            if(i == 0){
                timeValues[i] = getString(R.string.infinite);
            }else{
                timeValues[i] = String.valueOf(i);
            }
        }
        mTimePicker.setMinValue(0);
        mTimePicker.setMaxValue(timeValues.length - 1);
        mTimePicker.setDisplayedValues(timeValues);
        mTimePicker.setValue(0);
    }

    private void initializeVelocity(){


        mMinVelSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mMaxVelSB.getProgress() < progress){
                    progress = mMaxVelSB.getProgress();
                    seekBar.setProgress(progress);
                }
                mMinVelTV.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mMaxVelSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mMinVelSB.getProgress() > progress){
                    progress = mMinVelSB.getProgress();
                    seekBar.setProgress(progress);
                }
                mMaxVelTV.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int max = 6;

        mMinVelSB.setMax(max);
        mMinVelSB.setProgress(0);
        mMaxVelSB.setMax(max);
        mMaxVelSB.setProgress(max);
    }

    private void setLanguages(String[] languages){
        if(languages != null && languages.length > 0 && !"".equals(languages[0])){
            StringBuilder stringBuilder = new StringBuilder();
            mLanguagesList = languages;
            for (String lan : mLanguagesList){
                if(stringBuilder.length() > 0){
                    stringBuilder.append(Constants.ARRAY_SEPARATOR);
                }
                stringBuilder.append(lan);
            }
            mLanguagesTV.setText(stringBuilder.toString());
        }else{
            mLanguagesList = new String[0];
            mLanguagesTV.setText(R.string.all);
        }
    }

    private boolean hasModified(){
        boolean noModified = true;
        try {
            if (mGame == null) {
                noModified = Utils.isNull(mGameName.getText().toString());
                noModified = noModified && mMinVelSB.getProgress() == 0;
                noModified = noModified && mMaxVelSB.getProgress() == mMaxVelSB.getMax();
                noModified = noModified && (mLanguagesList == null || mLanguagesList.length == 0);
                noModified = noModified && !mHorizMove.isChecked();
                noModified = noModified && !mVertMove.isChecked();
                noModified = noModified && !mDiagMove.isChecked();
                noModified = noModified && !mShowHide.isChecked();
                noModified = noModified && !mAccent.isChecked();
                noModified = noModified && mTimePicker.getValue() == 0;
            } else {
                noModified = mGame.getName().equals(mGameName.getText().toString());
                noModified = noModified && mGame.getVelocity()[0] == mMinVelSB.getProgress();
                noModified = noModified && mGame.getVelocity()[1] == mMaxVelSB.getProgress();
                noModified = noModified && Arrays.equals(mGame.getLanguages(), mLanguagesList);
                GameConstants.LettersType[] lettersTypes = mGame.getLettersType();
                int lettersMovement = 0;
                lettersMovement += mHorizMove.isChecked() ? 1 : 0;
                lettersMovement += mVertMove.isChecked() ? 1 : 0;
                lettersMovement += mDiagMove.isChecked() ? 1 : 0;
                lettersMovement += mShowHide.isChecked() ? 1 : 0;
                if(lettersMovement != lettersTypes.length){
                    noModified = false;
                }else{
                    for(GameConstants.LettersType lettersType : lettersTypes){
                        switch (lettersType){
                            case HORIZONTAL_MOVE:
                                noModified = noModified && mHorizMove.isChecked();
                                break;
                            case VERTICAL_MOVE:
                                noModified = noModified && mVertMove.isChecked();
                                break;
                            case DIAGONAL_MOVE:
                                noModified = noModified && mDiagMove.isChecked();
                                break;
                            case SHOW_HIDE:
                                noModified = noModified && mShowHide.isChecked();
                                break;
                        }
                    }
                }
                noModified = noModified && mGame.hasAccent() != mAccent.isChecked();
                noModified = noModified && mGame.getTime() == mTimePicker.getValue();
            }
        }catch (Exception ex){
            Logger.e(TAG, "hasModified", ex);
            noModified = false;
        }
        return !noModified;
    }

    @Override
    public void onClick(View v) {
        mListener.hideKeyboard();
        if(v.getId() == R.id.btnLoadGame) {
            Intent i = new Intent(getActivity(), GameListActivity.class);
            i.putExtra(Constants.Extras.GAME_MODIFIED.name(), hasModified());
            startActivityForResult(i, LOAD_GAME_CODE);
        }else if(v.getId() == R.id.btnSelectLanguages || v.getId() == mLanguagesTV.getId()) {
            Intent i = new Intent(getActivity(), LanguageSelectionActivity.class);
            i.putExtra(Constants.Extras.LANGUAGE_LIST.name(), mLanguagesTV.getText().toString());
            startActivityForResult(i, SELECT_LANGUAGE_CODE);
        }else{
            String name = mGameName.getEditableText().toString();
            Integer[] vel = new Integer[]{
                    Integer.valueOf(mMinVelTV.getText().toString()),
                    Integer.valueOf(mMaxVelTV.getText().toString())
            };

            ArrayList<GameConstants.LettersType> lettersTypes = new ArrayList<>();
            if(mHorizMove.isChecked()) lettersTypes.add(GameConstants.LettersType.HORIZONTAL_MOVE);
            if(mVertMove.isChecked()) lettersTypes.add(GameConstants.LettersType.VERTICAL_MOVE);
            if(mDiagMove.isChecked()) lettersTypes.add(GameConstants.LettersType.DIAGONAL_MOVE);
            if(mShowHide.isChecked()) lettersTypes.add(GameConstants.LettersType.SHOW_HIDE);

            int time = mTimePicker.getValue();


            if(lettersTypes.isEmpty()){
                mListener.showOneBtnDialog(R.string.error_title,
                        R.string.error_no_letter_type,
                        R.string.accept,
                        (dialog, which) -> {dialog.dismiss();});

            }else if(v.getId() == R.id.btnSaveGame){
                if(!name.isEmpty() && (mGame == null || !name.equals(mGame.getName()))){
                    mGame = RealmManager.getInstance(getContext()).saveGame(
                            name,
                            vel,
                            lettersTypes.toArray(new GameConstants.LettersType[0]),
                            mLanguagesList,
                            !mAccent.isChecked(),
                            time, false);
                }else if(!name.isEmpty()){
                    mGame.setVelocity(vel);
                    mGame.setLettersType(lettersTypes.toArray(new GameConstants.LettersType[0]));
                    mGame.setLanguages(mLanguagesList);
                    mGame.setTime(time);

                    RealmManager.getInstance(getContext()).updateGame(mGame, false);
                }else{
                    mListener.showOneBtnDialog(R.string.error_title,
                            R.string.error_game_name,
                            R.string.accept,
                            (dialog, which) -> {dialog.dismiss();});
                }
                if(mGame == null){
                    mListener.showOneBtnDialog(R.string.error_title,
                            R.string.error_save_game,
                            R.string.accept,
                            (dialog, which) -> {dialog.dismiss();});
                }else{
                    mListener.showOneBtnDialog(R.string.game_save_title,
                            R.string.game_save_message,
                            R.string.accept,
                            (dialog, which) -> {dialog.dismiss();});
                }
            }else{
                if(hasModified() && !Utils.isNull(mGameName.getText().toString())){
                    mListener.showTwoBtnDialog(R.string.warning, R.string.warning_game_not_saved, R.string.accept,
                            (dialog, which) -> {
                                mGame = RealmManager.getInstance(getContext()).updateGame(mGame, true);
                                runGame(v.getId());
                            },
                            R.string.cancel, (dialog, which) -> {

                                mGame = RealmManager.getInstance(getContext()).saveGame(
                                        null,
                                        vel,
                                        lettersTypes.toArray(new GameConstants.LettersType[0]),
                                        mLanguagesList,
                                        !mAccent.isChecked(),
                                        time, true);
                                runGame(v.getId());

                    });
                }else if(Utils.isNull(mGameName.getText().toString())){
                    mGame = RealmManager.getInstance(getContext()).saveGame(
                            null,
                            vel,
                            lettersTypes.toArray(new GameConstants.LettersType[0]),
                            mLanguagesList,
                            !mAccent.isChecked(),
                            time, true);
                    runGame(v.getId());
                }else{
                    mGame = RealmManager.getInstance(getContext()).updateGame(mGame, true);
                    runGame(v.getId());
                }
            }
        }
    }

    private void runGame(long btnId){
        if(mGame == null){
            mListener.showOneBtnDialog(R.string.error_title,
                    R.string.error_create_game,
                    R.string.accept,
                    (dialog, which) -> {dialog.dismiss();});
        }else{
           /* Intent intent = new Intent(this, GameFragment.class);
            intent.putExtra(Constants.Extras.GAME.name(), mGame.getId());
            intent.putExtra(Constants.Extras.GAME_MODE.name(), btnId == R.id.btnInvite ? GameConstants.Mode.INVITATION : GameConstants.Mode.SINGLE_PLAYER);
            startActivity(intent);
            finish();*/
        }
    }

    @Override
    public void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if(res == Activity.RESULT_OK){
            if (req == LOAD_GAME_CODE) {
                try {
                    if (data != null && data.getExtras() != null) {
                        Long gameId = data.getExtras().getLong(Constants.Extras.GAME.name());
                        mGame = RealmManager.getInstance(getContext()).getGame(gameId);
                        if (mGame != null) {
                            if(mGame.isDefaultGame())
                                mGame.setName("");
                            mGameName.setText(mGame.getName());

                            Integer[] vel = mGame.getVelocity();
                            if (vel != null && vel.length == 2) {
                                mMinVelSB.setProgress(vel[0]);
                                mMaxVelSB.setProgress(vel[1]);
                            }

                            GameConstants.LettersType[] lettersTypes = mGame.getLettersType();

                            mHorizMove.setChecked(false);
                            mVertMove.setChecked(false);
                            mDiagMove.setChecked(false);
                            mShowHide.setChecked(false);

                            for (GameConstants.LettersType lettersType : lettersTypes) {
                                switch (lettersType) {
                                    case HORIZONTAL_MOVE:
                                        mHorizMove.setChecked(true);
                                        break;
                                    case VERTICAL_MOVE:
                                        mVertMove.setChecked(true);
                                        break;
                                    case DIAGONAL_MOVE:
                                        mDiagMove.setChecked(true);
                                        break;
                                    case SHOW_HIDE:
                                        mShowHide.setChecked(true);
                                        break;
                                }
                            }

                            setLanguages(mGame.getLanguages());
                            mAccent.setChecked(!mGame.hasAccent());
                            mTimePicker.setValue(mGame.getTime());
                        }
                    }
                } catch (Exception ex) {
                    Logger.e(TAG, "onActivityResult - Select Game", ex);
                }
            }else if(req == SELECT_LANGUAGE_CODE){
                    try{
                        if(data != null && data.getExtras() != null){
                            ArrayList<String> languages = data.getExtras().getStringArrayList(Constants.Extras.LANGUAGE_LIST.name());
                            if(languages != null) {
                                setLanguages(languages.toArray(new String[languages.size()]));
                            }
                        }
                    }catch (Exception ex){
                        Logger.e(TAG, "onActivityResult - Select Language", ex);
                    }
            }
        }
    }
}
