package com.avtdev.crazyletters.services;

import android.content.Context;
import android.os.Handler;

import com.avtdev.crazyletters.fragments.GameCanvas;
import com.avtdev.crazyletters.models.game.DiagonalLetter;
import com.avtdev.crazyletters.models.game.HorizontalLetter;
import com.avtdev.crazyletters.models.game.ShowHideLetter;
import com.avtdev.crazyletters.models.game.VerticalLetter;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.models.realm.LetterFrequency;
import com.avtdev.crazyletters.utils.Constants;
import com.avtdev.crazyletters.utils.Utils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameFactory {

    HashMap<String, Double> mLettersFrequency;
    private Game mGame;
    private Random mRandom;
    private GameCanvas mGameCanvas;
    Handler mHandler;
    private boolean finished;

    public GameFactory(Game game, GameCanvas gameCanvas, Context context){
        this.mGame = game;
        this.mRandom = new Random();
        this.mGameCanvas = gameCanvas;
        this.finished = false;

        getLetterFrequency(context);

        mHandler = new Handler();
        generateWord();
    }

    private void getLetterFrequency(Context context){
        mLettersFrequency = new HashMap<>();

        List<LetterFrequency> lettersFrequencyTemp = RealmManager.getInstance(context).getLettersFrequency(mGame.getLanguagesString());

        String letter;

        double sum = 0;

        for(LetterFrequency letterFrequency : lettersFrequencyTemp){
            if(mGame.hasAccent()){
                letter = letterFrequency.getLetter();
            }else{
                letter = Normalizer.normalize(letterFrequency.getLetter(), Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            }
            if(!Utils.isNull(letter) && letterFrequency.getFrequency() > 0){
                sum += letterFrequency.getFrequency();
                mLettersFrequency.put(letter, letterFrequency.getFrequency() + mLettersFrequency.getOrDefault(letter, 0.0));
            }
        }

        if(sum > 0){
            double prevValue = 0.0;
            for (Map.Entry<String, Double> values : mLettersFrequency.entrySet()) {
                prevValue += values.getValue() / sum;
                mLettersFrequency.put(values.getKey(), prevValue);
            }
        }
    }

    private void generateWord(){
        char letterChar = 'A';
        double v = mRandom.nextDouble();
        for (Map.Entry<String, Double> values : mLettersFrequency.entrySet()) {
            if(v <= values.getValue()){
                letterChar = values.getKey().charAt(0);
                break;
            }
        }

        int velocity = 0;
        if(mGame.getVelocity()[1] > mGame.getVelocity()[0]){
            velocity = mRandom.nextInt(mGame.getVelocity()[1] - mGame.getVelocity()[0] + 1) + mGame.getVelocity()[0];
        }else{
            velocity = mGame.getVelocity()[0];
        }
        switch (mGame.getLettersType()[mRandom.nextInt(mGame.getLettersType().length)]){
            case HORIZONTAL_MOVE:
                mGameCanvas.drawLetter(new HorizontalLetter(
                        letterChar,
                        mRandom.nextBoolean(),
                        velocity));
                break;
            case VERTICAL_MOVE:
                mGameCanvas.drawLetter(new VerticalLetter(
                        letterChar,
                        mRandom.nextBoolean(),
                        velocity));
                break;
            case DIAGONAL_MOVE:
                mGameCanvas.drawLetter(new DiagonalLetter(
                        letterChar,
                        mRandom.nextBoolean(),
                        mRandom.nextBoolean(),
                        velocity));
                break;
            case SHOW_HIDE:
                mGameCanvas.drawLetter(new ShowHideLetter(
                        letterChar,
                        velocity));
                break;
        }

        if(!this.finished)
            mHandler.postDelayed(() -> generateWord(), Constants.WORD_DELAY );
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
