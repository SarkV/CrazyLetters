package com.avtdev.crazyletters.services;

import android.os.Handler;

import com.avtdev.crazyletters.activities.GameCanvas;
import com.avtdev.crazyletters.models.game.Letter;
import com.avtdev.crazyletters.models.realm.Game;
import com.avtdev.crazyletters.utils.Constants;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameFactory {

    char[] consonants = new char[]{'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'};
    char[] vocals = new char[]{'A', 'E', 'I', 'O', 'U'};
    char[] accentCharacters = new char[]{'À', 'Á', 'Â', 'Ã', 'Ä', 'Ç', 'È', 'É', 'Ê', 'Ë', 'Ì', 'Í', 'Î', 'Ï', 'Ñ', 'Ò', 'Ó', 'Ô', 'Õ', 'Ö', 'Ù', 'Ú', 'Û', 'Ü', 'Ý', 'Ÿ', 'Š', 'Ž'};

    private Game mGame;
    private Random mRandom;
    private GameCanvas mGameCanvas;
    Handler mHandler;
    private boolean finished;

    public GameFactory(Game game, GameCanvas gameCanvas){
        this.mGame = game;
        this.mRandom = new Random();
        this.mGameCanvas = gameCanvas;
        this.finished = false;

        mHandler = new Handler();
        generateWord();
    }

    private void generateWord(){
        char letterChar;
        int v = mRandom.nextInt(14);
        if(mGame.hasAccent() && v == 0){
            letterChar = accentCharacters[mRandom.nextInt(accentCharacters.length)];
        }else if(v < 7){
            letterChar = vocals[mRandom.nextInt(vocals.length)];
        }else{
            letterChar = consonants[mRandom.nextInt(consonants.length)];
        }

        int velocity = 0;
        if(mGame.getVelocity()[1] > mGame.getVelocity()[0]){
            velocity = mRandom.nextInt(mGame.getVelocity()[1] - mGame.getVelocity()[0] + 1) + mGame.getVelocity()[0];
        }else{
            velocity = mGame.getVelocity()[0];
        }

        mGameCanvas.drawLetter(new Letter(
                letterChar,
                mGame.getLettersType()[mRandom.nextInt(mGame.getLettersType().length)],
                mRandom.nextInt(2) == 1,
                mRandom.nextInt(2) == 1,
                velocity));

        if(!this.finished)
            mHandler.postDelayed(() -> generateWord(), Constants.WORD_DELAY );
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
