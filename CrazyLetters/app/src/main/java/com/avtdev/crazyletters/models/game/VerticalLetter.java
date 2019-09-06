package com.avtdev.crazyletters.models.game;

import android.animation.ValueAnimator;

import com.avtdev.crazyletters.activities.GameCanvas;

import java.util.List;

public class VerticalLetter extends Letter{

    private boolean upToDown;
    private float finalPositionY;

    public VerticalLetter(char letter, boolean upToDown, int velocity) {
        super(letter, velocity);
        this.upToDown = upToDown;
    }

    @Override
    public void setPositions(float minX, float minY, float maxX, float maxY, float width, float height, float position0, List<Letter> letters) {
        super.setPositions(minX, minY, maxX, maxY, width, height, position0, letters);
        if(upToDown){
            positionY = position0;
            finalPositionY = height;
        }else{
            positionY =  height;
            finalPositionY = position0;
        }
    }

    @Override
    public void startAnimation(GameCanvas gameCanvas) {
        super.startAnimation(gameCanvas);

        ValueAnimator animator = ValueAnimator.ofFloat(positionY, finalPositionY);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            this.positionY = ((float) animation.getAnimatedValue());
            gameCanvas.invalidate();
        });
        gameCanvas.setEndListener(animator, this);
        animator.start();
    }
}
