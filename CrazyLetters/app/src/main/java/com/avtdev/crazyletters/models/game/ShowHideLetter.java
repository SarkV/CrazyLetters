package com.avtdev.crazyletters.models.game;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Paint;

import com.avtdev.crazyletters.activities.GameCanvas;

import java.util.List;

public class ShowHideLetter extends Letter{

    public ShowHideLetter(char letter, int velocity) {
        super(letter, velocity);
    }

    @Override
    public void setPositions(float minX, float minY, float maxX, float maxY, float width, float height, float position0, List<Letter> letters) {
        super.setPositions(minX, minY, maxX, maxY, width, height, position0, letters);

        for (int i = 0; i < letters.size(); i++){
            Letter l = letters.get(i);
            if( (positionX >= l.getPositionX() - (minX / 2)
                    && positionX <= l.getPositionX() + (minX / 2)
                    && positionY >= l.getPositionY() - (minY / 2)
                    && positionY <= l.getPositionY() + (minY / 2))){
                i = -1;
                super.setPositions(minX, minY, maxX, maxY, width, height, position0, letters);
            }
        }
    }

    @Override
    public void setPaint(Paint paint) {
        super.setPaint(paint);
        paint.setAlpha(0);
    }

    @Override
    public void startAnimation(GameCanvas gameCanvas) {
        super.startAnimation(gameCanvas);

        ValueAnimator animatorHide = ValueAnimator.ofInt(255, 0);
        animatorHide.setDuration(duration);
        animatorHide.addUpdateListener(animation -> {
            paint.setAlpha((int) animation.getAnimatedValue());
            gameCanvas.invalidate();
        });
        gameCanvas.setEndListener(animatorHide, this);
        ValueAnimator animatorShow = ValueAnimator.ofInt(0, 255);
        animatorShow.setDuration(duration);
        animatorShow.addUpdateListener(animation -> {
            paint.setAlpha((int) animation.getAnimatedValue());
            gameCanvas.invalidate();
        });
        animatorShow.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatorHide.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animatorHide.start();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorShow.start();
    }
}
