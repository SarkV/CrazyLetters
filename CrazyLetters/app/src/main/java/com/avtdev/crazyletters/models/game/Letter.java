package com.avtdev.crazyletters.models.game;

import android.graphics.Paint;

import com.avtdev.crazyletters.utils.GameConstants;

public class Letter {

    char letter;
    GameConstants.LettersType type;
    boolean leftToRight;
    boolean upToDown;
    int velocity;
    float positionX;
    float positionY;
    Paint paint;

    public Letter(char letter, GameConstants.LettersType type, boolean leftToRight, boolean upToDown, int velocity) {
        this.letter = letter;
        this.type = type;
        this.leftToRight = leftToRight;
        this.upToDown = upToDown;
        this.velocity = velocity;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char mLetter) {
        this.letter = mLetter;
    }

    public GameConstants.LettersType getType() {
        return type;
    }

    public void setType(GameConstants.LettersType mType) {
        this.type = mType;
    }

    public boolean isLeftToRight() {
        return leftToRight;
    }

    public void setLeftToRight(boolean leftToRight) {
        this.leftToRight = leftToRight;
    }

    public boolean isUpToDown() {
        return upToDown;
    }

    public void setUpToDown(boolean upToDown) {
        this.upToDown = upToDown;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int mVelocity) {
        this.velocity = mVelocity;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
