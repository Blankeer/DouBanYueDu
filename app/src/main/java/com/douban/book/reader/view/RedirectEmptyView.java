package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.HomeActivity;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.fragment.StoreFragment_;
import com.douban.book.reader.util.RichText;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903179)
public class RedirectEmptyView extends RelativeLayout {
    @ViewById(2131558923)
    Button mBtnRedirect;
    @ViewById(2131558644)
    TextView mHint;

    public RedirectEmptyView(Context context) {
        super(context);
    }

    public RedirectEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RedirectEmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        setGravity(14);
        this.mBtnRedirect.setText(RichText.textWithIcon((int) R.drawable.v_store, (int) R.string.title_works_insert_from_store));
    }

    public RedirectEmptyView hint(int resId) {
        this.mHint.setText(resId);
        return this;
    }

    @Click({2131558923})
    void onBtnRedirectClicked() {
        HomeActivity.showContent(PageOpenHelper.from((View) this), StoreFragment_.class);
    }
}
