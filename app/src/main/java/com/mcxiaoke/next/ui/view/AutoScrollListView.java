package com.mcxiaoke.next.ui.view;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.ListView;

public class AutoScrollListView extends ListView {
    private static final float PREFERRED_SELECTION_OFFSET_FROM_TOP = 0.33f;
    private int mRequestedScrollPosition;
    private boolean mSmoothScrollRequested;

    public AutoScrollListView(Context context) {
        super(context);
        this.mRequestedScrollPosition = -1;
    }

    public AutoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRequestedScrollPosition = -1;
    }

    public AutoScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRequestedScrollPosition = -1;
    }

    public void requestPositionToScreen(int position, boolean smoothScroll) {
        this.mRequestedScrollPosition = position;
        this.mSmoothScrollRequested = smoothScroll;
        requestLayout();
    }

    protected void layoutChildren() {
        super.layoutChildren();
        if (this.mRequestedScrollPosition != -1) {
            int position = this.mRequestedScrollPosition;
            this.mRequestedScrollPosition = -1;
            int firstPosition = getFirstVisiblePosition() + 1;
            int lastPosition = getLastVisiblePosition();
            if (position < firstPosition || position > lastPosition) {
                int offset = (int) (((float) getHeight()) * PREFERRED_SELECTION_OFFSET_FROM_TOP);
                if (this.mSmoothScrollRequested) {
                    int twoScreens = (lastPosition - firstPosition) * 2;
                    int preliminaryPosition;
                    if (position < firstPosition) {
                        preliminaryPosition = position + twoScreens;
                        if (preliminaryPosition >= getCount()) {
                            preliminaryPosition = getCount() - 1;
                        }
                        if (preliminaryPosition < firstPosition) {
                            setSelection(preliminaryPosition);
                            super.layoutChildren();
                        }
                    } else {
                        preliminaryPosition = position - twoScreens;
                        if (preliminaryPosition < 0) {
                            preliminaryPosition = 0;
                        }
                        if (preliminaryPosition > lastPosition) {
                            setSelection(preliminaryPosition);
                            super.layoutChildren();
                        }
                    }
                    smoothScrollToPositionCompat(this, position, offset);
                    return;
                }
                setSelectionFromTop(position, offset);
                super.layoutChildren();
            }
        }
    }

    public static void smoothScrollToPositionCompat(ListView listView, int position, int offset) {
        if (VERSION.SDK_INT >= 11) {
            listView.smoothScrollToPositionFromTop(position, offset);
        }
        int firstVisible = listView.getFirstVisiblePosition();
        int lastVisible = listView.getLastVisiblePosition();
        if (position < firstVisible) {
            listView.smoothScrollToPosition(position);
        } else {
            listView.smoothScrollToPosition(((position + lastVisible) - firstVisible) - 2);
        }
    }
}
