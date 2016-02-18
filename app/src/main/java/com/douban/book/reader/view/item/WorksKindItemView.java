package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.WorksKind;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.card.WorksKindGridView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903202)
public class WorksKindItemView extends LinearLayout implements ViewBinder<WorksKind> {
    private WorksKindGridView mChildrenGridView;
    @ViewById(2131558462)
    TextView mTitle;
    private WorksKind mWorksKind;

    public WorksKindItemView(Context context) {
        super(context);
        init();
    }

    public WorksKindItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WorksKindItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setFocusable(false);
        setDescendantFocusability(393216);
        setOrientation(1);
        setGravity(16);
    }

    public void bindData(WorksKind worksKind) {
        this.mWorksKind = worksKind;
        this.mTitle.setText(worksKind.name);
        refreshChildrenCard();
    }

    private void refreshChildrenCard() {
        if (this.mWorksKind == null || this.mWorksKind.childIds == null || this.mWorksKind.childIds.length <= 0) {
            if (this.mChildrenGridView != null) {
                this.mChildrenGridView.setVisibility(8);
            }
            ViewUtils.setTextAppearance(getContext(), this.mTitle, R.style.AppWidget_Text_Clickable);
            return;
        }
        if (this.mChildrenGridView == null) {
            this.mChildrenGridView = new WorksKindGridView(getContext());
            addView(this.mChildrenGridView);
        }
        this.mChildrenGridView.worksKindId(this.mWorksKind.id);
        this.mChildrenGridView.setVisibility(0);
        ViewUtils.setTextAppearance(getContext(), this.mTitle, R.style.AppWidget_Text_Title_Column);
    }
}
