package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.Notification;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903193)
public class SimpleNotificationItemView extends RelativeLayout implements ViewBinder<Notification> {
    @ViewById(2131558770)
    TextView mContent;
    @ViewById(2131558946)
    ImageView mIconUnRead;

    public SimpleNotificationItemView(Context context) {
        super(context);
    }

    public SimpleNotificationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleNotificationItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        ViewUtils.setVerticalPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.general_subview_horizontal_padding_small);
        ThemedAttrs.ofView(this).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_list_item));
        ThemedUtils.updateView(this);
        setGravity(16);
        setDescendantFocusability(393216);
        ViewUtils.of(this).width(-1).height(-2).commit();
    }

    public void bindData(Notification notification) {
        boolean z;
        boolean z2 = true;
        this.mContent.setText(notification.content);
        if (notification.hasRead) {
            z = false;
        } else {
            z = true;
        }
        ViewUtils.visibleIf(z, this.mIconUnRead);
        if (notification.hasRead) {
            z2 = false;
        }
        setActivated(z2);
    }
}
