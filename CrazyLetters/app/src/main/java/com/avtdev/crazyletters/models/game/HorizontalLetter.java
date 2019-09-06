package com.avtdev.crazyletters.models.game;

import android.animation.ValueAnimator;
import android.graphics.Paint;

import com.avtdev.crazyletters.activities.GameCanvas;
import com.avtdev.crazyletters.utils.GameConstants;

import java.util.List;

public class HorizontalLetter extends Letter{

    private boolean leftToRight;
    private float finalPositionX;

    public HorizontalLetter(char letter, boolean leftToRight, int velocity) {
        super(letter, velocity);
        this.leftToRight = leftToRight;
    }

    @Override
    public void setPositions(float minX, float minY, float maxX, float maxY, float width, float height, float position0, List<Letter> letters) {
        super.setPositions(minX, minY, maxX, maxY, width, height, position0, letters);
        if(leftToRight){
            positionX = position0;
            finalPositionX = width;
        }else{
            positionX =  width;
            finalPositionX = position0;
        }
    }

    @Override
    public void startAnimation(GameCanvas gameCanvas) {
        super.startAnimation(gameCanvas);

        ValueAnimator animator = ValueAnimator.ofFloat(positionX, finalPositionX);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            this.positionX = ((float) animation.getAnimatedValue());
            gameCanvas.invalidate();
        });
        gameCanvas.setEndListener(animator, this);
        animator.start();
    }
}
