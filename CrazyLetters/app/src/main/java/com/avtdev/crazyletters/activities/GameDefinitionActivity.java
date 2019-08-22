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
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;

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

            if(v.getId() == R.id.btnSaveGame){
                if(!name.isEmpty() && (mGame == null || !name.equals(mGame.getName()))){
                    RealmManager.getInstance(this).saveGame(
                            name,
                            vel,
                            lettersTypes.toArray(new GameConstants.LettersType[0]),
                            mLanguagesList,
                            !mAccent.isChecked(),
                            time);
                }else if(!name.isEmpty()){
                    mGame.setVelocity(vel);
                    mGame.setLettersType(lettersTypes.toArray(new GameConstants.LettersType[0]));
                    mGame.setLanguages(mLanguagesList);
                    mGame.setTime(time);

                    RealmManager.getInstance(this).updateGame(mGame);
                }else{
                    showOneBtnDialog(R.string.error_title,
                            R.string.error_game_name,
                            R.string.accept,
                            (dialog, which) -> {dialog.dismiss();});
                }
            }else{
                if(modified){
                    RealmManager.getInstance(this).saveGame(
                            null,
                            vel,
                            lettersTypes.toArray(new GameConstants.LettersType[0]),
                            mLanguagesList,
                            !mAccent.isChecked(),
                            time);
                }else{
                    RealmManager.getInstance(this).saveGame(mGame);
                }
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if(res == RESULT_OK){
            switch (req){
                case LOAD_GAME_CODE:

                break;
                case SELECT_LANGUAGE_CODE:
                    try{
                        if(data != null && data.getExtras() != null){
                            ArrayList<String> languages = data.getExtras().getStringArrayList(Constants.Extras.LANGUAGE_LIST.name());
                            if(languages != null && !languages.isEmpty() && !"".equals(languages.get(0))){
                                StringBuilder stringBuilder = new StringBuilder();
                                mLanguagesList = languages.toArray(new String[languages.size()]);
                                for (String lan : mLanguagesList){
                                    if(stringBuilder.length() > 0){
                                        stringBuilder.append(";");
                                    }
                                    stringBuilder.append(lan);
                                }
                                mLanguagesTV.setText(stringBuilder.toString());
                            }else{
                                mLanguagesList = new String[]{""};
                                mLanguagesTV.setText(R.string.all);
                            }
                        }
                    }catch (Exception ex){
                        Logger.e(TAG, "onActivityResult - Select Language", ex);
                    }

                break;
            }
        }
    }
}
