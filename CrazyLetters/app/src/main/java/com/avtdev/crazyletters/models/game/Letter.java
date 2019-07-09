package com.avtdev.crazyletters.models.game;

import com.avtdev.crazyletters.utils.GameConstants;

public class Letter {

    char mLetter;
    GameConstants.LettersType mType;
    int mDirection;
    int mVelocity;

    public Letter(char mLetter, GameConstants.LettersType mType, int mDirection) {
        this.mLetter = mLetter;
        this.mType = mType;
        this.mDirection = mDirection;
    }

    public char getLetter() {
        return mLetter;
    }

    public void setLetter(char mLetter) {
        this.mLetter = mLetter;
    }

    public GameConstants.LettersType getType() {
        return mType;
    }

    public void setType(GameConstants.LettersType mType) {
        this.mType = mType;
    }

    public int getDirection() {
        return mDirection;
    }

    public void setDirection(int mDirection) {
        this.mDirection = mDirection;
    }

    public int getVelocity() {
        return mVelocity;
    }

    public void setVelocity(int mVelocity) {
        this.mVelocity = mVelocity;
    }
}
