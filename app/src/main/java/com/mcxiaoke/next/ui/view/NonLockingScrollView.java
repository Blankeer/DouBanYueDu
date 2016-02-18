package com.mcxiaoke.next.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.Iterator;

public class NonLockingScrollView extends ScrollView {
    private static final Rect sHitFrame;
    private final ArrayList<View> mChildrenNeedingAllTouches;
    private boolean mInCustomDrag;

    public NonLockingScrollView(Context context) {
        super(context);
        this.mInCustomDrag = false;
        this.mChildrenNeedingAllTouches = new ArrayList();
    }

    public NonLockingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInCustomDrag = false;
        this.mChildrenNeedingAllTouches = new ArrayList();
    }

    public NonLockingScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mInCustomDrag = false;
        this.mChildrenNeedingAllTouches = new ArrayList();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isUp;
        if (ev.getActionMasked() == 1) {
            isUp = true;
        } else {
            isUp = false;
        }
        if (isUp && this.mInCustomDrag) {
            this.mInCustomDrag = false;
            onTouchEvent(ev);
            return true;
        } else if (!this.mInCustomDrag && !isEventOverChild(ev, this.mChildrenNeedingAllTouches)) {
            return super.onInterceptTouchEvent(ev);
        } else {
            this.mInCustomDrag = super.onInterceptTouchEvent(ev);
            if (this.mInCustomDrag) {
                onTouchEvent(ev);
            }
            return false;
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        excludeChildrenFromInterceptions(this);
    }

    private void excludeChildrenFromInterceptions(View node) {
        if (node instanceof WebView) {
            this.mChildrenNeedingAllTouches.add(node);
        } else if (node instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) node;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                excludeChildrenFromInterceptions(viewGroup.getChildAt(i));
            }
        }
    }

    static {
        sHitFrame = new Rect();
    }

    private static boolean isEventOverChild(MotionEvent ev, ArrayList<View> children) {
        int actionIndex = ev.getActionIndex();
        float x = ev.getX(actionIndex);
        float y = ev.getY(actionIndex);
        Iterator i$ = children.iterator();
        while (i$.hasNext()) {
            View child = (View) i$.next();
            if (canViewReceivePointerEvents(child)) {
                child.getHitRect(sHitFrame);
                if (sHitFrame.contains((int) x, (int) y)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean canViewReceivePointerEvents(View child) {
        return child.getVisibility() == 0 || child.getAnimation() != null;
    }
}
