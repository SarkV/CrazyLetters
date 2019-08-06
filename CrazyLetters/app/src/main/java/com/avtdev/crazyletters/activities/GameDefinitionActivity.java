package com.avtdev.crazyletters.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.services.RealmManager;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class GameDefinitionActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG = "GameDefinitionActivity";
    public static int SELECT_LANGUAGE_CODE = 100;
    public static int LOAD_GAME_CODE = 101;

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

    NumberPicker mTimeNP;

    boolean modified = true;

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

        mTimeNP = findViewById(R.id.npTime);

        setNumberPickerTextColor(mTimeNP, getResources().getColor(R.color.colorSecondaryLight));

        findViewById(R.id.btnLoadGame).setOnClickListener(this);
        findViewById(R.id.btnSaveGame).setOnClickListener(this);
        findViewById(R.id.btnSelectLanguages).setOnClickListener(this);
        findViewById(R.id.btnPlay).setOnClickListener(this);

        initializeVelocity();

        String[] timeValues = new String[61];
        for(int i = 0; i < timeValues.length; i++){
            if(i == 0){
                timeValues[i] = getString(R.string.infinite);
            }else{
                timeValues[i] = String.valueOf(i);
            }
        }
        mTimeNP.setDisplayedValues(timeValues);
        mTimeNP.setMinValue(0);
        mTimeNP.setMaxValue(timeValues.length - 1);
    }


    public static void setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {

        try{
            Field selectorWheelPaintField = numberPicker.getClass()
                    .getDeclaredField("mSelectorWheelPaint");
            selectorWheelPaintField.setAccessible(true);
            ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
        }
        catch(Exception e){
            Logger.e(TAG, "setNumberPickerTextColor", e);
        }
        for(int i = 0; i < numberPicker.getChildCount(); i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText)
                ((EditText)child).setTextColor(color);
        }
        numberPicker.invalidate();
    }

    private void initializeVelocity(){

        mMinVelSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnLoadGame) {
            startActivityForResult(new Intent(this, GameListActivity.class), LOAD_GAME_CODE);
        }else if(v.getId() == R.id.btnSelectLanguages) {
            startActivityForResult(new Intent(this, GameListActivity.class), SELECT_LANGUAGE_CODE);
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

            int time = mTimeNP.getValue();

            if(v.getId() == R.id.btnSaveGame){
                if(mGame == null || (!name.isEmpty() && !name.equals(mGame.getName()))){
                    RealmManager.getInstance(this).saveGame(name, vel, (GameConstants.LettersType[]) lettersTypes.toArray(), mLanguagesList, time);
                }else{
                    mGame.setVelocity(vel);
                    mGame.setLettersType((GameConstants.LettersType[]) lettersTypes.toArray());
                    mGame.setLanguages(mLanguagesList);
                    mGame.setTime(time);

                    RealmManager.getInstance(this).updateGame(mGame);
                }
            }else{
                if(modified){
                    RealmManager.getInstance(this).saveGame(null, vel, (GameConstants.LettersType[]) lettersTypes.toArray(), mLanguagesList, time);
                }else{
                    RealmManager.getInstance(this).saveGame(mGame);
                }
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
