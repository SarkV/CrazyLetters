package com.avtdev.crazyletters.fragments;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.avtdev.crazyletters.R;
import com.avtdev.crazyletters.models.game.Letter;

import java.util.ArrayList;
import java.util.List;

public class GameCanvas extends View {

    public interface IGameCanvas{
        boolean addLetter(char letter);
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

    boolean mClicked = false;

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
        letter.setPositions(minX, minY, maxX, maxY, width, height, position0, mLetters);
        letter.startAnimation(this);
        mLetters.add(letter);
        invalidate();
    }

    public void setEndListener(Animator animator, Letter letter){
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
        if(!mClicked){
            ArrayList<Letter> lettersAux = new ArrayList<>(mLetters);
            for(Letter letter : lettersAux) {
                if ((x >= letter.getPositionX() - (minX / 2) && x <= letter.getPositionX() + (minX / 2)
                        && y >= letter.getPositionY() - (minY / 2) && y <= letter.getPositionY() + (minY / 2))) {
                    if (listener != null && listener.addLetter(letter.getLetter())) {
                        mLetters.remove(letter);
                        break;
                    }
                }
            }
        }

        switch(event.getAction())
        {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mClicked = false;
                break;
            case MotionEvent.ACTION_DOWN:
                mClicked = true;
                break;
        }
        return true;
    }
}
