package com.sina.weibo.sdk.register.mobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import com.sina.weibo.sdk.utils.ResourceManager;
import u.aly.dx;

public class LetterIndexBar extends View {
    public static final int INDEX_COUNT_DEFAULT = 27;
    public static final String SEARCH_ICON_LETTER = "";
    private int count;
    private int mIndex;
    private String[] mIndexLetter;
    private int mItemHeight;
    private int mItemPadding;
    private OnIndexChangeListener mListener;
    private boolean[] mNeedIndex;
    private int mOrgTextSzie;
    private Paint mPaint;
    private RectF mRect;
    private Drawable mSeatchIcon;
    private boolean mTouching;

    public interface OnIndexChangeListener {
        void onIndexChange(int i);
    }

    public LetterIndexBar(Context context) {
        super(context);
        this.mPaint = new Paint();
        this.count = INDEX_COUNT_DEFAULT;
        init();
    }

    public LetterIndexBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaint = new Paint();
        this.count = INDEX_COUNT_DEFAULT;
        init();
    }

    public LetterIndexBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaint = new Paint();
        this.count = INDEX_COUNT_DEFAULT;
        init();
    }

    private void init() {
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setColor(-10658467);
        this.mOrgTextSzie = ResourceManager.dp2px(getContext(), 13);
    }

    public void setIndexMark(boolean[] mark) {
        if (mark != null) {
            this.mNeedIndex = mark;
            invalidate();
        }
    }

    public void setIndexLetter(String[] letter) {
        if (letter != null) {
            this.mIndexLetter = letter;
            this.count = this.mIndexLetter.length;
            this.mIndex = -1;
            invalidate();
        }
    }

    public void setIndexChangeListener(OnIndexChangeListener listener) {
        this.mListener = listener;
    }

    protected void onDraw(Canvas canvas) {
        int textSize;
        super.onDraw(canvas);
        if (this.mTouching) {
            int color = this.mPaint.getColor();
            this.mPaint.setColor(-2005436536);
            canvas.drawRoundRect(this.mRect, (float) (getMeasuredWidth() / 2), (float) (getMeasuredWidth() / 2), this.mPaint);
            this.mPaint.setColor(color);
        }
        if (this.mOrgTextSzie > this.mItemHeight) {
            textSize = this.mItemHeight;
        } else {
            textSize = this.mOrgTextSzie;
        }
        this.mPaint.setTextSize((float) textSize);
        int i;
        int top;
        String title;
        if (this.mIndexLetter == null) {
            char letter = 'A';
            i = 0;
            while (i < this.count) {
                top = (((this.mItemHeight * i) + getPaddingTop()) + textSize) + this.mItemPadding;
                if (this.mNeedIndex == null || this.mNeedIndex[i]) {
                    if (i == this.count - 1) {
                        title = "#";
                    } else {
                        char letter2 = (char) (letter + 1);
                        title = String.valueOf(letter);
                        letter = letter2;
                    }
                    canvas.drawText(title, (float) ((getMeasuredWidth() - ((int) this.mPaint.measureText(title))) / 2), (float) top, this.mPaint);
                }
                i++;
            }
            return;
        }
        i = 0;
        while (i < this.count) {
            top = (((this.mItemHeight * i) + getPaddingTop()) + textSize) + this.mItemPadding;
            if (this.mNeedIndex == null || this.mNeedIndex[i]) {
                title = this.mIndexLetter[i];
                if (title.equals(SEARCH_ICON_LETTER)) {
                    int textWidth = (int) this.mPaint.measureText("M");
                    int left = (getMeasuredWidth() - textWidth) / 2;
                    this.mSeatchIcon.setBounds(left, top - left, textWidth + left, (textWidth + top) - left);
                    this.mSeatchIcon.draw(canvas);
                } else {
                    canvas.drawText(title, (float) ((getMeasuredWidth() - ((int) this.mPaint.measureText(title))) / 2), (float) top, this.mPaint);
                }
            }
            i++;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.mItemHeight = ((height - getPaddingTop()) - getPaddingBottom()) / this.count;
        this.mItemPadding = (int) ((((float) this.mItemHeight) - this.mPaint.getTextSize()) / 2.0f);
        setMeasuredDimension((this.mOrgTextSzie + getPaddingLeft()) + getPaddingRight(), heightMeasureSpec);
        this.mRect = new RectF(0.0f, (float) getPaddingTop(), (float) getMeasuredWidth(), (float) ((height - getPaddingTop()) - getPaddingBottom()));
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case dx.a /*0*/:
            case dx.c /*2*/:
                this.mTouching = true;
                int index = (((int) event.getY()) - getPaddingTop()) / this.mItemHeight;
                if (index != this.mIndex && ((this.mNeedIndex == null || this.mNeedIndex[index]) && index < this.count && index >= 0)) {
                    this.mIndex = index;
                    if (this.mListener != null) {
                        this.mListener.onIndexChange(this.mIndex);
                        break;
                    }
                }
                break;
            case dx.b /*1*/:
            case dx.d /*3*/:
            case dx.e /*4*/:
                this.mTouching = false;
                break;
        }
        invalidate();
        return true;
    }
}
