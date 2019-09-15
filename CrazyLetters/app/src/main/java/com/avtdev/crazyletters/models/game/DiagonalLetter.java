package com.avtdev.crazyletters.models.game;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;

import com.avtdev.crazyletters.fragments.GameCanvas;
import com.avtdev.crazyletters.utils.Utils;

import java.util.List;
import java.util.Random;

public class DiagonalLetter extends Letter{

    private boolean leftToRight;
    private boolean upToDown;
    private float finalPositionX;
    private float finalPositionY;

    public DiagonalLetter(char letter, boolean leftToRight, boolean upToDown, int velocity) {
        super(letter, velocity);
        this.leftToRight = leftToRight;
        this.upToDown = upToDown;
    }

    @Override
    public void setPositions(float minX, float minY, float maxX, float maxY, float width, float height, float position0, List<Letter> letters) {
        super.setPositions(minX, minY, maxX, maxY, width, height, position0, letters);

        boolean yStart = new Random().nextBoolean();
        boolean yEnd = new Random().nextBoolean();

        if(yStart && yEnd){
            if(upToDown){
                positionY = position0;
                finalPositionY = height;
            }else{
                positionY = height;
                finalPositionY = position0;
            }
            if(leftToRight){
                positionX = Utils.getRandomFloat(minX, maxX);
                finalPositionX = Utils.getRandomFloat(positionX, maxX);
            }else{
                finalPositionX = Utils.getRandomFloat(minX, maxX);
                positionX = Utils.getRandomFloat(finalPositionX, maxX);
            }
        }else if(!yStart && !yEnd){
            if(leftToRight){
                positionX = position0;
                finalPositionX = width;
            }else{
                positionX = width;
                finalPositionX = position0;
            }
            if(upToDown){
                positionY = Utils.getRandomFloat(minY, maxY);
                finalPositionY = Utils.getRandomFloat(positionY, maxY);
            }else{
                finalPositionY = Utils.getRandomFloat(minY , maxY);
                positionY = Utils.getRandomFloat(finalPositionY, maxY);
            }
        }else{
            if(yStart){
                if(upToDown){
                    positionY =  position0;
                    finalPositionY = Utils.getRandomFloat((maxY - minY) / 2, maxY);
                }else{
                    positionY =  height;
                    finalPositionY = Utils.getRandomFloat(minY, (maxY - minY) / 2);
                }
            }else{
                if(leftToRight){
                    positionX =  position0;
                    finalPositionX = Utils.getRandomFloat((maxX - minX) / 2, maxX);
                }else{
                    positionX =  width;
                    finalPositionX = Utils.getRandomFloat(minX, (maxX - minX) / 2);
                }
            }
            if(yEnd){
                if(upToDown){
                    finalPositionY =  height;
                }else{
                    finalPositionY =  position0;
                }
                positionY = Utils.getRandomFloat(minY, (maxY - minY) / 2);
            }else{
                if(leftToRight){
                    finalPositionX =  width;
                }else{
                    finalPositionX =  position0;
                }
                positionX = Utils.getRandomFloat(minX, (maxX - minX) / 2);
            }
        }
    }

    @Override
    public void startAnimation(GameCanvas gameCanvas) {
        super.startAnimation(gameCanvas);

        ValueAnimator animatorX = ValueAnimator.ofFloat(positionX, finalPositionX);
        ValueAnimator animatorY = ValueAnimator.ofFloat(positionY, finalPositionY);

        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(animatorX, animatorY);
        animator.setDuration(duration);
        animatorX.addUpdateListener(animation -> {
            positionX = (float) animation.getAnimatedValue();
            gameCanvas.invalidate();
        });
        animatorY.addUpdateListener(animation -> {
            positionY = (float) animation.getAnimatedValue();
            gameCanvas.invalidate();
        });
        gameCanvas.setEndListener(animator, this);
        animator.start();
    }
}
