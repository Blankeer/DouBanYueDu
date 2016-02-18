package com.douban.book.reader.view.store.card.content;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.entity.store.MoreWorksStoreWidgetEntity;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.CoverLeftWorksView;
import com.douban.book.reader.view.CoverLeftWorksView_;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class MoreWorksWidgetContentView extends LinearLayout {
    public MoreWorksWidgetContentView(Context context) {
        super(context);
    }

    public MoreWorksWidgetContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoreWorksWidgetContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        ViewUtils.of(this).width(-1).height(-2).commit();
        ViewUtils.setTopPaddingResId(this, R.dimen.general_subview_vertical_padding_small);
        ViewUtils.setBottomPaddingResId(this, R.dimen.general_subview_vertical_padding_large);
        setOrientation(1);
        setGravity(1);
        ThemedAttrs.ofView(this).append(R.attr.dividerArray, Integer.valueOf(R.array.bg_divider_horizontal)).updateView();
        setShowDividers(2);
        setDividerPadding(1);
    }

    public void setStoreWidgetEntity(MoreWorksStoreWidgetEntity storeWidgetEntity) {
        if (storeWidgetEntity != null && storeWidgetEntity.payload != null) {
            List<Works> worksList = storeWidgetEntity.payload.worksList;
            if (worksList != null) {
                for (int index = worksList.size() - 1; index >= 0; index--) {
                    CoverLeftWorksView worksView = CoverLeftWorksView_.build(App.get());
                    worksView.bindData((Works) worksList.get(index));
                    worksView.showAbstract();
                    worksView.showRatingInfo(true);
                    addView(worksView, 0);
                }
            }
        }
    }
}
