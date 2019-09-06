package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Logger;
import com.avtdev.crazyletters.utils.Utils;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class GameDefinitionActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG = "GameDefinitionActivity";
    public final static int SELECT_LANGUAGE_CODE = 100;
    public final static int LOAD_GAME_CODE = 101;

    Game mGame;

    EditText mGameName;

    SeekBar mMinVelSB;
    TextView mMinVelTV;
    SeekBar mMaxVelSB;
    TextView mMaxVelTV;

    CheckBox mHorizMove;
    CheckBox mVertMove;
    CheckBox mDiagMove;
    CheckBox mShowHide;

    TextView mLanguagesTV;
    String[] mLanguagesList;

    CheckBox mAccent;

    NumberPicker mTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_definition);

        mGameName = findViewById(R.id.etName);

        mMinVelSB = findViewById(R.id.sbMinVel);
        mMinVelTV = findViewById(R.id.tvMinVel);
        mMaxVelSB = findViewById(R.id.sbMaxVel);
        mMaxVelTV = findViewById(R.id.tvMaxVel);

        mHorizMove = findViewById(R.id.cbHorizMove);
        mVertMove = findViewById(R.id.cbVertMove);
        mDiagMove = findViewById(R.id.cbDiagMove);
        mShowHide = findViewById(R.id.cbShowHide);

        mLanguagesTV = findViewById(R.id.tvLanguages);

        mAccent = findViewById(R.id.cbUseAccent);

        mTimePicker = findViewById(R.id.timePicker);

        findViewById(R.id.btnLoadGame).setOnClickListener(this);
        findViewById(R.id.btnSaveGame).setOnClickListener(this);
        findViewById(R.id.btnPlay).setOnClickListener(this);

        if(RealmManager.getInstance(this).getLanguages().size() > 2){
            findViewById(R.id.btnSelectLanguages).setOnClickListener(this);
            mLanguagesTV.setOnClickListener(this);
        }else{
            findViewById(R.id.btnSelectLanguages).setVisibility(View.INVISIBLE);
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
        hideKeyboard();
        if(v.getId() == R.id.btnLoadGame) {
            Intent i = new Intent(this, GameListActivity.class);
            i.putExtra(Constants.Extras.GAME_MODIFIED.name(), hasModified());
            startActivityForResult(i, LOAD_GAME_CODE);
        }else if(v.getId() == R.id.btnSelectLanguages || v.getId() == mLanguagesTV.getId()) {
            Intent i = new Intent(this, LanguageSelectionActivity.class);
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
                showOneBtnDialog(R.string.error_title,
                        R.string.error_no_letter_type,
                        R.string.accept,
                        (dialog, which) -> {dialog.dismiss();});

            }else if(v.getId() == R.id.btnSaveGame){
                if(!name.isEmpty() && (mGame == null || !name.equals(mGame.getName()))){
                    mGame = RealmManager.getInstance(this).saveGame(
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

                    RealmManager.getInstance(this).updateGame(mGame, false);
                }else{
                    showOneBtnDialog(R.string.error_title,
                            R.string.error_game_name,
                            R.string.accept,
                            (dialog, which) -> {dialog.dismiss();});
                }
                if(mGame == null){
                    showOneBtnDialog(R.string.error_title,
                            R.string.error_save_game,
                            R.string.accept,
                            (dialog, which) -> {dialog.dismiss();});
                }else{
                    showOneBtnDialog(R.string.game_save_title,
                            R.string.game_save_message,
                            R.string.accept,
                            (dialog, which) -> {dialog.dismiss();});
                }
            }else{
                if(hasModified() && !Utils.isNull(mGameName.getText().toString())){
                    showTwoBtnDialog(R.string.warning, R.string.warning_game_not_saved, R.string.accept,
                            (dialog, which) -> {
                                mGame = RealmManager.getInstance(this).updateGame(mGame, true);
                                runGame(v.getId());
                            },
                            R.string.cancel, (dialog, which) -> {

                                mGame = RealmManager.getInstance(this).saveGame(
                                        null,
                                        vel,
                                        lettersTypes.toArray(new GameConstants.LettersType[0]),
                                        mLanguagesList,
                                        !mAccent.isChecked(),
                                        time, true);
                                runGame(v.getId());

                    });
                }else if(Utils.isNull(mGameName.getText().toString())){
                    mGame = RealmManager.getInstance(this).saveGame(
                            null,
                            vel,
                            lettersTypes.toArray(new GameConstants.LettersType[0]),
                            mLanguagesList,
                            !mAccent.isChecked(),
                            time, true);
                    runGame(v.getId());
                }else{
                    mGame = RealmManager.getInstance(this).updateGame(mGame, true);
                    runGame(v.getId());
                }
            }
        }
    }

    private void runGame(long btnId){
        if(mGame == null){
            showOneBtnDialog(R.string.error_title,
                    R.string.error_create_game,
                    R.string.accept,
                    (dialog, which) -> {dialog.dismiss();});
        }else{
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(Constants.Extras.GAME.name(), mGame.getId());
            intent.putExtra(Constants.Extras.GAME_MODE.name(), btnId == R.id.btnInvite ? GameConstants.Mode.INVITATION : GameConstants.Mode.SINGLE_PLAYER);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if(res == RESULT_OK){
            if (req == LOAD_GAME_CODE) {
                try {
                    if (data != null && data.getExtras() != null) {
                        Long gameId = data.getExtras().getLong(Constants.Extras.GAME.name());
                        mGame = RealmManager.getInstance(this).getGame(gameId);
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
