package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.UserAvatarView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903162)
public class GiftRecipientItemView extends LinearLayout implements ViewBinder<Gift> {
    @ViewById(2131558530)
    UserAvatarView mAvatar;
    @ViewById(2131558866)
    TextView mReceivedTime;
    @ViewById(2131558615)
    TextView mUserName;

    public GiftRecipientItemView(Context context) {
        super(context);
    }

    public GiftRecipientItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GiftRecipientItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init() {
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.page_horizontal_padding);
        ViewUtils.setVerticalPaddingResId(this, R.dimen.general_subview_vertical_padding_small);
    }

    public void bindData(Gift data) {
        this.mAvatar.displayUserAvatar(data.recipient);
        this.mUserName.setText(data.recipient.name);
        this.mReceivedTime.setText(DateUtils.formatDateTime(data.createTime));
    }
}
