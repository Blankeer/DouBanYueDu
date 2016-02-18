package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.RelativeLayout;

public class GridItemView extends RelativeLayout {
    private int mGridIndex;

    public static class GridItemViewContextMenuInfo implements ContextMenuInfo {
        public long gridIndex;
        public int id;

        public GridItemViewContextMenuInfo(int id, int gridIndex) {
            this.id = id;
            this.gridIndex = (long) gridIndex;
        }
    }

    public GridItemView(Context context) {
        super(context);
    }

    public GridItemView(Context context, AttributeSet attrset) {
        super(context, attrset);
    }

    public void setGridIndex(int gridIndex) {
        this.mGridIndex = gridIndex;
    }

    public int getGridIndex() {
        return this.mGridIndex;
    }

    protected ContextMenuInfo getContextMenuInfo() {
        return new GridItemViewContextMenuInfo(getId(), getGridIndex());
    }
}
