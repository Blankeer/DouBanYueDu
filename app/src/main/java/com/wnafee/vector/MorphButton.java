package com.wnafee.vector;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.widget.CompoundButton;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import com.wnafee.vector.compat.DrawableCompat;
import com.wnafee.vector.compat.ResourcesCompat;
import com.wnafee.vector.compat.Tintable;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class MorphButton extends CompoundButton {
    public static final String TAG;
    private static final ScaleToFit[] sS2FArray;
    boolean mAdjustViewBounds;
    boolean mAdjustViewBoundsCompat;
    TintInfo mBackgroundTint;
    boolean mCropToPadding;
    Drawable mCurrentDrawable;
    int mCurrentDrawableHeight;
    int mCurrentDrawableWidth;
    private Matrix mDrawMatrix;
    boolean mEndCanMorph;
    Drawable mEndDrawable;
    int mEndDrawableHeight;
    int mEndDrawableWidth;
    TintInfo mForegroundTint;
    boolean mHasStarted;
    boolean mHaveFrame;
    boolean mIsToggling;
    private Matrix mMatrix;
    private ScaleType mScaleType;
    boolean mStartCanMorph;
    Drawable mStartDrawable;
    int mStartDrawableHeight;
    int mStartDrawableWidth;
    MorphState mState;
    private OnStateChangedListener mStateListener;
    private RectF mTempDst;
    private RectF mTempSrc;

    public enum MorphState {
        START,
        END
    }

    public interface OnStateChangedListener {
        void onStateChanged(MorphState morphState, boolean z);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        MorphState state;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.state = (MorphState) in.readValue(null);
        }

        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(this.state);
        }

        public String toString() {
            return "MorphButton.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " state=" + this.state + "}";
        }

        static {
            CREATOR = new Creator<SavedState>() {
                public SavedState createFromParcel(Parcel in) {
                    return new SavedState(null);
                }

                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            };
        }
    }

    public enum ScaleType {
        MATRIX(0),
        FIT_XY(1),
        FIT_START(2),
        FIT_CENTER(3),
        FIT_END(4),
        CENTER(5),
        CENTER_CROP(6),
        CENTER_INSIDE(7);
        
        final int nativeInt;

        private ScaleType(int ni) {
            this.nativeInt = ni;
        }
    }

    private static class TintInfo {
        boolean mHasTintList;
        boolean mHasTintMode;
        ColorStateList mTintList;
        Mode mTintMode;

        private TintInfo() {
        }
    }

    static {
        TAG = MorphButton.class.getSimpleName();
        sS2FArray = new ScaleToFit[]{ScaleToFit.FILL, ScaleToFit.START, ScaleToFit.CENTER, ScaleToFit.END};
    }

    public MorphButton(Context context) {
        this(context, null);
    }

    public MorphButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.morphButtonStyle);
    }

    @TargetApi(21)
    public MorphButton(Context context, AttributeSet attrs, int defStyleAttr) {
        boolean z;
        super(context, attrs, defStyleAttr);
        this.mState = MorphState.START;
        this.mStartDrawable = null;
        this.mEndDrawable = null;
        this.mStartCanMorph = false;
        this.mEndCanMorph = false;
        this.mIsToggling = false;
        this.mHasStarted = false;
        this.mCropToPadding = false;
        this.mAdjustViewBounds = false;
        if (VERSION.SDK_INT <= 17) {
            z = true;
        } else {
            z = false;
        }
        this.mAdjustViewBoundsCompat = z;
        this.mHaveFrame = false;
        this.mDrawMatrix = null;
        this.mTempSrc = new RectF();
        this.mTempDst = new RectF();
        initMorphButton();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MorphButton, defStyleAttr, 0);
        int startResId = a.getResourceId(R.styleable.MorphButton_vc_startDrawable, -1);
        int endResId = a.getResourceId(R.styleable.MorphButton_vc_endDrawable, -1);
        boolean autoStart = a.getBoolean(R.styleable.MorphButton_vc_autoStartAnimation, false);
        int st = a.getInt(R.styleable.MorphButton_android_scaleType, -1);
        if (st >= 0) {
            setScaleType(getScaleTypeFromInt(st));
        }
        readTintAttributes(a);
        a.recycle();
        applyBackgroundTint();
        setClickable(true);
        setStartDrawable(startResId, false);
        setEndDrawable(endResId, false);
        setState(this.mState);
        if (autoStart) {
            this.mHasStarted = true;
            setState(MorphState.END, true);
        }
    }

    private void initMorphButton() {
        this.mMatrix = new Matrix();
        this.mScaleType = ScaleType.FIT_CENTER;
    }

    private boolean isMorphable(Drawable d) {
        return d != null && (d instanceof Animatable);
    }

    public void setOnStateChangedListener(OnStateChangedListener l) {
        if (l != null && l != this.mStateListener) {
            this.mStateListener = l;
        }
    }

    public void toggle() {
        this.mHasStarted = true;
        this.mIsToggling = true;
        setState(this.mState == MorphState.START ? MorphState.END : MorphState.START, true);
        super.toggle();
        this.mIsToggling = false;
    }

    private void updateDrawable(Drawable d, MorphState state) {
        Drawable oldD = state == MorphState.START ? this.mStartDrawable : this.mEndDrawable;
        if (oldD != null) {
            oldD.setCallback(null);
            unscheduleDrawable(oldD);
        }
        if (state == MorphState.START) {
            this.mStartDrawable = d;
            this.mStartCanMorph = isMorphable(d);
        } else {
            this.mEndDrawable = d;
            this.mEndCanMorph = isMorphable(d);
        }
        if (d != null) {
            boolean z;
            int width;
            int height;
            d.setCallback(this);
            if (d.isStateful()) {
                d.setState(getDrawableState());
            }
            if (getVisibility() == 0) {
                z = true;
            } else {
                z = false;
            }
            d.setVisible(z, true);
            d.setLevel(0);
            if (state == MorphState.START) {
                width = d.getIntrinsicWidth();
                this.mStartDrawableWidth = width;
                height = d.getIntrinsicHeight();
                this.mStartDrawableHeight = height;
            } else {
                width = d.getIntrinsicWidth();
                this.mEndDrawableWidth = width;
                height = d.getIntrinsicHeight();
                this.mEndDrawableHeight = height;
            }
            applyForegroundTint(d);
            configureBounds(d, width, height);
        } else if (state == MorphState.START) {
            this.mStartDrawableHeight = -1;
            this.mStartDrawableWidth = -1;
        } else {
            this.mEndDrawableHeight = -1;
            this.mEndDrawableWidth = -1;
        }
    }

    public void refreshDrawableState() {
        super.refreshDrawableState();
        refreshCurrentDrawable();
    }

    private void refreshCurrentDrawable() {
        if (this.mCurrentDrawable != null) {
            this.mCurrentDrawable.setState(getDrawableState());
        }
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mCurrentDrawable != null) {
            this.mCurrentDrawable.jumpToCurrentState();
        }
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        resizeFromDrawable(this.mState);
    }

    private void resizeFromDrawable(MorphState state) {
        int width = state == MorphState.START ? this.mStartDrawableWidth : this.mEndDrawableWidth;
        int height = state == MorphState.START ? this.mStartDrawableHeight : this.mEndDrawableHeight;
        Drawable d = state == MorphState.START ? this.mStartDrawable : this.mEndDrawable;
        if (d != null) {
            int w = d.getIntrinsicWidth();
            if (w < 0) {
                w = width;
            }
            int h = d.getIntrinsicHeight();
            if (h < 0) {
                h = height;
            }
            if (w != width || h != height) {
                if (state == MorphState.START) {
                    this.mStartDrawableWidth = w;
                    this.mStartDrawableHeight = h;
                } else {
                    this.mEndDrawableWidth = w;
                    this.mEndDrawableHeight = h;
                }
                requestLayout();
            }
        }
    }

    public void setStartDrawable(int rId) {
        setStartDrawable(rId, true);
    }

    private void setStartDrawable(int rId, boolean refreshState) {
        if (rId > 0) {
            setStartDrawable(ResourcesCompat.getDrawable(getContext(), rId), refreshState);
        }
    }

    public void setStartDrawable(Drawable d) {
        setStartDrawable(d, true);
    }

    private void setStartDrawable(Drawable d, boolean refreshState) {
        if (this.mStartDrawable != d) {
            updateDrawable(d, MorphState.START);
            if (refreshState) {
                setState(this.mState);
            }
        }
    }

    public void setEndDrawable(int rId) {
        setEndDrawable(rId, true);
    }

    private void setEndDrawable(int rId, boolean refreshState) {
        if (rId > 0) {
            setEndDrawable(ResourcesCompat.getDrawable(getContext(), rId), refreshState);
        }
    }

    public void setEndDrawable(Drawable d) {
        setEndDrawable(d, true);
    }

    private void setEndDrawable(Drawable d, boolean refreshState) {
        if (this.mEndDrawable != d) {
            updateDrawable(d, MorphState.END);
            if (refreshState) {
                setState(this.mState);
            }
        }
    }

    public MorphState getState() {
        return this.mState;
    }

    private void setCurrentDrawable(Drawable d, int width, int height) {
        if (this.mCurrentDrawable != d) {
            this.mCurrentDrawable = d;
            Rect r = d.getBounds();
            int boundsWidth = r.right - r.left;
            int boundsHeight = r.bottom - r.top;
            if (!(this.mCurrentDrawableWidth == width && this.mCurrentDrawableHeight == height && boundsWidth == width && boundsHeight == height)) {
                requestLayout();
            }
            this.mCurrentDrawableWidth = width;
            this.mCurrentDrawableHeight = height;
        }
    }

    public void setState(MorphState state) {
        setState(state, false);
    }

    public void setState(MorphState state, boolean animate) {
        boolean checked;
        if (state == MorphState.START) {
            checked = false;
            setCurrentDrawable(this.mEndCanMorph ? this.mEndDrawable : this.mStartDrawable, this.mEndCanMorph ? this.mEndDrawableWidth : this.mStartDrawableWidth, this.mEndCanMorph ? this.mEndDrawableHeight : this.mStartDrawableHeight);
            beginEndAnimation();
            if (!animate) {
                endEndAnimation();
            }
        } else {
            checked = true;
            setCurrentDrawable(this.mStartCanMorph ? this.mStartDrawable : this.mEndDrawable, this.mStartCanMorph ? this.mStartDrawableWidth : this.mEndDrawableWidth, this.mStartCanMorph ? this.mStartDrawableHeight : this.mEndDrawableHeight);
            beginStartAnimation();
            if (!animate) {
                endStartAnimation();
            }
        }
        if (this.mState != state || !this.mHasStarted) {
            super.setChecked(checked);
            this.mState = state;
            if (this.mStateListener != null) {
                this.mStateListener.onStateChanged(state, animate);
            }
        }
    }

    public void setChecked(boolean checked) {
        if (!this.mIsToggling) {
            setState(checked ? MorphState.END : MorphState.START);
        }
    }

    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (this.mCurrentDrawable != null && this.mCurrentDrawableWidth != 0 && this.mCurrentDrawableHeight != 0) {
            int paddingTop = getPaddingTop();
            int paddingLeft = getPaddingLeft();
            int paddingBottom = getPaddingBottom();
            int paddingRight = getPaddingRight();
            int top = getTop();
            int bottom = getBottom();
            int left = getLeft();
            int right = getRight();
            if (this.mDrawMatrix == null && paddingTop == 0 && paddingLeft == 0) {
                this.mCurrentDrawable.draw(canvas);
                return;
            }
            int saveCount = canvas.getSaveCount();
            canvas.save();
            if (this.mCropToPadding) {
                int scrollX = getScrollX();
                int scrollY = getScrollY();
                canvas.clipRect(scrollX + paddingLeft, scrollY + paddingTop, ((scrollX + right) - left) - paddingRight, ((scrollY + bottom) - top) - paddingBottom);
            }
            canvas.translate((float) paddingLeft, (float) paddingTop);
            if (this.mDrawMatrix != null) {
                canvas.concat(this.mDrawMatrix);
            }
            this.mCurrentDrawable.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    @TargetApi(21)
    public void setBackgroundDrawable(Drawable background) {
        if (ResourcesCompat.LOLLIPOP) {
            if (this.mBackgroundTint != null) {
                if (this.mBackgroundTint.mHasTintList) {
                    super.setBackgroundTintList(this.mBackgroundTint.mTintList);
                }
                if (this.mBackgroundTint.mHasTintMode) {
                    super.setBackgroundTintMode(this.mBackgroundTint.mTintMode);
                }
            }
            super.setBackgroundDrawable(background);
            return;
        }
        super.setBackgroundDrawable(background);
        applyBackgroundTint();
    }

    public ColorStateList getBackgroundTintList() {
        if (ResourcesCompat.LOLLIPOP) {
            return getBackgroundTintList();
        }
        return this.mBackgroundTint != null ? this.mBackgroundTint.mTintList : null;
    }

    public ColorStateList getForegroundTintList() {
        return this.mForegroundTint != null ? this.mForegroundTint.mTintList : null;
    }

    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        if (ResourcesCompat.LOLLIPOP) {
            super.setBackgroundTintList(tint);
        }
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintList = tint;
        this.mBackgroundTint.mHasTintList = true;
        if (!ResourcesCompat.LOLLIPOP) {
            applyBackgroundTint();
        }
    }

    public void setForegroundTintList(@Nullable ColorStateList tint) {
        if (this.mForegroundTint == null) {
            this.mForegroundTint = new TintInfo();
        }
        this.mForegroundTint.mTintList = tint;
        this.mForegroundTint.mHasTintList = true;
        applyForegroundTint();
    }

    public Mode getForegroundTintMode() {
        return this.mForegroundTint != null ? this.mForegroundTint.mTintMode : null;
    }

    public Mode getBackgroundTintMode() {
        if (ResourcesCompat.LOLLIPOP) {
            return getBackgroundTintMode();
        }
        return this.mBackgroundTint != null ? this.mBackgroundTint.mTintMode : null;
    }

    public void setBackgroundTintMode(@Nullable Mode tintMode) {
        if (ResourcesCompat.LOLLIPOP) {
            super.setBackgroundTintMode(tintMode);
        }
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintMode = tintMode;
        this.mBackgroundTint.mHasTintMode = true;
        if (!ResourcesCompat.LOLLIPOP) {
            applyBackgroundTint();
        }
    }

    public void setForegroundTintMode(@Nullable Mode tintMode) {
        if (this.mForegroundTint == null) {
            this.mForegroundTint = new TintInfo();
        }
        this.mForegroundTint.mTintMode = tintMode;
        this.mForegroundTint.mHasTintMode = true;
        applyForegroundTint();
    }

    private void setDrawableColorFilter(Drawable d, int color, Mode mode) {
        if (d != null) {
            d.setColorFilter(color, mode);
        }
    }

    public void setForegroundColorFilter(int color, Mode mode) {
        if (this.mStartDrawable != null) {
            this.mStartDrawable.setColorFilter(color, mode);
        }
        if (this.mEndDrawable != null) {
            this.mEndDrawable.setColorFilter(color, mode);
        }
    }

    public void setBackgroundColorFilter(int color, Mode mode) {
        setDrawableColorFilter(getBackground(), color, mode);
    }

    private void applyForegroundTint(Drawable d) {
        applyTint(d, this.mForegroundTint);
    }

    private void applyForegroundTint() {
        applyTint(this.mStartDrawable, this.mForegroundTint);
        applyTint(this.mEndDrawable, this.mForegroundTint);
    }

    private void applyBackgroundTint() {
        applyTint(getBackground(), this.mBackgroundTint);
    }

    @TargetApi(21)
    private void applyTint(Drawable d, TintInfo t) {
        if (d != null && t != null) {
            if (ResourcesCompat.LOLLIPOP) {
                if (t.mHasTintList || t.mHasTintMode) {
                    d = d.mutate();
                    if (t.mHasTintList) {
                        d.setTintList(t.mTintList);
                    }
                    if (t.mHasTintMode) {
                        d.setTintMode(t.mTintMode);
                    }
                }
            } else if (d instanceof Tintable) {
                if (t.mHasTintList || t.mHasTintMode) {
                    Tintable tintable = (Tintable) d.mutate();
                    if (t.mHasTintList) {
                        tintable.setTintList(t.mTintList);
                    }
                    if (t.mHasTintMode) {
                        tintable.setTintMode(t.mTintMode);
                    }
                }
            } else if (t.mHasTintList) {
                setDrawableColorFilter(d, t.mTintList.getColorForState(getDrawableState(), 0), Mode.SRC_IN);
            }
        }
    }

    private void readTintAttributes(TypedArray a) {
        boolean z;
        boolean z2 = true;
        this.mBackgroundTint = new TintInfo();
        this.mForegroundTint = new TintInfo();
        this.mBackgroundTint.mTintList = a.getColorStateList(R.styleable.MorphButton_vc_backgroundTint);
        this.mBackgroundTint.mHasTintList = this.mBackgroundTint.mTintList != null;
        this.mBackgroundTint.mTintMode = DrawableCompat.parseTintMode(a.getInt(R.styleable.MorphButton_vc_backgroundTintMode, -1), null);
        TintInfo tintInfo = this.mBackgroundTint;
        if (this.mBackgroundTint.mTintMode != null) {
            z = true;
        } else {
            z = false;
        }
        tintInfo.mHasTintMode = z;
        this.mForegroundTint.mTintList = a.getColorStateList(R.styleable.MorphButton_vc_foregroundTint);
        tintInfo = this.mForegroundTint;
        if (this.mForegroundTint.mTintList != null) {
            z = true;
        } else {
            z = false;
        }
        tintInfo.mHasTintList = z;
        this.mForegroundTint.mTintMode = DrawableCompat.parseTintMode(a.getInt(R.styleable.MorphButton_vc_foregroundTintMode, -1), null);
        TintInfo tintInfo2 = this.mForegroundTint;
        if (this.mForegroundTint.mTintMode == null) {
            z2 = false;
        }
        tintInfo2.mHasTintMode = z2;
    }

    @NonNull
    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.state = getState();
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setState(ss.state, false);
        requestLayout();
    }

    private boolean beginStartAnimation() {
        if (this.mStartDrawable == null || !this.mStartCanMorph) {
            return false;
        }
        ((Animatable) this.mStartDrawable).start();
        return true;
    }

    private boolean endStartAnimation() {
        if (this.mStartDrawable == null || !this.mStartCanMorph) {
            return false;
        }
        ((Animatable) this.mStartDrawable).stop();
        return true;
    }

    private boolean beginEndAnimation() {
        if (this.mEndDrawable == null || !this.mEndCanMorph) {
            return false;
        }
        ((Animatable) this.mEndDrawable).start();
        return true;
    }

    private boolean endEndAnimation() {
        if (this.mEndDrawable == null || !this.mEndCanMorph) {
            return false;
        }
        ((Animatable) this.mEndDrawable).stop();
        return true;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h;
        int w;
        int widthSize;
        int heightSize;
        float desiredAspect = 0.0f;
        boolean resizeWidth = false;
        boolean resizeHeight = false;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        if (this.mCurrentDrawable == null) {
            this.mCurrentDrawableWidth = -1;
            this.mCurrentDrawableHeight = -1;
            h = 0;
            w = 0;
        } else {
            w = this.mCurrentDrawableWidth;
            h = this.mCurrentDrawableHeight;
            if (w <= 0) {
                w = 1;
            }
            if (h <= 0) {
                h = 1;
            }
            if (this.mAdjustViewBounds) {
                resizeWidth = widthSpecMode != 1073741824;
                resizeHeight = heightSpecMode != 1073741824;
                desiredAspect = ((float) w) / ((float) h);
            }
        }
        int pleft = getPaddingLeft();
        int pright = getPaddingRight();
        int ptop = getPaddingTop();
        int pbottom = getPaddingBottom();
        if (resizeWidth || resizeHeight) {
            widthSize = resolveAdjustedSize((w + pleft) + pright, AdvancedShareActionProvider.WEIGHT_MAX, widthMeasureSpec);
            heightSize = resolveAdjustedSize((h + ptop) + pbottom, AdvancedShareActionProvider.WEIGHT_MAX, heightMeasureSpec);
            if (desiredAspect != 0.0f) {
                if (((double) Math.abs((((float) ((widthSize - pleft) - pright)) / ((float) ((heightSize - ptop) - pbottom))) - desiredAspect)) > 1.0E-7d) {
                    boolean done = false;
                    if (resizeWidth) {
                        int newWidth = (((int) (((float) ((heightSize - ptop) - pbottom)) * desiredAspect)) + pleft) + pright;
                        if (!(resizeHeight || this.mAdjustViewBoundsCompat)) {
                            widthSize = resolveAdjustedSize(newWidth, AdvancedShareActionProvider.WEIGHT_MAX, widthMeasureSpec);
                        }
                        if (newWidth <= widthSize) {
                            widthSize = newWidth;
                            done = true;
                        }
                    }
                    if (!done && resizeHeight) {
                        int newHeight = (((int) (((float) ((widthSize - pleft) - pright)) / desiredAspect)) + ptop) + pbottom;
                        if (!(resizeWidth || this.mAdjustViewBoundsCompat)) {
                            heightSize = resolveAdjustedSize(newHeight, AdvancedShareActionProvider.WEIGHT_MAX, heightMeasureSpec);
                        }
                        if (newHeight <= heightSize) {
                            heightSize = newHeight;
                        }
                    }
                }
            }
        } else {
            h += ptop + pbottom;
            w = Math.max(w + (pleft + pright), getSuggestedMinimumWidth());
            h = Math.max(h, getSuggestedMinimumHeight());
            widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
            heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    private int resolveAdjustedSize(int desiredSize, int maxSize, int measureSpec) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case ExploreByTouchHelper.INVALID_ID /*-2147483648*/:
                return Math.min(Math.min(desiredSize, specSize), maxSize);
            case dx.a /*0*/:
                return Math.min(desiredSize, maxSize);
            case 1073741824:
                return specSize;
            default:
                return result;
        }
    }

    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        this.mHaveFrame = true;
        configureBounds();
        return changed;
    }

    private void configureBounds() {
        configureBounds(this.mCurrentDrawable, this.mCurrentDrawableWidth, this.mCurrentDrawableHeight);
    }

    private void configureBounds(Drawable d, int dwidth, int dheight) {
        if (d != null && this.mHaveFrame) {
            boolean fits;
            int vwidth = (getWidth() - getPaddingLeft()) - getPaddingRight();
            int vheight = (getHeight() - getPaddingTop()) - getPaddingBottom();
            if ((dwidth < 0 || vwidth == dwidth) && (dheight < 0 || vheight == dheight)) {
                fits = true;
            } else {
                fits = false;
            }
            if (dwidth <= 0 || dheight <= 0 || ScaleType.FIT_XY == this.mScaleType) {
                d.setBounds(0, 0, vwidth, vheight);
                this.mDrawMatrix = null;
                return;
            }
            d.setBounds(0, 0, dwidth, dheight);
            if (ScaleType.MATRIX == this.mScaleType) {
                if (this.mMatrix.isIdentity()) {
                    this.mDrawMatrix = null;
                } else {
                    this.mDrawMatrix = this.mMatrix;
                }
            } else if (fits) {
                this.mDrawMatrix = null;
            } else if (ScaleType.CENTER == this.mScaleType) {
                this.mDrawMatrix = this.mMatrix;
                this.mDrawMatrix.setTranslate((float) ((int) ((((float) (vwidth - dwidth)) * 0.5f) + 0.5f)), (float) ((int) ((((float) (vheight - dheight)) * 0.5f) + 0.5f)));
            } else if (ScaleType.CENTER_CROP == this.mScaleType) {
                this.mDrawMatrix = this.mMatrix;
                dx = 0.0f;
                dy = 0.0f;
                if (dwidth * vheight > vwidth * dheight) {
                    scale = ((float) vheight) / ((float) dheight);
                    dx = (((float) vwidth) - (((float) dwidth) * scale)) * 0.5f;
                } else {
                    scale = ((float) vwidth) / ((float) dwidth);
                    dy = (((float) vheight) - (((float) dheight) * scale)) * 0.5f;
                }
                this.mDrawMatrix.setScale(scale, scale);
                this.mDrawMatrix.postTranslate((float) ((int) (dx + 0.5f)), (float) ((int) (dy + 0.5f)));
            } else if (ScaleType.CENTER_INSIDE == this.mScaleType) {
                this.mDrawMatrix = this.mMatrix;
                if (dwidth > vwidth || dheight > vheight) {
                    scale = Math.min(((float) vwidth) / ((float) dwidth), ((float) vheight) / ((float) dheight));
                } else {
                    scale = 1.0f;
                }
                dx = (float) ((int) (((((float) vwidth) - (((float) dwidth) * scale)) * 0.5f) + 0.5f));
                dy = (float) ((int) (((((float) vheight) - (((float) dheight) * scale)) * 0.5f) + 0.5f));
                this.mDrawMatrix.setScale(scale, scale);
                this.mDrawMatrix.postTranslate(dx, dy);
            } else {
                this.mTempSrc.set(0.0f, 0.0f, (float) dwidth, (float) dheight);
                this.mTempDst.set(0.0f, 0.0f, (float) vwidth, (float) vheight);
                this.mDrawMatrix = this.mMatrix;
                this.mDrawMatrix.setRectToRect(this.mTempSrc, this.mTempDst, scaleTypeToScaleToFit(this.mScaleType));
            }
        }
    }

    private static ScaleToFit scaleTypeToScaleToFit(ScaleType st) {
        return sS2FArray[st.nativeInt - 1];
    }

    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            throw new NullPointerException();
        } else if (this.mScaleType != scaleType) {
            this.mScaleType = scaleType;
            setWillNotCacheDrawing(this.mScaleType == ScaleType.CENTER);
            requestLayout();
            invalidate();
        }
    }

    private ScaleType getScaleTypeFromInt(int i) {
        switch (i) {
            case dx.a /*0*/:
                return ScaleType.MATRIX;
            case dx.b /*1*/:
                return ScaleType.FIT_XY;
            case dx.c /*2*/:
                return ScaleType.FIT_START;
            case dx.d /*3*/:
                return ScaleType.FIT_CENTER;
            case dx.e /*4*/:
                return ScaleType.FIT_END;
            case dj.f /*5*/:
                return ScaleType.CENTER;
            case ci.g /*6*/:
                return ScaleType.CENTER_CROP;
            case ci.h /*7*/:
                return ScaleType.CENTER_INSIDE;
            default:
                return ScaleType.FIT_CENTER;
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        return who == this.mStartDrawable || who == this.mEndDrawable || super.verifyDrawable(who);
    }

    public void invalidateDrawable(@NonNull Drawable dr) {
        if (dr == this.mStartDrawable || dr == this.mEndDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(dr);
        }
    }
}
