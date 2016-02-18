package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.WorksKind;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903203)
public class WorksKindTextItemView extends RelativeLayout implements ViewBinder<WorksKind> {
    private boolean mShowWorksKindCount;
    @ViewById(2131558969)
    TextView mWorksKind;

    public WorksKindTextItemView(Context context) {
        super(context);
        init();
    }

    public WorksKindTextItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WorksKindTextItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        ViewUtils.of(this).width(-1).height(-1).commit();
        setGravity(17);
        ThemedAttrs.ofView(this).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_gridview_item)).updateView();
    }

    public void showWorksKindCount(boolean showWorksKindCount) {
        this.mShowWorksKindCount = showWorksKindCount;
    }

    public void bindData(WorksKind worksKind) {
        if (!StringUtils.isNotEmpty(worksKind.name)) {
            return;
        }
        if (StringUtils.equals(Res.getString(R.string.btn_more), worksKind.name)) {
            this.mWorksKind.setText(R.string.more);
        } else if (this.mShowWorksKindCount) {
            this.mWorksKind.setText(String.format("%s  %s", new Object[]{worksKind.name, Integer.valueOf(worksKind.worksCount)}));
        } else {
            this.mWorksKind.setText(worksKind.name);
        }
    }
}
