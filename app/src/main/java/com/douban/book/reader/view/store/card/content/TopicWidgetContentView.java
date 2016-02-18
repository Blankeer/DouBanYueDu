package com.douban.book.reader.view.store.card.content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.store.BaseStoreWidgetEntity.LinkButton;
import com.douban.book.reader.entity.store.TopicStoreWidgetEntity;
import com.douban.book.reader.helper.WorksListUri.Display;
import com.douban.book.reader.util.IterableUtils;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.store.WorksGridView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903195)
public class TopicWidgetContentView extends LinearLayout {
    @ViewById(2131558948)
    Button mBtnMore;
    @ViewById(2131558525)
    WorksGridView mTopWorks;

    /* renamed from: com.douban.book.reader.view.store.card.content.TopicWidgetContentView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ LinkButton val$moreBtn;

        AnonymousClass1(LinkButton linkButton) {
            this.val$moreBtn = linkButton;
        }

        public void onClick(View v) {
            PageOpenHelper.from(TopicWidgetContentView.this).open(this.val$moreBtn.uri);
        }
    }

    public TopicWidgetContentView(Context context) {
        super(context);
        init();
    }

    public TopicWidgetContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopicWidgetContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(1);
        setGravity(1);
        ViewUtils.of(this).width(-1).height(-2).commit();
        ViewUtils.setBottomPaddingResId(this, R.dimen.general_subview_vertical_padding_large);
    }

    public void setTopicWidget(TopicStoreWidgetEntity topicWidget) {
        this.mTopWorks.showPrice(IterableUtils.containsAny(topicWidget.payload.display, String.valueOf(Display.PRICE)));
        this.mTopWorks.setWorksList(topicWidget.payload.worksList);
        LinkButton moreBtn = topicWidget.payload.moreBtn;
        if (moreBtn != null) {
            if (StringUtils.isNotEmpty(moreBtn.text)) {
                this.mBtnMore.setVisibility(0);
                this.mBtnMore.setText(moreBtn.text);
                this.mBtnMore.setOnClickListener(new AnonymousClass1(moreBtn));
            }
        }
        invalidate();
    }
}
