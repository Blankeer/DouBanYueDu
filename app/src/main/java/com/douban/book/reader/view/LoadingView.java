package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903166)
public class LoadingView extends RelativeLayout {
    @ViewById(2131558876)
    ProgressBar mBlockProgressBar;
    @ViewById(2131558875)
    ProgressBar mNormalProgressBar;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        setId(R.id.view_loading);
        ViewUtils.of(this).height(-1).width(-1).commit();
    }

    public LoadingView blockPage(boolean blockPage) {
        boolean z;
        setClickable(blockPage);
        ViewUtils.showIf(blockPage, this.mBlockProgressBar);
        if (blockPage) {
            z = false;
        } else {
            z = true;
        }
        ViewUtils.showIf(z, this.mNormalProgressBar);
        return this;
    }
}
