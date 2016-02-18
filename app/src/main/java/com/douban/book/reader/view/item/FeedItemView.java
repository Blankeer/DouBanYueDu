package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.Feed;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.WorksCoverView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903158)
public class FeedItemView extends RelativeLayout implements ViewBinder<Feed> {
    @ViewById(2131558770)
    TextView mContent;
    @ViewById(2131558772)
    WorksCoverView mCover;
    @ViewById(2131558532)
    TextView mPublishInfo;
    @ViewById(2131558462)
    TextView mTitle;

    public FeedItemView(Context context) {
        super(context);
    }

    public FeedItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        ViewUtils.setVerticalPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.page_horizontal_padding);
        ThemedAttrs.ofView(this).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_list_item));
        setGravity(16);
        setDescendantFocusability(393216);
        ViewUtils.of(this).width(-1).height(-2).commit();
    }

    public void bindData(Feed msg) {
        boolean z;
        this.mPublishInfo.setText(Res.getString(R.string.msg_publish_info, DateUtils.formatDate(msg.publishTime), msg.payload.worksTitle));
        this.mTitle.setText(msg.title);
        this.mCover.url(msg.cover);
        this.mContent.setText(msg.desc);
        this.mTitle.setTypeface(msg.hasRead ? Font.SANS_SERIF : Font.SANS_SERIF_BOLD);
        if (msg.hasRead) {
            z = false;
        } else {
            z = true;
        }
        setActivated(z);
    }
}
