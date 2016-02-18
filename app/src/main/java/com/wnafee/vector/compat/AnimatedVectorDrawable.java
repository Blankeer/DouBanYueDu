package com.wnafee.vector.compat;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.wnafee.vector.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimatedVectorDrawable extends DrawableCompat implements Animatable, Tintable {
    private static final String ANIMATED_VECTOR = "animated-vector";
    private static final boolean DBG_ANIMATION_VECTOR_DRAWABLE = false;
    private static final String LOGTAG;
    private static final String TARGET = "target";
    private AnimatedVectorDrawableState mAnimatedVectorState;
    private boolean mMutated;

    private static class AnimatedVectorDrawableState extends ConstantState {
        ArrayList<Animator> mAnimators;
        int mChangingConfigurations;
        ArrayMap<Animator, String> mTargetNameMap;
        VectorDrawable mVectorDrawable;

        public AnimatedVectorDrawableState(AnimatedVectorDrawableState copy) {
            if (copy != null) {
                this.mChangingConfigurations = copy.mChangingConfigurations;
                if (copy.mVectorDrawable != null) {
                    this.mVectorDrawable = (VectorDrawable) copy.mVectorDrawable.getConstantState().newDrawable();
                    this.mVectorDrawable.mutate();
                    this.mVectorDrawable.setAllowCaching(AnimatedVectorDrawable.DBG_ANIMATION_VECTOR_DRAWABLE);
                    this.mVectorDrawable.setBounds(copy.mVectorDrawable.getBounds());
                }
                if (copy.mAnimators != null) {
                    int numAnimators = copy.mAnimators.size();
                    this.mAnimators = new ArrayList(numAnimators);
                    this.mTargetNameMap = new ArrayMap(numAnimators);
                    for (int i = 0; i < numAnimators; i++) {
                        Animator anim = (Animator) copy.mAnimators.get(i);
                        Animator animClone = anim.clone();
                        String targetName = (String) copy.mTargetNameMap.get(anim);
                        animClone.setTarget(this.mVectorDrawable.getTargetByName(targetName));
                        this.mAnimators.add(animClone);
                        this.mTargetNameMap.put(animClone, targetName);
                    }
                    return;
                }
                return;
            }
            this.mVectorDrawable = new VectorDrawable();
        }

        public Drawable newDrawable() {
            return new AnimatedVectorDrawable(null, null, null);
        }

        public Drawable newDrawable(Resources res) {
            return new AnimatedVectorDrawable(res, null, null);
        }

        public Drawable newDrawable(Resources res, Theme theme) {
            return new AnimatedVectorDrawable(res, theme, null);
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }

    static {
        LOGTAG = AnimatedVectorDrawable.class.getSimpleName();
    }

    public AnimatedVectorDrawable() {
        this.mAnimatedVectorState = new AnimatedVectorDrawableState(null);
    }

    private AnimatedVectorDrawable(AnimatedVectorDrawableState state, Resources res, Theme theme) {
        this.mAnimatedVectorState = new AnimatedVectorDrawableState(state);
        if (theme != null && canApplyTheme()) {
            applyTheme(theme);
        }
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mAnimatedVectorState.mVectorDrawable.mutate();
            this.mMutated = true;
        }
        return this;
    }

    public ConstantState getConstantState() {
        this.mAnimatedVectorState.mChangingConfigurations = getChangingConfigurations();
        return this.mAnimatedVectorState;
    }

    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mAnimatedVectorState.mChangingConfigurations;
    }

    public void draw(Canvas canvas) {
        this.mAnimatedVectorState.mVectorDrawable.draw(canvas);
        if (isStarted()) {
            invalidateSelf();
        }
    }

    protected void onBoundsChange(Rect bounds) {
        this.mAnimatedVectorState.mVectorDrawable.setBounds(bounds);
    }

    protected boolean onStateChange(int[] state) {
        return this.mAnimatedVectorState.mVectorDrawable.setState(state);
    }

    protected boolean onLevelChange(int level) {
        return this.mAnimatedVectorState.mVectorDrawable.setLevel(level);
    }

    public int getAlpha() {
        return this.mAnimatedVectorState.mVectorDrawable.getAlpha();
    }

    public void setAlpha(int alpha) {
        this.mAnimatedVectorState.mVectorDrawable.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mAnimatedVectorState.mVectorDrawable.setColorFilter(colorFilter);
    }

    public void setTintList(ColorStateList tint) {
        this.mAnimatedVectorState.mVectorDrawable.setTintList(tint);
    }

    public void setHotspot(float x, float y) {
        this.mAnimatedVectorState.mVectorDrawable.setHotspot(x, y);
    }

    public void setHotspotBounds(int left, int top, int right, int bottom) {
        this.mAnimatedVectorState.mVectorDrawable.setHotspotBounds(left, top, right, bottom);
    }

    public void setTintMode(Mode tintMode) {
        this.mAnimatedVectorState.mVectorDrawable.setTintMode(tintMode);
    }

    public boolean setVisible(boolean visible, boolean restart) {
        this.mAnimatedVectorState.mVectorDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    public void setLayoutDirection(int layoutDirection) {
        this.mAnimatedVectorState.mVectorDrawable.setLayoutDirection(layoutDirection);
    }

    public boolean isStateful() {
        return this.mAnimatedVectorState.mVectorDrawable.isStateful();
    }

    public int getOpacity() {
        return this.mAnimatedVectorState.mVectorDrawable.getOpacity();
    }

    public int getIntrinsicWidth() {
        return this.mAnimatedVectorState.mVectorDrawable.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        return this.mAnimatedVectorState.mVectorDrawable.getIntrinsicHeight();
    }

    public void getOutline(@NonNull Outline outline) {
        this.mAnimatedVectorState.mVectorDrawable.getOutline(outline);
    }

    public static AnimatedVectorDrawable getDrawable(Context c, int resId) {
        return create(c, c.getResources(), resId);
    }

    public static AnimatedVectorDrawable create(Context c, Resources resources, int rid) {
        try {
            int type;
            XmlPullParser parser = resources.getXml(rid);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            do {
                type = parser.next();
                if (type == 2) {
                    break;
                }
            } while (type != 1);
            if (type != 2) {
                throw new XmlPullParserException("No start tag found");
            } else if (ANIMATED_VECTOR.equals(parser.getName())) {
                AnimatedVectorDrawable drawable = new AnimatedVectorDrawable();
                drawable.inflate(c, resources, parser, attrs, null);
                return drawable;
            } else {
                throw new IllegalArgumentException("root node must start with: animated-vector");
            }
        } catch (XmlPullParserException e) {
            Log.e(LOGTAG, "parser error", e);
            return null;
        } catch (IOException e2) {
            Log.e(LOGTAG, "parser error", e2);
            return null;
        }
    }

    public void inflate(Context c, Resources res, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        float pathErrorScale = 1.0f;
        while (eventType != 1) {
            if (eventType == 2) {
                String tagName = parser.getName();
                TypedArray a;
                if (ANIMATED_VECTOR.equals(tagName)) {
                    a = DrawableCompat.obtainAttributes(res, theme, attrs, R.styleable.AnimatedVectorDrawable);
                    int drawableRes = a.getResourceId(R.styleable.AnimatedVectorDrawable_android_drawable, 0);
                    if (drawableRes != 0) {
                        VectorDrawable vectorDrawable = (VectorDrawable) VectorDrawable.create(res, drawableRes).mutate();
                        vectorDrawable.setAllowCaching(DBG_ANIMATION_VECTOR_DRAWABLE);
                        pathErrorScale = vectorDrawable.getPixelSize();
                        this.mAnimatedVectorState.mVectorDrawable = vectorDrawable;
                    }
                    a.recycle();
                } else if (TARGET.equals(tagName)) {
                    a = DrawableCompat.obtainAttributes(res, theme, attrs, R.styleable.AnimatedVectorDrawableTarget);
                    String target = a.getString(R.styleable.AnimatedVectorDrawableTarget_android_name);
                    int id = a.getResourceId(R.styleable.AnimatedVectorDrawableTarget_android_animation, 0);
                    if (id != 0) {
                        Animator objectAnimator;
                        if (isPath(target)) {
                            objectAnimator = getPathAnimator(c, res, theme, id, pathErrorScale);
                        } else {
                            objectAnimator = AnimatorInflater.loadAnimator(c, id);
                        }
                        setupAnimatorsForTarget(target, objectAnimator);
                    }
                    a.recycle();
                }
            }
            eventType = parser.next();
        }
    }

    public boolean isPath(String target) {
        return this.mAnimatedVectorState.mVectorDrawable.getTargetByName(target) instanceof VFullPath;
    }

    Animator getPathAnimator(Context c, Resources res, Theme theme, int id, float pathErrorScale) {
        return PathAnimatorInflater.loadAnimator(c, res, theme, id, pathErrorScale);
    }

    public boolean canApplyTheme() {
        return (super.canApplyTheme() || !(this.mAnimatedVectorState == null || this.mAnimatedVectorState.mVectorDrawable == null || !this.mAnimatedVectorState.mVectorDrawable.canApplyTheme())) ? true : DBG_ANIMATION_VECTOR_DRAWABLE;
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        VectorDrawable vectorDrawable = this.mAnimatedVectorState.mVectorDrawable;
        if (vectorDrawable != null && vectorDrawable.canApplyTheme()) {
            vectorDrawable.applyTheme(t);
        }
    }

    private void setupAnimatorsForTarget(String name, Animator animator) {
        animator.setTarget(this.mAnimatedVectorState.mVectorDrawable.getTargetByName(name));
        if (this.mAnimatedVectorState.mAnimators == null) {
            this.mAnimatedVectorState.mAnimators = new ArrayList();
            this.mAnimatedVectorState.mTargetNameMap = new ArrayMap();
        }
        this.mAnimatedVectorState.mAnimators.add(animator);
        this.mAnimatedVectorState.mTargetNameMap.put(animator, name);
    }

    public boolean isRunning() {
        ArrayList<Animator> animators = this.mAnimatedVectorState.mAnimators;
        int size = animators.size();
        for (int i = 0; i < size; i++) {
            if (((Animator) animators.get(i)).isRunning()) {
                return true;
            }
        }
        return DBG_ANIMATION_VECTOR_DRAWABLE;
    }

    private boolean isStarted() {
        ArrayList<Animator> animators = this.mAnimatedVectorState.mAnimators;
        int size = animators.size();
        for (int i = 0; i < size; i++) {
            if (((Animator) animators.get(i)).isStarted()) {
                return true;
            }
        }
        return DBG_ANIMATION_VECTOR_DRAWABLE;
    }

    public void start() {
        ArrayList<Animator> animators = this.mAnimatedVectorState.mAnimators;
        int size = animators.size();
        for (int i = 0; i < size; i++) {
            Animator animator = (Animator) animators.get(i);
            if (!animator.isStarted()) {
                animator.start();
            }
        }
        invalidateSelf();
    }

    public void stop() {
        ArrayList<Animator> animators = this.mAnimatedVectorState.mAnimators;
        int size = animators.size();
        for (int i = 0; i < size; i++) {
            ((Animator) animators.get(i)).end();
        }
    }

    public void reverse() {
        ArrayList<Animator> animators = this.mAnimatedVectorState.mAnimators;
        int size = animators.size();
        for (int i = 0; i < size; i++) {
            Animator animator = (Animator) animators.get(i);
            if (canReverse(animator)) {
                reverse(animator);
            } else {
                Log.w(LOGTAG, "AnimatedVectorDrawable can't reverse()");
            }
        }
    }

    public boolean canReverse() {
        ArrayList<Animator> animators = this.mAnimatedVectorState.mAnimators;
        int size = animators.size();
        for (int i = 0; i < size; i++) {
            if (!canReverse((Animator) animators.get(i))) {
                return DBG_ANIMATION_VECTOR_DRAWABLE;
            }
        }
        return true;
    }

    public static boolean canReverse(Animator a) {
        if (a instanceof AnimatorSet) {
            Iterator i$ = ((AnimatorSet) a).getChildAnimations().iterator();
            while (i$.hasNext()) {
                if (!canReverse((Animator) i$.next())) {
                    return DBG_ANIMATION_VECTOR_DRAWABLE;
                }
            }
            return DBG_ANIMATION_VECTOR_DRAWABLE;
        } else if (a instanceof ValueAnimator) {
            return true;
        } else {
            return DBG_ANIMATION_VECTOR_DRAWABLE;
        }
    }

    private void reverse(Animator a) {
        if (a instanceof AnimatorSet) {
            Iterator i$ = ((AnimatorSet) a).getChildAnimations().iterator();
            while (i$.hasNext()) {
                reverse((Animator) i$.next());
            }
        } else if (a instanceof ValueAnimator) {
            ((ValueAnimator) a).reverse();
        }
    }
}
