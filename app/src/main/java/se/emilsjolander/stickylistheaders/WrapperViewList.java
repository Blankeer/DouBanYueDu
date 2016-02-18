package se.emilsjolander.stickylistheaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class WrapperViewList extends ListView {
    private boolean mBlockLayoutChildren;
    private boolean mClippingToPadding;
    private List<View> mFooterViews;
    private LifeCycleListener mLifeCycleListener;
    private Field mSelectorPositionField;
    private Rect mSelectorRect;
    private int mTopClippingLength;

    interface LifeCycleListener {
        void onDispatchDrawOccurred(Canvas canvas);
    }

    public WrapperViewList(Context context) {
        super(context);
        this.mSelectorRect = new Rect();
        this.mClippingToPadding = true;
        this.mBlockLayoutChildren = false;
        try {
            Field selectorRectField = AbsListView.class.getDeclaredField("mSelectorRect");
            selectorRectField.setAccessible(true);
            this.mSelectorRect = (Rect) selectorRectField.get(this);
            if (VERSION.SDK_INT >= 14) {
                this.mSelectorPositionField = AbsListView.class.getDeclaredField("mSelectorPosition");
                this.mSelectorPositionField.setAccessible(true);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
    }

    public boolean performItemClick(View view, int position, long id) {
        if (view instanceof WrapperView) {
            view = ((WrapperView) view).mItem;
        }
        return super.performItemClick(view, position, id);
    }

    private void positionSelectorRect() {
        if (!this.mSelectorRect.isEmpty()) {
            int selectorPosition = getSelectorPosition();
            if (selectorPosition >= 0) {
                View v = getChildAt(selectorPosition - getFixedFirstVisibleItem());
                if (v instanceof WrapperView) {
                    WrapperView wrapper = (WrapperView) v;
                    this.mSelectorRect.top = wrapper.getTop() + wrapper.mItemTop;
                }
            }
        }
    }

    private int getSelectorPosition() {
        if (this.mSelectorPositionField == null) {
            for (int i = 0; i < getChildCount(); i++) {
                if (getChildAt(i).getBottom() == this.mSelectorRect.bottom) {
                    return getFixedFirstVisibleItem() + i;
                }
            }
        } else {
            try {
                return this.mSelectorPositionField.getInt(this);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
        }
        return -1;
    }

    protected void dispatchDraw(Canvas canvas) {
        positionSelectorRect();
        if (this.mTopClippingLength != 0) {
            canvas.save();
            Rect clipping = canvas.getClipBounds();
            clipping.top = this.mTopClippingLength;
            canvas.clipRect(clipping);
            super.dispatchDraw(canvas);
            canvas.restore();
        } else {
            super.dispatchDraw(canvas);
        }
        this.mLifeCycleListener.onDispatchDrawOccurred(canvas);
    }

    void setLifeCycleListener(LifeCycleListener lifeCycleListener) {
        this.mLifeCycleListener = lifeCycleListener;
    }

    public void addFooterView(View v) {
        super.addFooterView(v);
        addInternalFooterView(v);
    }

    public void addFooterView(View v, Object data, boolean isSelectable) {
        super.addFooterView(v, data, isSelectable);
        addInternalFooterView(v);
    }

    private void addInternalFooterView(View v) {
        if (this.mFooterViews == null) {
            this.mFooterViews = new ArrayList();
        }
        this.mFooterViews.add(v);
    }

    public boolean removeFooterView(View v) {
        if (!super.removeFooterView(v)) {
            return false;
        }
        this.mFooterViews.remove(v);
        return true;
    }

    boolean containsFooterView(View v) {
        if (this.mFooterViews == null) {
            return false;
        }
        return this.mFooterViews.contains(v);
    }

    void setTopClippingLength(int topClipping) {
        this.mTopClippingLength = topClipping;
    }

    int getFixedFirstVisibleItem() {
        int firstVisibleItem = getFirstVisiblePosition();
        if (VERSION.SDK_INT >= 11) {
            return firstVisibleItem;
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getBottom() >= 0) {
                firstVisibleItem += i;
                break;
            }
        }
        if (!this.mClippingToPadding && getPaddingTop() > 0 && firstVisibleItem > 0 && getChildAt(0).getTop() > 0) {
            firstVisibleItem--;
        }
        return firstVisibleItem;
    }

    public void setClipToPadding(boolean clipToPadding) {
        this.mClippingToPadding = clipToPadding;
        super.setClipToPadding(clipToPadding);
    }

    public void setBlockLayoutChildren(boolean block) {
        this.mBlockLayoutChildren = block;
    }

    protected void layoutChildren() {
        if (!this.mBlockLayoutChildren) {
            super.layoutChildren();
        }
    }
}
