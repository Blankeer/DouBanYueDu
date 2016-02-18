package com.douban.book.reader.lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import com.douban.book.reader.R;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;

public class LockView extends CompoundButton {
    private Drawable mButtonDrawable;
    private StatusChangeChecker mStatusChangeChecker;
    private UnlockChecker mUnlockChecker;

    public interface StatusChangeChecker {
        boolean canChangeStatus();
    }

    public interface UnlockChecker {
        boolean canUnlock();
    }

    public LockView(Context context) {
        super(context);
        initView();
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.mButtonDrawable = Res.getDrawable(R.drawable.selector_lock);
        setButtonDrawable(this.mButtonDrawable);
        ViewUtils.setPadding(this, Utils.dp2pixel(10.0f));
        ViewUtils.setEventAware(this);
    }

    public boolean performClick() {
        if ((!isLocked() || canUnlock()) && canChangeStatus()) {
            return super.performClick();
        }
        return false;
    }

    private boolean canUnlock() {
        return this.mUnlockChecker == null || this.mUnlockChecker.canUnlock();
    }

    private boolean canChangeStatus() {
        return this.mStatusChangeChecker == null || this.mStatusChangeChecker.canChangeStatus();
    }

    public void setUnlockChecker(UnlockChecker unlockChecker) {
        this.mUnlockChecker = unlockChecker;
    }

    public void setStatusChangeChecker(StatusChangeChecker statusChangeChecker) {
        this.mStatusChangeChecker = statusChangeChecker;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateDrawableColor();
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        updateDrawableColor();
    }

    public void setLocked(boolean locked) {
        setChecked(locked);
    }

    public boolean isLocked() {
        return isChecked();
    }

    private void updateDrawableColor() {
        if (this.mButtonDrawable != null) {
            this.mButtonDrawable.setColorFilter(Res.getColorStateList(R.array.lock_color).getColorForState(this.mButtonDrawable.getState(), Res.getColor(R.array.blue)), Mode.SRC_IN);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (this.mButtonDrawable != null) {
            setMeasuredDimension(Math.max((this.mButtonDrawable.getIntrinsicWidth() + getPaddingLeft()) + getPaddingRight(), width), Math.max((this.mButtonDrawable.getIntrinsicHeight() + getPaddingTop()) + getPaddingBottom(), height));
        }
    }

    protected void onDraw(Canvas canvas) {
        CanvasUtils.drawDrawableCenteredOnPoint(canvas, this.mButtonDrawable, ((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
    }
}
