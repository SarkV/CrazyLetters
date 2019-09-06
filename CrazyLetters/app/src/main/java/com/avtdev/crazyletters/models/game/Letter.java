package com.avtdev.crazyletters.models.game;

import android.graphics.Paint;

import com.avtdev.crazyletters.activities.GameCanvas;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Utils;

import java.util.List;

public class Letter {

    char letter;
    float positionX;
    float positionY;
    Paint paint;
    int duration;

    public Letter(char letter, int velocity) {
        this.letter = letter;
        this.duration = 1500 + ((GameConstants.Velocity[GameConstants.Velocity.length - 1] - velocity) * 250);
    }

    public char getLetter() {
        return letter;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void setPositions(float minX, float minY, float maxX, float maxY, float width, float height, float position0, List<Letter> letterList){
        positionX = Utils.getRandomFloat(minX, maxX);
        positionY = Utils.getRandomFloat(minY, maxY);
    }

    public void startAnimation(GameCanvas gameCanvas){}
}
