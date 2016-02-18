package com.douban.book.reader.view.card;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.WorksKind;
import com.douban.book.reader.manager.WorksKindManager;
import com.douban.book.reader.manager.WorksKindManager_;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ExpandableHeightGridView;
import com.douban.book.reader.view.item.WorksKindTextItemView;
import com.douban.book.reader.view.item.WorksKindTextItemView_;
import java.util.List;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class WorksKindGridView extends ExpandableHeightGridView {
    private static final int GRID_NUM_COLUMN = 2;
    private static final String TAG;
    private int mNumColumn;
    private boolean mShowWorksKindCount;
    private int mWorksKindId;
    private WorksKindManager mWorksKindManager;

    /* renamed from: com.douban.book.reader.view.card.WorksKindGridView.1 */
    class AnonymousClass1 extends ViewBinderAdapter<WorksKind> {
        AnonymousClass1(Class type) {
            super(type);
        }

        protected void bindView(View itemView, WorksKind data) {
            ((WorksKindTextItemView) itemView).showWorksKindCount(WorksKindGridView.this.mShowWorksKindCount);
            super.bindView(itemView, data);
        }
    }

    static {
        TAG = WorksKindGridView.class.getSimpleName();
    }

    public WorksKindGridView(Context context) {
        super(context);
        this.mWorksKindManager = WorksKindManager_.getInstance_(App.get());
        this.mShowWorksKindCount = true;
        init();
    }

    public WorksKindGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mWorksKindManager = WorksKindManager_.getInstance_(App.get());
        this.mShowWorksKindCount = true;
        init();
    }

    public WorksKindGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mWorksKindManager = WorksKindManager_.getInstance_(App.get());
        this.mShowWorksKindCount = true;
        init();
    }

    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        this.mNumColumn = numColumns;
    }

    private void init() {
        setNumColumns(GRID_NUM_COLUMN);
        setHorizontalSpacing(-1);
        setVerticalSpacing(-1);
        setBackgroundColor(0);
        setSelector(new StateListDrawable());
        ViewUtils.setTopPaddingResId(this, R.dimen.height_horizontal_divider);
        setExpanded(true);
    }

    public WorksKindGridView worksKindId(int id) {
        this.mWorksKindId = id;
        try {
            setWorksKindList(this.mWorksKindManager.getChildList(id));
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        return this;
    }

    public void setShowWorksKindCount(boolean showWorksKindCount) {
        this.mShowWorksKindCount = showWorksKindCount;
    }

    public void setWorksKindList(List<WorksKind> kindList) {
        ViewBinderAdapter<WorksKind> adapter = new AnonymousClass1(WorksKindTextItemView_.class);
        int dummyNum = (this.mNumColumn - (kindList.size() % this.mNumColumn)) % this.mNumColumn;
        for (int index = 0; index < dummyNum; index++) {
            kindList.add(new WorksKind());
        }
        adapter.addAll(kindList);
        setAdapter(adapter);
        setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PageOpenHelper.from(WorksKindGridView.this).open(((WorksKind) parent.getAdapter().getItem(position)).uri);
            }
        });
    }
}
