package com.avtdev.crazyletters.activities;

import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.realm.Game;

import java.util.stream.IntStream;

public class GameDefinitionActivity extends BaseActivity {

    SeekBar mMinVelSB;
    TextView mMinVelTV;
    SeekBar mMaxVelSB;
    TextView mMaxVelTV;
    NumberPicker mTimeNP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_definition);

        mMinVelSB = findViewById(R.id.sbMinVel);
        mMinVelTV = findViewById(R.id.tvMinVel);
        mMaxVelSB = findViewById(R.id.sbMaxVel);
        mMaxVelTV = findViewById(R.id.tvMaxVel);
        mTimeNP = findViewById(R.id.npTime);

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

     //   new Game(int[] mVelocity, GameConstants.LettersType[] mLettersType, String[] mLanguages, int mTime)
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
}
