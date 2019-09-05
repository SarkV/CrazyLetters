package com.avtdev.crazyletters.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.game.Letter;
import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Logger;
import com.avtdev.crazyletters.utils.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameCanvas extends View {

    public interface IGameCanvas{
        void addLetter(char letter);
    }

    private static final String TAG = "GameCanvas";

    private float width;
    private float height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float mTextSize;
    private Paint mPaint;
    Context context;
    IGameCanvas listener;
    float minX, minY, maxX, maxY, position0;

    List<Letter> mLetters;

    public GameCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        mLetters = new ArrayList<>();

        mTextSize = context.getResources().getDimension(R.dimen.game_letters_size);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(context.getColor(R.color.colorSecondaryLight));
    }

    public void setListener(IGameCanvas listener) {
        this.listener = listener;
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        minX = mTextSize + 5;
        minY = mTextSize + 5;
        maxX = mCanvas.getWidth() - (mTextSize + 5);
        maxY = mCanvas.getHeight() - (mTextSize + 5);
        width = mTextSize + mCanvas.getWidth();
        height = mTextSize + mCanvas.getHeight();
        position0 = -(mTextSize + 5);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(Letter l : mLetters){
            canvas.drawText(String.valueOf(l.getLetter()), l.getPositionX(), l.getPositionY(), l.getPaint());
        }
    }

    public void drawLetter(Letter letter){

        letter.setPaint(new Paint(mPaint));

        int duration = 1500 + ((GameConstants.Velocity[GameConstants.Velocity.length - 1] - letter.getVelocity()) * 250);

        letter.setPositionX(Utils.getRandomFloat(minX, maxX));
        letter.setPositionY(Utils.getRandomFloat(minY, maxY));

        if(letter.getType() == GameConstants.LettersType.VERTICAL_MOVE || letter.getType() == GameConstants.LettersType.HORIZONTAL_MOVE){
            setHorVerMove(letter, duration);
        }else if(letter.getType() == GameConstants.LettersType.DIAGONAL_MOVE){
            setDiaMove(letter, duration);
        }else if(letter.getType() == GameConstants.LettersType.SHOW_HIDE){
            setHideShowMove(letter, duration);
        }

        mLetters.add(letter);
        invalidate();
    }

    private void setHorVerMove(Letter letter, int duration){
        float from = 0f, to = 0f;


        if(letter.getType() == GameConstants.LettersType.HORIZONTAL_MOVE){
            if(letter.isLeftToRight()){
                from = position0;
                to = width;
            }else{
                from = width;
                to = position0;
            }
            letter.setPositionX(from);
        }else if(letter.getType() == GameConstants.LettersType.VERTICAL_MOVE){
            if(letter.isUpToDown()){
                from = position0;
                to = height;
            }else{
                from = height;
                to = position0;
            }
            letter.setPositionY(from);
        }

        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            if(letter.getType() == GameConstants.LettersType.VERTICAL_MOVE){
                letter.setPositionY((float) animation.getAnimatedValue());
            }else{
                letter.setPositionX((float) animation.getAnimatedValue());
            }
            invalidate();
        });
        setEndListener(animator, letter);
        animator.start();
    }

    private void setDiaMove(Letter letter, int duration){
        float fromX = 0f;
        float fromY = 0f;
        float toX = 0f;
        float toY = 0f;

        boolean yStart = new Random().nextBoolean();
        boolean yEnd = new Random().nextBoolean();

        if(yStart && yEnd){
            if(letter.isUpToDown()){
                fromY = position0;
                toY = height;
            }else{
                fromY = height;
                toY = position0;
            }
            if(letter.isLeftToRight()){
                fromX = Utils.getRandomFloat(minX, maxX);
                toX = Utils.getRandomFloat(fromX, maxX);
            }else{
                toX = Utils.getRandomFloat(minX, maxX);
                fromX = Utils.getRandomFloat(toX, maxX);
            }
        }else if(!yStart && !yEnd){
            if(letter.isLeftToRight()){
                fromX = position0;
                toX = width;
            }else{
                fromX = width;
                toX = position0;
            }
            if(letter.isUpToDown()){
                fromY = Utils.getRandomFloat(minY, maxY);
                toY = Utils.getRandomFloat(fromY, maxY);
            }else{
                toY = Utils.getRandomFloat(minY , maxY);
                fromY = Utils.getRandomFloat(toY, maxY);
            }
        }else{
            if(yStart){
                if(letter.isUpToDown()){
                    fromY =  position0;
                    toY = Utils.getRandomFloat((maxY - minY) / 2, maxY);
                }else{
                    fromY =  height;
                    toY = Utils.getRandomFloat(minY, (maxY - minY) / 2);
                }
            }else{
                if(letter.isLeftToRight()){
                    fromX =  position0;
                    toX = Utils.getRandomFloat((maxX - minX) / 2, maxX);
                }else{
                    fromX =  width;
                    toX = Utils.getRandomFloat(minX, (maxX - minX) / 2);
                }
            }
            if(yEnd){
                if(letter.isUpToDown()){
                    toY =  height;
                }else{
                    toY =  position0;
                }
                fromY = Utils.getRandomFloat(minY, (maxY - minY) / 2);
            }else{
                if(letter.isLeftToRight()){
                    toX =  width;
                }else{
                    toX =  position0;
                }
                fromX = Utils.getRandomFloat(minX, (maxX - minX) / 2);
            }
        }
        letter.setPositionX(fromX);
        letter.setPositionY(fromY);

        ValueAnimator animatorX = ValueAnimator.ofFloat(fromX, toX);
        ValueAnimator animatorY = ValueAnimator.ofFloat(fromY, toY);

        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(animatorX, animatorY);
        animator.setDuration(duration);
        animatorX.addUpdateListener(animation -> {
            letter.setPositionX((float) animation.getAnimatedValue());
            invalidate();
        });
        animatorY.addUpdateListener(animation -> {
            letter.setPositionY((float) animation.getAnimatedValue());
            invalidate();
        });
        setEndListener(animator, letter);
        animator.start();
    }

    private void setHideShowMove(Letter letter, int duration){
        for (int i = 0; i < mLetters.size(); i++){
            if( (letter.getPositionX() >= mLetters.get(i).getPositionX() - (mTextSize / 2)
                    && letter.getPositionX() <= mLetters.get(i).getPositionX() + (mTextSize / 2)
                    && letter.getPositionY() >= mLetters.get(i).getPositionY() - (mTextSize / 2)
                    && mLetters.get(i).getPositionY() <= mLetters.get(i).getPositionY() + (mTextSize / 2))){
                i = -1;
                letter.setPositionX(minX + (new Random().nextFloat() * (maxX - minX)));
                letter.setPositionY(minY + (new Random().nextFloat() * (maxY - minY)));
            }
        }


        letter.getPaint().setAlpha(0);
        ValueAnimator animatorHide = ValueAnimator.ofInt(255, 0);
        animatorHide.setDuration(duration);
        animatorHide.addUpdateListener(animation -> {
            letter.getPaint().setAlpha((int) animation.getAnimatedValue());
            invalidate();
        });
        setEndListener(animatorHide, letter);
        ValueAnimator animatorShow = ValueAnimator.ofInt(0, 255);
        animatorShow.setDuration(duration);
        animatorShow.addUpdateListener(animation -> {
            letter.getPaint().setAlpha((int) animation.getAnimatedValue());
            invalidate();
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

    private void setEndListener(Animator animator, Letter letter){
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLetters.remove(letter);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mLetters.remove(letter);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();
        ArrayList<Letter> lettersAux = new ArrayList<>(mLetters);
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                for(Letter letter : lettersAux){
                    if( (x >= letter.getPositionX() - (mTextSize / 2) && x <= letter.getPositionX() + (mTextSize / 2)
                    && y >= letter.getPositionY() - (mTextSize / 2) && y <= letter.getPositionY() + (mTextSize / 2)))
                    {
                        if(listener != null)
                            listener.addLetter(letter.getLetter());
                        mLetters.remove(letter);
                        return true;
                    }

                }
        }
        return false;
    }
}
